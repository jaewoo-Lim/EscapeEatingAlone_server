package com.wpjm.escapeeatingalone.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.wpjm.escapeeatingalone.Activity.BoardDetailActivity
import com.wpjm.escapeeatingalone.Model.BoardModel
import com.wpjm.escapeeatingalone.R


class BoardAdapter(val BoardList: ArrayList<BoardModel>) : RecyclerView.Adapter<BoardAdapter.CustomViewHolder>() {
    private var db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var userName=""

    init {
        // users의 name, imageUrl
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    userName = result["name"] as String
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardAdapter.CustomViewHolder {
        // item을 붙이기
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return BoardList.size
    }

    override fun onBindViewHolder(holder: BoardAdapter.CustomViewHolder, position: Int) {
        if(BoardList.get(position).profile == "") {
            holder.profile.setImageResource(R.drawable.android)
        }
        Glide.with(holder.itemView.context).load(BoardList.get(position).profile).into(holder.profile)
        var writerName = BoardList.get(position).name
        holder.title.text = BoardList.get(position).title
        holder.contents.text = BoardList.get(position).contents
        holder.date.text = BoardList.get(position).date


        // 리스트 눌렀을 때
        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView?.context, BoardDetailActivity::class.java)
            intent.putExtra("profile", "${BoardList.get(position).profile.toString()}")
            intent.putExtra("writerName", "${writerName}")
            intent.putExtra("title", "${holder.title.text}")
            intent.putExtra("contents", "${holder.contents.text}")
            intent.putExtra("date", "${holder.date.text}")
            intent.putExtra("userName", userName)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.boardActivity_image_profile) // 이미지
        val title = itemView.findViewById<TextView>(R.id.boardActivity_textview_title) // 제목
        val contents = itemView.findViewById<TextView>(R.id.boardActivity_textview_contents) // 내용
        val date = itemView.findViewById<TextView>(R.id.boardActivity_textview_date) // 날짜

    }
}