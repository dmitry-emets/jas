package com.demets.jas.ui.tracks

import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.demets.jas.R
import kotlinx.android.synthetic.main.track_list.rv_tracks

/**
 * Created by dmitr on 19.02.2018.
 */
class TracksActivity : MvpAppCompatActivity(), TracksView {
    @InjectPresenter
    lateinit var mTracksPresenter: TracksPresenter
    private val mRecyclerView: RecyclerView by lazy { rv_tracks }
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var receiver: BroadcastReceiver

    override fun updateView(cursor: Cursor) {
        mRecyclerView.adapter = TrackAdapter(cursor)
        (mRecyclerView.adapter as TrackAdapter).notifyDataSetChanged()
        //hide or show no tracks message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_list)
        mTracksPresenter.init(applicationContext)

        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    mTracksPresenter.processIntent(intent)
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(TracksPresenter.ACTION_TRACK_SCROBBLED)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}