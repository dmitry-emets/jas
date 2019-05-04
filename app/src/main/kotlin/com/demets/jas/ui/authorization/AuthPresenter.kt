package com.demets.jas.ui.authorization

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class AuthPresenter : MvpPresenter<IAuthView>() {
    fun showLoading() = viewState.showLoading()
    fun hideLoading() = viewState.hideLoading()
}
