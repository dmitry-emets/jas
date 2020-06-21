package com.demets.jas.ui.main.unauthorised

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface IUnauthorizedView : MvpView {
    fun showLoader()
    fun hideLoader()
    fun showErrorToast(message: String)
}
