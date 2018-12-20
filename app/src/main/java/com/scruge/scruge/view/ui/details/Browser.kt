package com.scruge.scruge.view.ui.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.views.NavigationBarButton
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import kotlinx.android.synthetic.main.fragment_browser.*
import android.webkit.WebView
import android.webkit.WebViewClient



class BrowserFragment : NavigationFragment() {

    var url:String? = null
    var campaignVM: CampaignVM? = null
    var updateVM:UpdateVM? = null

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setup()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        shouldHideNavigationBar = false
    }

    private fun setup() {
        @SuppressLint("SetJavaScriptEnabled")
        web_view.settings.javaScriptEnabled = true

        url?.let {
            web_view.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    request?.url?.path?.let { view?.loadUrl(it) }
                    return false
                }
            }
            web_view.loadUrl(it)
        } ?:
        campaignVM?.lastUpdateVM?.let {
            it.loadDescription {
                title = "Pitch"
                setData(it)
            }
        } ?:
        updateVM?.let { vm ->
            vm.loadDescription {
                title = "Update"
                setData(it)

                navigationBarButton = NavigationBarButton(R.drawable.comments) {
                    Service.presenter.presentCommentsViewController(this, vm)
                }
            }
        }
    }

    private fun setData(html:String) {
        web_view.setInitialScale(1)
        web_view.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY;
        web_view.isScrollbarFadingEnabled = false;
        web_view.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null)
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.useWideViewPort = true
    }
}