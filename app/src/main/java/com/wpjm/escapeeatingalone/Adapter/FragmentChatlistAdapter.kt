package com.wpjm.escapeeatingalone.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Activity.MessageActivity
import com.wpjm.escapeeatingalone.Model.ChatlistModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.FragmentPersonBinding

class FragmentChatlistAdapter(val ChatList:ArrayList<ChatlistModel>) : RecyclerView.Adapter<FragmentChatlistAdapter.CustomViewHolder>() {
    var firestore : FirebaseFirestore? = null
    private var mBinding: FragmentPersonBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentChatlistAdapter.CustomViewHolder {
        // item을 붙이기
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatlist, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ChatList.size
    }
    override fun onBindViewHolder(holder: FragmentChatlistAdapter.CustomViewHolder, position: Int) {
        holder.profile.setImageResource(ChatList.get(position).profile)
        holder.title.text = ChatList.get(position).title
        holder.storeName.text = ChatList.get(position).storeName
        holder.date.text = ChatList.get(position).date
        var chatroomId = ChatList.get(position).chatroomId

        // 리스트 눌렀을 때
        holder.itemView.setOnClickListener{
            var intent = Intent(holder.itemView?.context, MessageActivity::class.java)
            intent.putExtra("chatroomId", chatroomId)
            intent.putExtra("messageTitle", ChatList.get(position).title)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.fragmentchatlist_image_profile) // 이미지
        val title = itemView.findViewById<TextView>(R.id.fragmentchatlist_textview_title) // 제목
        val storeName = itemView.findViewById<TextView>(R.id.fragmentchatlist_textview_storeName) // 가게이름
        val date = itemView.findViewById<TextView>(R.id.fragmentchatlist_textview_date) // 날짜
    }
}