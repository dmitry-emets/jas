package com.demets.jas.ui.tracks

import android.database.Cursor
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ITracksView : MvpView {
    fun updateView(cursor: Cursor)
}