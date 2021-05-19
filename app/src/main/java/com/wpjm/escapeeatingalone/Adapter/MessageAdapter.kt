package com.wpjm.escapeeatingalone.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.MessageModel
import com.wpjm.escapeeatingalone.R
import java.util.ArrayList

class MessageAdapter(val MessageList: ArrayList<MessageModel>) : RecyclerView.Adapter<MessageAdapter.CustomViewHolder>(){
    private val user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private var userName=""

    init {
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    userName = result["name"] as String }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.CustomViewHolder {
        // item을 붙이기
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

        return CustomViewHolder(view)
    }


    override fun getItemCount(): Int {
        return MessageList.size
    }

    override fun onBindViewHolder(holder: MessageAdapter.CustomViewHolder, position: Int) {

        if(MessageList.get(position).profile == "") {
            holder.profile.setImageResource(R.drawable.android)
        }
        Glide.with(holder.itemView.context).load(MessageList.get(position).profile).into(holder.profile)

        holder.name.text = MessageList.get(position).name
        holder.contents.text = MessageList.get(position).contents
        holder.timeStamp.text = MessageList.get(position).timeStamp

        if(holder.name.text == userName){
            holder.messageLayout.setBackgroundResource(R.drawable.rightbubble)
        } else {
            holder.messageLayout.setBackgroundResource(R.drawable.leftbubble)
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageLayout = itemView.findViewById<ConstraintLayout>(R.id.messageItem_layout)
        var profile = itemView.findViewById<ImageView>(R.id.messageItem_imageView_profile)
        var name = itemView.findViewById<TextView>(R.id.messageItem_textview_name)
        var contents = itemView.findViewById<TextView>(R.id.messageItem_textview_contents)
        var timeStamp = itemView.findViewById<TextView>(R.id.messageItem_textView_timeStamp)
    }
}