package com.scruge.scruge.view.ui.details

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.*
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.CommentCell
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : NavigationFragment(), ArrayViewModelDelegate {

    lateinit var vm: CommentAVM
    var shouldOpenTyping = false
    private val adapter = Adapter(this)
    private val handler = ArrayViewModelUpdateHandler(adapter)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupActions()
        setupTable()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupNavigationBar()
        setupKeyboard()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setupKeyboard() {
        if (shouldOpenTyping) {
            shouldOpenTyping = false
            context?.showKeyboard(comment_field)
        }
    }

    private fun setupTable() {
        comment_recycler_view.setupForVerticalLayout()
        comment_recycler_view.adapter = adapter
    }

    private fun setupActions() {
        comment_refresh_control.setOnRefreshListener { vm.reloadData() }
        comment_send.setOnClickListener {
            if (Service.tokenManager.hasToken) {
                val comment = comment
                        ?: return@setOnClickListener alert(R.string.error_comment_too_short.string())

                comment_send_progress.visibility = View.VISIBLE
                comment_send.visibility = View.GONE

                vm.postComment(comment) { error ->
                    comment_send_progress.visibility = View.GONE
                    comment_send.visibility = View.VISIBLE

                    if (error != null) {
                        alert(error)
                    }
                    else {
                        hideKeyboard()
                        comment_field.setText("")
                        vm.reloadData()
                    }
                }
            }
            else {
                Service.presenter.presentLoginActivity(activity!!) {
                    checkAuthentication()
                    hideKeyboard()
                }
            }
        }
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupNavigationBar() {
        shouldHideNavigationBar = false
        title = R.string.title_comments.string()
    }

    val comment: String?
        get() {
            val string = comment_field.text.toString().trim()
            return if (string.length < 3) null else string
        }

    private fun checkAuthentication() {
        if (Service.tokenManager.hasToken) {
            comment_field.hint = R.string.hint_add_comment.string()
            comment_field.isEnabled = true
            comment_send.text = R.string.do_send.string()
        }
        else {
            comment_field.hint = R.string.hint_sign_in_to_comment.string()
            comment_field.isEnabled = false
            comment_send.text = R.string.do_sign_in.string()
        }
    }

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        activity?.runOnUiThread {
            handler.handle(update)
        }
    }

    override fun didChangeState(state: State) {
        when (state) {
            State.initial -> comment_loading_view.state = ViewState.loading
            State.error -> {
                comment_loading_view.state = ViewState.error
                comment_loading_view.state.errorMessage = ErrorHandler.message(state.errorValue)
                comment_refresh_control.isRefreshing = false
            }
            State.ready -> {
                comment_refresh_control.isRefreshing = false
                if (vm.isEmpty()) {
                    comment_loading_view.state = ViewState.error
                    comment_loading_view.state.errorMessage = R.string.error_no_comments.string()
                }
                else {
                    comment_loading_view.state = ViewState.ready
                }
                checkAuthentication()
            }
            else -> {}
        }
    }

    class Adapter(val fr:CommentsFragment) : RecyclerView.Adapter<CommentCell>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentCell {
            return CommentCell(LayoutInflater.from(parent.context)
                                       .inflate(R.layout.cell_comment, parent, false))
        }

        override fun getItemCount(): Int = fr.vm.numberOfItems

        override fun onBindViewHolder(holder: CommentCell, position: Int) {
            holder.setup(fr.vm.item(position, true))
                    .like {
                        it.like()
                    }
                    .reply {
                        Service.presenter.presentCommentsViewController(fr, fr.vm, it)
                    }
                    .seeAll {
                        Service.presenter.presentCommentsViewController(fr, fr.vm, it, true)
                    }
        }
    }
}