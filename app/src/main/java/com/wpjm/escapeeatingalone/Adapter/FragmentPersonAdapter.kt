package com.wpjm.escapeeatingalone.Adapter

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Activity.MemberInitActivity
import com.wpjm.escapeeatingalone.Activity.MessageActivity
import com.wpjm.escapeeatingalone.Activity.SignupActivity
import com.wpjm.escapeeatingalone.Model.ChatroomModel
import com.wpjm.escapeeatingalone.Model.PersonModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.FragmentPersonBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class FragmentPersonAdapter(val personList:ArrayList<PersonModel>) : RecyclerView.Adapter<FragmentPersonAdapter.CustomViewHolder>() {
    public var firestore: FirebaseFirestore? = null
    private var db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var timeStamp: String?
    private var userName = ""

    init {
        // 친구 목록 읽어오기
        db.collection("friends").document(user!!.getUid())
                .get()
                .addOnSuccessListener { result ->
                    Log.d("nullTest!!!!!!!!!!!", "일단 success")
                    personList.clear()
                    var fList = result["friendNames"] as MutableList<String>?
                    if (fList != null) {
                        Log.d("nullTest!!!!!!!!!!!", "리스트에 친구 있음")
                        for (name in fList) {
                            val item = PersonModel(name)
                            personList.add(item)
                        }
                        // 리사이클러뷰 갱신

                    } else {
                        Log.d("nullTest!!!!!!!!!!!", "아직 친구없음")

                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e("no data", "$exception")
                }

        // 현재시간
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 SSS")
        timeStamp = current.format(formatter)

        // 유저 이름
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    userName = result["name"] as String
                }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentPersonAdapter.CustomViewHolder {
        // item을 붙이기
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var friendName = personList.get(position).name

        /*holder.profile.setImageResource(personList.get(position).profile)*/
        holder.friendName.text = friendName
        /*holder.spoonScore.text = personList.get(position).spoonScore*/

        // 리스트 눌렀을 때
        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView?.context, MessageActivity::class.java)
            MakeUser(userName!!, friendName!!, timeStamp!!)
            intent.putExtra("messageTitle", friendName)
            intent.putExtra("chatroomId", timeStamp)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*val profile =  itemView.findViewById<ImageView>(R.id.fragmentperson_image_profile) // 이미지*/
        val friendName = itemView.findViewById<TextView>(R.id.fragmentperson_textview_name) // 이름
        /*val spoonScore = itemView.findViewById<TextView>(R.id.fragmentperson_textview_spoonscore) // 숫가락 점수*/
    }


    private fun MakeUser(userName: String, friendName: String, timeStamp: String) {
        var chatroomModel = ChatroomModel(mutableListOf(userName, friendName), "${userName}, ${friendName}", "친구", "", timeStamp)

        db.collection("chatrooms").document(timeStamp)
                .set(chatroomModel)
    }
}
