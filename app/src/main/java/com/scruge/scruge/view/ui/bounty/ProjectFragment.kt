package com.scruge.scruge.view.ui.bounty

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.*
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.DocumentsCell
import com.scruge.scruge.view.cells.SocialAdapter
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.document.DocumentAVM
import kotlinx.android.synthetic.main.fragment_bounty_project.*

class ProjectFragment: NavigationFragment() {

    private var imageUrl: Uri? = null
    private var didLoadMedia = false

    lateinit var projectVM: ProjectVM

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_project, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        (activity as? TabbarActivity)?.tabbarHidden = true
    }

    private fun setupViews() {
        title = projectVM.name
        earn_button.text = R.string.do_earn.string()

        val vm = projectVM

        project_title.text = vm.name
        project_description.text = vm.description

        economies_supply.text = vm.tokenSupply
        economies_inflation.text = vm.inflation

        if (vm.tokenListingDate != null) {
            project_economies_trading_static.text = R.string.label_listing_date.string()
            project_economies_trading_value.text = vm.tokenListingDate!!
        }
        else if (vm.tokenExchange != null) {
            project_economies_trading_static.text = R.string.label_listed_on_exchange.string(vm.tokenExchange!!)
            project_economies_trading_value.setHidden(true)
        }
        else {
            project_economies_trading_static.text = R.string.label_not_listed.string()
            project_economies_trading_value.setHidden(true)
        }

        val url = vm.videoUrl
        if (url != null) {
            setupWebView(url.toString())
        }
        else {
            setupImageView()
        }

        if (vm.social.isEmpty()) {
            about_social.setHidden(true)
        }
        else {
            val socAdapter = SocialAdapter(vm.social)
            about_social.adapter = socAdapter
            about_social.setupForGridLayout(6)
            socAdapter.tap = { social ->
                Service.presenter.presentBrowser(this, social.url)
            }
        }

        if (vm.documents.isEmpty()) {
            project_documents.setHidden(true)
        }
        else {
            val docsVM = DocumentAVM(vm.documents)
            project_documents_recycler_view.setupForVerticalLayout()
            val adapter = DocumentsCell.Adapter(docsVM)
            project_documents_recycler_view.adapter = adapter
            adapter.tap = { docVM ->
                Service.presenter.presentBrowser(this,
                                                 "https://drive.google.com/viewerng/viewer?embedded=true&url=${docVM.documentUrl}",
                                                 docVM.name)
            }
        }
    }

    private fun setupActions() {
        earn_button.setOnClickListener {
            Service.presenter.presentBountiesFragment(this, projectVM)
        }
    }

    private fun setupImageView() {
        project_web_view.visibility = View.GONE
        project_image.visibility = View.VISIBLE
        project_image.setImage(imageUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url:String) {
        if (didLoadMedia) return
        val wv = project_web_view

        // todo setup
        project_web_view.visibility = View.VISIBLE
        project_image.visibility = View.GONE
        wv.settings.javaScriptEnabled = true

        wv.loadUrl(url)
        didLoadMedia = true
    }

}