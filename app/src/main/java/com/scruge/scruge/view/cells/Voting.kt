package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.view.views.ButtonView
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import kotlinx.android.synthetic.main.cell_controls.view.*
import kotlinx.android.synthetic.main.cell_vote_info.view.*

class VoteInfoCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: CampaignVM, kind: VoteKind) {
        itemView.vote_info_title.text = vm.title
        itemView.vote_info_date.text = vm.daysLeft

        if (kind == VoteKind.extend) {
            itemView.vote_info_description.text = "Voting whether to extend deadlines of the milestone."
        }
        else {
            itemView.vote_info_description.text = "Voting to accept milestone results and release next portions of funds to the founders of the campaign or to return remaining funds to backers and close the campaign."
        }
    }
}

class VoteControlsCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(block:(String, Boolean)->Unit): VoteControlsCell {
        itemView.controls_yes.color = ButtonView.Color.green
        itemView.controls_yes.title = "VOTE FOR"
        itemView.controls_no.title = "VOTE AGAINST"

        itemView.controls_no.click { block(itemView.controls_password.text.toString(), false) }
        itemView.controls_yes.click { block(itemView.controls_password.text.toString(), true) }

        return this
    }
}

class CountdownCell(itemView: View) : RecyclerView.ViewHolder(itemView) {


}