package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.comment.CommentAVM

class TopCommentCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:CommentAVM?, allCommentsCount:Int):TopCommentCell {

        return this
    }

    fun allComments(tap:()->Unit):TopCommentCell {

        return this
    }
}