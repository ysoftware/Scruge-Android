package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.viewmodel.comment.CommentAVM
import kotlinx.android.synthetic.main.cell_comment_top.view.*

class TopCommentCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:CommentAVM?, allCommentsCount:Int):TopCommentCell {
        if (vm != null && vm.isNotEmpty()) {
            val comment = vm.item(0)

            itemView.top_comment_name.text = comment.authorName
            itemView.top_comment_text.text = comment.comment
            itemView.top_comment_date.text = comment.date
            itemView.top_comment_likes.text = comment.likes

            comment.authorPhoto?.let {
                itemView.top_comment_profile_image.setImage(it)
            } ?: itemView.top_comment_profile_image.setImageDrawable(null)

            itemView.top_comment_see_all.text = if (allCommentsCount == 1) "Add your comment"
                else "See all $allCommentsCount comments"

            itemView.top_comment_view.visibility = View.VISIBLE
            itemView.top_comment_no_view.visibility = View.GONE
        }
        else {
            itemView.top_comment_see_all.text = "Add your comment"
            itemView.top_comment_view.visibility = View.GONE
            itemView.top_comment_no_view.visibility = View.VISIBLE
        }
        return this
    }

    fun allComments(tap:()->Unit):TopCommentCell {
        itemView.top_comment_see_all_view.setOnClickListener {
            tap()
        }
        return this
    }
}