package com.demets.jas.ui.main.authorised

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface IAuthorizedView : MvpView {
    fun updateLastFmCountSuccess(pair: Pair<String, String>)
    fun updateLastFmCountFailed(pair: Pair<String, String>)

    fun updateTodayScrobbled(pair: Pair<String, String>)

    fun updateNowPlaying(text: String)
    fun toggleLikeFab(like: Boolean)
    fun showLikeFab()
    fun hideLikeFab()
    fun showRefresher()
    fun hideRefresher()
}
