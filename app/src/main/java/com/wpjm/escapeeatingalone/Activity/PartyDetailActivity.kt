package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        // 채팅방 id
        var chatroomId = intent.getStringExtra("timeStamp").toString()
        
        // 메시지 제목
        var messageTitle = intent.getStringExtra("title").toString()

        binding.partyDetailActivityEdittextStoreName.setText(intent.getStringExtra("storeName").toString())
        binding.partyDetailActivityEdittextTitle.setText(intent.getStringExtra("title").toString())
        binding.partyDetailActivityEdittextEattingDate.setText(intent.getStringExtra("date").toString())
        binding.partyDetailActivityEdittextCount.setText(intent.getStringExtra("count").toString())
        binding.partyDetailActivityEdittextTimeStamp.setText(intent.getStringExtra("timeStamp").toString())
        binding.partyDetailActivityEdittextContents.setText(intent.getStringExtra("contents").toString())

        binding.partyDetailActivityButtonCheck.setOnClickListener(View.OnClickListener {
            // chatrooms의 users에 해당 id가 없다면
            // chatrooms의 users숫자가 count보다 작으면
            MakeUser(name, chatroomId)
            var intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("chatroomId", chatroomId)
            intent.putExtra("messageTitle", messageTitle)
            startActivity(intent)
        })
    }

    private fun MakeUser(name: String, chatroomId: String) {
        var personModel = PersonModel(name)
        db.collection("chatrooms").document(chatroomId)
                .collection("users").document(user!!.getUid())
                .set(personModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "채팅방 유저 생성 성공", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "채팅방 유저 생성 실패", Toast.LENGTH_SHORT).show()
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