package com.demets.jas.ui.main.unauthorised

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.demets.jas.AppSettings
import com.demets.jas.R
import com.demets.jas.ui.authorization.AuthActivity
import com.demets.jas.ui.main.authorised.AuthorizedFragment
import com.demets.jas.utils.NotificationUtil
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_unauthorized.buttonSignIn
import kotlinx.android.synthetic.main.fragment_unauthorized.loadingPanel

/**
 * Created by dmitr on 06.02.2018.
 */
class UnauthorizedFragment : MvpAppCompatFragment(), IUnauthorizedView {

    var disposable: Disposable? = null

    @InjectPresenter
    lateinit var presenter: UnauthorisedPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_unauthorized, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSignIn.setOnClickListener {
            disposable = presenter
                .getAuthToken()
                .doOnSubscribe { presenter.showLoader() }
                .doOnTerminate { presenter.hideLoader() }
                .subscribe(
                    {
                        //save token
                        presenter.accessRequested = true
                        AppSettings.setToken(activity, it)
                        //open auth page with received token
                        val intent = Intent(activity, AuthActivity::class.java)
                        intent.putExtra(AuthActivity.TOKEN_KEY, it)
                        startActivity(intent)
                    },
                    {
                        presenter.showError(getString(R.string.error_connection_message))
                    })
        }
    }

    override fun onResume() {
        super.onResume()
        if (presenter.accessRequested) {
            val token = AppSettings.getToken(activity)
            if (token.isNotEmpty()) {
                presenter.getAuthSession(token)
                    .doOnSubscribe { presenter.showLoader() }
                    .doOnTerminate { presenter.hideLoader() }
                    .subscribe(
                        {
                            AppSettings.setSessionKey(activity, it.key)
                            AppSettings.setUsername(activity, it.name)
                            val successMessage =
                                String.format(getString(R.string.message_login_success), it.name)
                            showToast(activity, successMessage)
                            if (AppSettings.getSessionKey(activity).isNotEmpty()) {
                                fragmentManager
                                    ?.beginTransaction()
                                    ?.replace(android.R.id.content,
                                              AuthorizedFragment()
                                    )
                                    ?.commit()
                                NotificationUtil.restoreNowPlaying(context!!)
                            }
                        },
                        {
                            presenter.showError(getString(R.string.error_connection_message))
                        })
            } else {
                showToast(activity, getString(R.string.auth_error_message))
            }
            presenter.accessRequested = false
        }
    }

    private fun showToast(context: Context?, text: String) =
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun showLoader() {
        loadingPanel.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        loadingPanel.visibility = View.GONE
    }

    override fun showErrorToast(message: String) {
        showToast(activity, message)
    }
}