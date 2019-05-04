package com.demets.jas.ui.authorization

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.demets.jas.R
import com.demets.jas.androidx.moxy.MvpAppCompatActivity
import kotlinx.android.synthetic.main.activity_authorize.loadingPanel
import kotlinx.android.synthetic.main.activity_authorize.webView


class AuthActivity : MvpAppCompatActivity(), IAuthView {

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    private lateinit var sUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)

        val token = if (intent.hasExtra(TOKEN_KEY)) intent.getStringExtra(TOKEN_KEY) else null
        sUrl = "https://www.last.fm/api/auth?api_key=6cda0aac2410defc7d67360134b3e76a&token=$token"
        initWebView()

        val toolbar = supportActionBar

        toolbar?.let {
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.title = ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        runOnUiThread {
            val webSettings = webView.settings

            webSettings.javaScriptEnabled = true
            webSettings.defaultTextEncodingName = "utf-8"
            webSettings.allowContentAccess = true
            webSettings.allowFileAccess = true
            webSettings.setSupportZoom(true)
            webSettings.domStorageEnabled = true
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT

            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true

            webView.webChromeClient = object : WebChromeClient() {

                override fun onPermissionRequest(request: PermissionRequest) {
                    request.grant(request.resources)
                }
            }
            webView.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    presenter.showLoading()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    presenter.hideLoading()
                }
            }
            WebStorage.getInstance().deleteAllData()
            webView.loadUrl(sUrl)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.ACTION_DOWN == event.action) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun showLoading() {
        webView.visibility = View.GONE
        loadingPanel.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        webView.visibility = View.VISIBLE
        loadingPanel.visibility = View.GONE
    }

    companion object {
        const val TOKEN_KEY = "webToken"
    }
}
