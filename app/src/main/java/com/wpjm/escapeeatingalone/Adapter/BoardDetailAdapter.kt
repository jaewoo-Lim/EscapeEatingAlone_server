package com.wpjm.escapeeatingalone.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.CommentModel
import com.wpjm.escapeeatingalone.R


class BoardDetailAdapter(val BoardCommentList: ArrayList<CommentModel>) : RecyclerView.Adapter<BoardDetailAdapter.CustomViewHolder>() {
    private var db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var userName=""

    init {
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    userName = result["name"] as String }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardDetailAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_boardcomment, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  BoardCommentList.size
    }

    override fun onBindViewHolder(holder: BoardDetailAdapter.CustomViewHolder, position: Int) {
        holder.name.text = BoardCommentList.get(position).name
        holder.contents.text = BoardCommentList.get(position).contents
        holder.commentTimeStamp.text = BoardCommentList.get(position).timestamp

        // 자신의 댓글만 수정, 삭제 버튼 보이기
        if(holder.name.text == userName){
            holder.button_comment_delete.visibility = View.VISIBLE
        }
        
        holder.button_comment_delete.setOnClickListener {
            db.collection("comments").document("${holder.commentTimeStamp.text}")
                .delete()
                .addOnSuccessListener { Log.e("성공", "삭제") }
                .addOnFailureListener { e -> Log.e("실패", "Error deleting document", e) }
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.boardComment_textview_name) // 이름
        val contents = itemView.findViewById<TextView>(R.id.boardComment_textview_contents) // 내용
        val commentTimeStamp = itemView.findViewById<TextView>(R.id.boardComment_textview_timeStamp) // 날짜
        var button_comment_delete = itemView.findViewById<Button>(R.id.boardComment_button_delete)
    }


}