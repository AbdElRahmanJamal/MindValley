package com.mindvalleytask.view.profile


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.mindvalleytask.R
import com.mindvalleytask.view.BaseScreenFragment
import kotlinx.android.synthetic.main.fragment_user_profile_page.*

/**
 * A simple [Fragment] subclass.
 */
class UserProfilePage : BaseScreenFragment() {


    private lateinit var profileURL: String

    override fun getLayoutId() = R.layout.fragment_user_profile_page

    override fun getScreenTitle() = resources.getString(R.string.user_profile_title)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDateFromSafeArgs()
        openProfilePage()
    }

    private fun getDateFromSafeArgs() {
        arguments?.let {
            profileURL = UserProfilePageArgs.fromBundle(it).profileLink
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openProfilePage() {

        profile_page_web_view.loadUrl(profileURL)
        val webSettings = profile_page_web_view.settings
        webSettings.javaScriptEnabled = true
        profile_page_web_view.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                setLoadingIndicatorVisibility(VISIBLE)
                setFragmentContentVisibility(GONE)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                setLoadingIndicatorVisibility(GONE)
                setFragmentContentVisibility(VISIBLE)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                setLoadingIndicatorVisibility(GONE)
                setFragmentContentVisibility(VISIBLE)
            }

        }
    }

}
