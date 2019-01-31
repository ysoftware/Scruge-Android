package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.plural
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.scruge.scruge.viewmodel.comment.CommentVM
import kotlinx.android.synthetic.main.cell_comment.view.*
import kotlinx.android.synthetic.main.cell_comment_top.view.*

class TopCommentCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: CommentAVM?, allCommentsCount: Int): TopCommentCell {
        if (vm != null && vm.isNotEmpty()) {
            val comment = vm.item(0)

            itemView.top_comment_name.text = comment.authorName
            itemView.top_comment_text.text = comment.comment
            itemView.top_comment_date.text = comment.date
            itemView.top_comment_likes.text = comment.likes

            comment.authorPhoto?.let {
                itemView.top_comment_profile_image.setImage(it)
            } ?: itemView.top_comment_profile_image.setImageDrawable(null)

            itemView.top_comment_see_all.text = R.plurals.see_all_comments.plural(allCommentsCount,
                                                                                  allCommentsCount.toString())
            itemView.top_comment_view.visibility = View.VISIBLE
            itemView.top_comment_no_view.visibility = View.GONE
        }
        else {
            itemView.top_comment_see_all.text = R.string.do_add_your_comment.string()
            itemView.top_comment_view.visibility = View.GONE
            itemView.top_comment_no_view.visibility = View.VISIBLE
        }
        return this
    }

    fun allComments(tap: () -> Unit): TopCommentCell {
        itemView.top_comment_see_all_view.setOnClickListener {
            tap()
        }
        return this
    }
}

class CommentCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var vm:CommentVM? = null

    fun setup(vm: CommentVM): CommentCell {

        itemView.comment_name.text = vm.authorName
        itemView.comment_text.text = vm.comment
        itemView.comment_date.text = vm.date
        itemView.comment_likes.text = vm.likes

        vm.authorPhoto?.let {
            itemView.comment_profile_image.setImage(it)
        } ?: itemView.comment_profile_image.setImageDrawable(null)


        vm.repliesText?.let {
            itemView.comment_see_all.text = it
            itemView.comment_see_all.setHidden(false)
        } ?: itemView.comment_see_all.setHidden(true)

        itemView.comment_reply.setHidden(!(vm.canReply && Service.tokenManager.hasToken))
        itemView.comment_like_image.setImageResource(
                if (vm.isLiking) R.drawable.like_active else R.drawable.like)

        // actions

        itemView.comment_like_tap.setOnClickListener {
            likeBlock?.invoke(vm)
        }

        itemView.comment_reply.setOnClickListener {
            replyBlock?.invoke(vm)
        }

        itemView.comment_see_all.setOnClickListener {
            seeAllBlock?.invoke(vm)
        }
        return this
    }

    fun like(block:((CommentVM)->Unit)?):CommentCell {
        likeBlock = block
        return this
    }

    fun reply(block:((CommentVM)->Unit)?):CommentCell {
        replyBlock = block
        return this
    }

    fun seeAll(block:((CommentVM)->Unit)?):CommentCell {
        seeAllBlock = block
        return this
    }

    private var likeBlock:((CommentVM)->Unit)? = null
    private var seeAllBlock:((CommentVM)->Unit)? = null
    private var replyBlock:((CommentVM)->Unit)? = null
}