package com.demets.jas.ui.tracks

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.demets.jas.db.room.TrackEntity

@StateStrategyType(AddToEndSingleStrategy::class)
interface ITracksView : MvpView {
    fun updateView(tracks: List<TrackEntity>)
}