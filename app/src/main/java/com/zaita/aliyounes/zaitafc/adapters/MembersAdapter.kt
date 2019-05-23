package com.zaita.aliyounes.zaitafc.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.pojos.Member
import com.zaita.aliyounes.zaitafc.pojos.Player

@Suppress("MemberVisibilityCanBePrivate")
/**
 * Created by Ali Younes on 8/11/2017.
 */

class MembersAdapter(//List of members to show
        private val members: List<Member>) : RecyclerView.Adapter<MembersAdapter.MemberVieHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MemberVieHolder {
        val view: View
        return when (i) {
            1 -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_team_player_layout, viewGroup, false)
                PlayerVieHolder(view)
            }
            else -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_member_layout, viewGroup, false)
                MemberVieHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val member = members[position]
        return if (member is Player) {
            1
        } else 2
    }

    override fun onBindViewHolder(memberViewHolder: MemberVieHolder, position: Int) {
        val member = members[position]
        memberViewHolder.bind(member)
    }

    override fun getItemCount(): Int {
        return members.size
    }

    open inner class MemberVieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewMemberName: TextView = itemView.findViewById<View>(R.id.textView_memberName) as TextView
        var textViewMemberRole: TextView = itemView.findViewById<View>(R.id.textView_memberPosition) as TextView
        var textViewMemberAge: TextView = itemView.findViewById<View>(R.id.textView_memberAge) as TextView
        var textViewMemberJoinDate: TextView = itemView.findViewById<View>(R.id.textView_memberJoinDate) as TextView
        var cardViewMember: CardView = itemView.findViewById<View>(R.id.cardView_member) as CardView

        //Bind data to List item
        internal open fun bind(member: Member) {
            textViewMemberName.text = member.name
            textViewMemberRole.text = member.role
            textViewMemberAge.text = member.age.toString()
            textViewMemberJoinDate.text = member.joinDate
            cardViewMember.setOnClickListener {
                //Add action for any member item click
            }
        }
    }

    private inner class PlayerVieHolder internal constructor(itemView: View) : MemberVieHolder(itemView) {

        private val getTextViewPlayerNumber: TextView = itemView.findViewById<View>(R.id.textView_playerNumber) as TextView

        //Bind data to List item
        override fun bind(member: Member) {
            super.bind(member)
            getTextViewPlayerNumber.text = (member as Player).number.toString()
        }
    }
}
