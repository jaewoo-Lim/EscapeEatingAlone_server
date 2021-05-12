package com.wpjm.escapeeatingalone.Adapter

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.MessageModel
import com.wpjm.escapeeatingalone.R
import java.util.ArrayList

class MsgPersonAdaper(val personList: MutableList<String>) : RecyclerView.Adapter<MsgPersonAdaper.CustomViewHolder>(){
    private val user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private var userName=""

    init {
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    userName = result["name"] as String }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgPersonAdaper.CustomViewHolder {
        // item을 붙이기
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)

        return CustomViewHolder(view)
    }


    override fun getItemCount(): Int {
        return personList.size
    }



    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById<TextView>(R.id.fragmentperson_textview_name) // 이름

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.name.text = personList.get(position)
    }
}