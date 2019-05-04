package com.demets.jas.ui.main.authorised

import android.content.*
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.demets.jas.AppSettings
import com.demets.jas.R
import com.demets.jas.ui.main.authorised.AuthorizedPresenter.Companion.ACTION_TRACK_SCROBBLED
import com.demets.jas.ui.main.authorised.AuthorizedPresenter.Companion.ACTION_TRACK_START
import com.demets.jas.ui.main.authorised.AuthorizedPresenter.Companion.ACTION_TRACK_STOP
import com.demets.jas.ui.main.unauthorised.UnauthorizedFragment
import kotlinx.android.synthetic.main.authorized_fragment.*


/**
 * Created by dmitr on 06.02.2018.
 */
class AuthorizedFragment : MvpAppCompatFragment(), IAuthorizedView {
    override fun onResume() {
        super.onResume()
        if (AppSettings.getSessionKey(activity).isEmpty()) {
            fragmentManager?.beginTransaction()
                ?.replace(android.R.id.content, UnauthorizedFragment())
                ?.commit()
        }
    }

    override fun showLikeFab() {
        fab.visibility = View.VISIBLE
    }

    override fun hideLikeFab() {
        fab.visibility = View.GONE
    }

    override fun toggleLikeFab(like: Boolean) {
        val drawable = if (like) {
            ContextCompat.getDrawable(context!!, R.drawable.love)
        } else {
            ContextCompat.getDrawable(context!!, R.drawable.unlove)
        }
        fab.setImageDrawable(drawable)
    }

    private lateinit var receiver: BroadcastReceiver

    @InjectPresenter
    lateinit var mAuthorizedPresenter: AuthorizedPresenter


    override fun askLastFmCount() {
        mAuthorizedPresenter.getScrobbles(context!!)
    }

    override fun updateLastFmCountSuccess(pair: Pair<String, String>) {
        tv_total_played_count.text = pair.first
        tv_total_played_label.text = getString(R.string.ma_total_played_label)
        tv_total_played_updated.text = pair.second
    }

    override fun updateLastFmCountFailed(pair: Pair<String, String>) {
        tv_total_played_count.text = pair.first
        tv_total_played_label.text = getString(R.string.ma_total_played_label)
        tv_total_played_updated.text = pair.second
        Toast.makeText(activity, getString(R.string.ma_lastfm_update_failed_message), Toast.LENGTH_SHORT).show()
    }

    override fun updateTodayScrobbled(pair: Pair<String, String>) {
        tv_scrobbled_today_count.text = pair.first
        tv_scrobbled_today_updated.text = pair.second
    }

    override fun updateNowPlaying(text: String) {
        tv_now_playing.text = text
    }

    override fun showRefresher() {
        refreshLayout.isRefreshing = true
    }

    override fun hideRefresher() {
        refreshLayout.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.authorized_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    mAuthorizedPresenter.processIntent(intent, context!!)
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_TRACK_START)
            addAction(ACTION_TRACK_STOP)
            addAction(ACTION_TRACK_SCROBBLED)
        }
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.visibility = View.GONE
        fab.setOnClickListener { mAuthorizedPresenter.likePressed(context!!) }
        refreshLayout.setOnRefreshListener { mAuthorizedPresenter.fetchScrobbleCount(context!!) }
        mAuthorizedPresenter.getScrobbles(context!!)
        mAuthorizedPresenter.initNowPlaying(context!!)
        mAuthorizedPresenter.countTodayScrobbled(context!!)
    }
}