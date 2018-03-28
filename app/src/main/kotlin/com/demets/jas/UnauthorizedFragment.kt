package com.demets.jas

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.demets.jas.api.LfApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.unauthorized_fragment.*

/**
 * Created by dmitr on 06.02.2018.
 */
class UnauthorizedFragment : Fragment() {

    var disposable: Disposable? = null
    var accessRequested = false
    lateinit var lfApiService: LfApiService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.unauthorized_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lfApiService = LfApiService.create()

        button_signIn.setOnClickListener {
            disposable = lfApiService.getAuthToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.result }
                    .subscribe({
                        //save token
                        accessRequested = true
                        AppSettings.setToken(activity, it)
                        //open auth page with received token
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://www.last.fm/api/auth?api_key=6cda0aac2410defc7d67360134b3e76a&token=$it")
                        }
                        startActivity(intent)
                    }, {
                        showToast(activity, getString(R.string.error_connection_message))
                    })
        }
    }

    override fun onResume() {
        super.onResume()
        if (accessRequested) {
            val token = AppSettings.getToken(activity)
            if (!token.isEmpty()) {
                lfApiService.getAuthSession(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { it.result }
                        .subscribe({
                            AppSettings.setSessionKey(activity, it.key)
                            AppSettings.setUsername(activity, it.name)
                            val successMessage = String.format(getString(R.string.message_login_success), it.name)
                            showToast(activity, successMessage)
                            if (AppSettings.getSessionKey(activity).isNotEmpty()) {
                                activity.fragmentManager
                                        .beginTransaction()
                                        .replace(android.R.id.content, AuthorizedFragment())
                                        .commit()
                            }
                        }, {
                            showToast(activity, getString(R.string.error_connection_message))
                            //TODO: Different messages for different causes?
                        })
            } else {
                showToast(activity, getString(R.string.auth_error_message))
            }
            accessRequested = false
        }
    }

    private fun showToast(context: Context, text: String) =
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}