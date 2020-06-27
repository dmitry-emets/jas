package com.demets.jas.ui.tracks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.demets.jas.R
import com.demets.jas.androidx.moxy.MvpAppCompatActivity
import com.demets.jas.repository.api.db.room.TrackEntity
import kotlinx.android.synthetic.main.track_list.*

/**
 * Created by dmitr on 19.02.2018.
 */
class TracksActivity : MvpAppCompatActivity(), ITracksView {
    @InjectPresenter
    lateinit var presenter: TracksPresenter
    private val mRecyclerView: RecyclerView by lazy { rvTracks }
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var receiver: BroadcastReceiver

    override fun updateView(tracks: List<TrackEntity>) {
        mRecyclerView.adapter = TrackAdapter(tracks)
        (mRecyclerView.adapter as TrackAdapter).notifyDataSetChanged()
        changeScreen(tracks.isNotEmpty())
    }

    private fun changeScreen(hasTracks: Boolean) {
        if (hasTracks) {
            noTracksPlaceholder.visibility = View.GONE
            rvTracks.visibility = View.VISIBLE
        } else {
            rvTracks.visibility = View.GONE
            noTracksPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_list)
        presenter.init(applicationContext)

        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    presenter.processIntent(intent)
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