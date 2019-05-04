package com.demets.jas.ui.tracks

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demets.jas.R
import com.demets.jas.db.contract.TrackContract
import com.demets.jas.ui.tracks.TrackAdapter.TrackViewHolder
import kotlinx.android.synthetic.main.track_list_item.view.tv_artist_item
import kotlinx.android.synthetic.main.track_list_item.view.tv_scrobbled_item
import kotlinx.android.synthetic.main.track_list_item.view.tv_track_item

/**
 * Created by dmitr on 20.02.2018.
 */
class TrackAdapter(private var mCursor: Cursor) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.track_list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = mCursor.count

    override fun onBindViewHolder(viewHolder: TrackViewHolder, position: Int) {
        if (!mCursor.moveToPosition(position)) return
        val trackColumnIndex = mCursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACK)
        val track = mCursor.getString(trackColumnIndex)
        val artistColumnIndex = mCursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_ARTIST)
        val artist = mCursor.getString(artistColumnIndex)
        val isScrobbledColumnIndex = mCursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_SCROBBLED)
        val isScrobbled = mCursor.getInt(isScrobbledColumnIndex) == 1
        viewHolder.bind(track, artist, isScrobbled)
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTrackTextView: TextView by lazy { itemView.tv_track_item }
        private val mArtistTextView: TextView by lazy { itemView.tv_artist_item }
        private val mScrobbledTextView: TextView by lazy { itemView.tv_scrobbled_item }

        fun bind(track: String, artist: String, isScrobbled: Boolean) {
            val context = mTrackTextView.context
            mTrackTextView.text = track
            mArtistTextView.text = String.format(context.getString(R.string.tracks_activity_element_artist), artist)
            if (isScrobbled) {
                mScrobbledTextView.text = context.getString(R.string.tracks_activity_element_scrobbled)
            } else {
                mScrobbledTextView.text = context.getString(R.string.tracks_activity_element_not_scrobbled)
            }
        }
    }
}