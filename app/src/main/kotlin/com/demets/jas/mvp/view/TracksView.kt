package com.demets.jas.mvp.view

import android.database.Cursor
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface TracksView : MvpView {
    fun updateView(cursor: Cursor)
}