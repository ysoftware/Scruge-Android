package com.scruge.scruge.view.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.viewmodel.faq.FaqVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : NavigationFragment() {

    var faq: FaqVM? = null
    var milestone: MilestoneVM? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setup()
    }

    private fun setup() {
        faq?.let {
            title = "Frequently answered question"
            detail_title.text = it.question
            detail_text.text = it.answer
        }
        milestone?.let {
            title = "Milestone"
            detail_title.text = "${it.date}\n${it.fundsRelease}"
            detail_text.text = it.description
        }
    }
}