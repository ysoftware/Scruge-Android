package com.scruge.scruge.view.cells

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.dependencies.view.setupForGridLayout
import com.scruge.scruge.model.entity.Member
import com.scruge.scruge.model.entity.Social
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import kotlinx.android.synthetic.main.cell_about.view.*
import kotlinx.android.synthetic.main.cell_member.view.*

class AboutCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var memberTap:((Member) -> Unit)
    private lateinit var members:List<Member>
    private lateinit var social:List<Social>

    fun setup(vm: CampaignVM):AboutCell {
        this.social = vm.social
        this.members = vm.team

        itemView.about_text.text = vm.about
        itemView.about_members.adapter = MembersAdapter(this)
        itemView.about_members.setupForGridLayout(4)
        itemView.about_social.adapter = SocialAdapter(social)
        itemView.about_social.setupForGridLayout(6)

        return this
    }

    fun memberTap(tap: (Member) -> Unit):AboutCell {
        memberTap = tap
        return this
    }

    fun socialTap(tap: (Social) -> Unit):AboutCell {
        (itemView.about_social.adapter as? SocialAdapter)?.tap = tap
        return this
    }

    class MembersAdapter(private val aboutCell: AboutCell):
            RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            return MemberViewHolder(LayoutInflater.from(parent.context)
                                            .inflate(R.layout.cell_member, parent, false))
        }

        override fun getItemCount(): Int {
            return aboutCell.members.size
        }

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            aboutCell.members.let {
                val item = it[position]
                holder.setup(item)
                holder.itemView.setOnClickListener { aboutCell.memberTap.invoke(item) }
            }
        }

        class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun setup(member:Member) {
                itemView.member_image_view.setImage(member.imageUrl)
            }
        }
    }
}