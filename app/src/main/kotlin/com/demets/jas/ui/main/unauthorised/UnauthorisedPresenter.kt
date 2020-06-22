package com.demets.jas.ui.main.unauthorised

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.demets.jas.repository.api.LfApiService
import com.demets.jas.repository.api.model.Session
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@InjectViewState
class UnauthorisedPresenter : MvpPresenter<IUnauthorizedView>() {
    private val lfApiService = LfApiService.create()

    var accessRequested = false

    fun showLoader() = viewState.showLoader()

    fun hideLoader() = viewState.hideLoader()

    fun getAuthToken(): Observable<String> {
        return lfApiService.getAuthToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.result }
    }

    fun getAuthSession(token: String): Observable<Session> {
        return lfApiService.getAuthSession(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.result }
    }

    fun showError(message: String) {
        viewState.showErrorToast(message)
    }
}
