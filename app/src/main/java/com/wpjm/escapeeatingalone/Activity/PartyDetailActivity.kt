package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.ChatroomModel
import com.wpjm.escapeeatingalone.Model.PersonModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityPartyDetailBinding

class PartyDetailActivity : AppCompatActivity() {
    private var mBinding: ActivityPartyDetailBinding? = null
    private val binding get() = mBinding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_detail)
        mBinding = ActivityPartyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이름
        var name = ""
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    name = result["name"] as String
                }

        // 메시지 제목
        var messageTitle = intent.getStringExtra("title").toString()
        // 가게 이름
        var storeName = intent.getStringExtra("storeName").toString()
        // 채팅방 id
        var chatroomId = intent.getStringExtra("timeStamp").toString()



        binding.partyDetailActivityEdittextStoreName.setText(intent.getStringExtra("storeName").toString())
        binding.partyDetailActivityEdittextTitle.setText(intent.getStringExtra("title").toString())
        binding.partyDetailActivityEdittextEattingDate.setText(intent.getStringExtra("date").toString())
        binding.partyDetailActivityEdittextCount.setText(intent.getStringExtra("count").toString())
        binding.partyDetailActivityEdittextTimeStamp.setText(intent.getStringExtra("timeStamp").toString())
        binding.partyDetailActivityEdittextContents.setText(intent.getStringExtra("contents").toString())

        binding.partyDetailActivityButtonCheck.setOnClickListener(View.OnClickListener {
            // chatrooms의 users에 해당 id가 없다면
            // chatrooms의 users숫자가 count보다 작으면
            MakeUser(name, messageTitle, storeName, chatroomId)
            var intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("chatroomId", chatroomId)
            intent.putExtra("messageTitle", messageTitle)
            startActivity(intent)
        })
    }

    private fun MakeUser(name: String, title: String, storeName: String, chatroomId: String) {
        var chatrooms = db.collection("chatrooms")
        chatrooms.get().addOnSuccessListener { documents ->
            for (document in documents) { 
                var uList = document["users"] as MutableList<String>
                
                if(user!!.getUid() in uList){ // uList안에 user의 uid가 존재하면
                    Toast.makeText(this, "이미 존재하는 회원입니다", Toast.LENGTH_SHORT).show()
                    Log.d("partyDetail", "이미 존재하는 회원입니다")
                }
                else{ // uList안에 user의 uid가 존재하지않으면
                    uList.add(name)
                    chatrooms.document(chatroomId).update(mapOf("users" to uList))
                }
            }
        }
    }

    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }

//    private fun CountUser() {
//        var countNumber = binding.partyDetailActivityEdittextCount.setText(intent.getStringExtra("count").toString())
//        if(countNumber > 0 &&
//    }
}