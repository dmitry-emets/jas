package com.demets.jas.ui.tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demets.jas.R
import com.demets.jas.repository.api.db.room.TrackEntity
import com.demets.jas.ui.tracks.TrackAdapter.TrackViewHolder
import kotlinx.android.synthetic.main.track_list_item.view.*

/**
 * Created by dmitr on 20.02.2018.
 */
class TrackAdapter(
        private var tracks: List<TrackEntity>
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.track_list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(viewHolder: TrackViewHolder, position: Int) {
        viewHolder.bind(tracks[position].title, tracks[position].artist, tracks[position].scrobbled)
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTrackTextView: TextView by lazy { itemView.tv_track_item }
        private val mArtistTextView: TextView by lazy { itemView.tv_artist_item }
        private val mScrobbledTextView: TextView by lazy { itemView.tv_scrobbled_item }

        fun bind(track: String, artist: String, isScrobbled: Boolean) {
            val context = mTrackTextView.context
            mTrackTextView.text = track
            mArtistTextView.text =
                String.format(context.getString(R.string.tracks_activity_element_artist), artist)
            if (isScrobbled) {
                mScrobbledTextView.text =
                    context.getString(R.string.tracks_activity_element_scrobbled)
            } else {
                mScrobbledTextView.text =
                    context.getString(R.string.tracks_activity_element_not_scrobbled)
            }
        }
    }
}