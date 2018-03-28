package com.demets.jas.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface AuthorizedView : MvpView {
    fun askLastFmCount()
    fun updateLastFmCountSuccess(pair: Pair<String, String>)
    fun updateLastFmCountFailed(pair: Pair<String, String>)

    fun updateTodayScrobbled(pair: Pair<String, String>)

    fun updateNowPlaying(text: String)
    fun toggleLikeFab(like: Boolean)
    fun showLikeFab()
    fun hideLikeFab()
}