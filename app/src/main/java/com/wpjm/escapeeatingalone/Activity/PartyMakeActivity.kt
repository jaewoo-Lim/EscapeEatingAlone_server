package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.MessageModel
import com.wpjm.escapeeatingalone.Model.PartyModel
import com.wpjm.escapeeatingalone.Model.PersonModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityPartyMakeBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PartyMakeActivity : AppCompatActivity() {
    private var mBinding : ActivityPartyMakeBinding ?= null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    // 현재 타임스탬프
    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 SSS")
    @RequiresApi(Build.VERSION_CODES.O)
    private val timeStamp = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_make)
        mBinding = ActivityPartyMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 유저 이름
        var name=""
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    name = result["name"] as String
                }

        // 가게 이름
        var storeName = intent.getStringExtra("storeName")

       val countList = arrayOf("2","3","4")
        val countAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countList)
        //spinner.adapter = countAdapter

        // 생성하기 버튼 누르면
        binding.partyMakeButtonComplete.setOnClickListener(View.OnClickListener {
           if (binding.partyMakeEdittextTitle.getText().toString().length > 0 &&
               binding.partyMakeEdittextDate.getText().toString().length > 0 &&
               binding.partyMakeEdittextCount.getText().toString().length > 0 &&
               binding.partyMakeEdittextContents.getText().toString().length > 0) {

               var partyModel = PartyModel(binding.partyMakeEdittextTitle.getText().toString(),
                                           binding.partyMakeEdittextDate.getText().toString(), 
                                           binding.partyMakeEdittextCount.getText().toString(),
                                           binding.partyMakeEdittextContents.getText().toString(),
                                           storeName!!,
                                           timeStamp)

               db.collection("party").document(timeStamp).set(partyModel)
                       .addOnSuccessListener {
                            Toast.makeText(this, "등록 성공", Toast.LENGTH_SHORT).show()
                            MakeMessage(name)
                       }
                       .addOnFailureListener {
                           Toast.makeText(this, "등록 실패", Toast.LENGTH_SHORT).show()
                       }
           }
           else {
                Toast.makeText(this, "모집글을 써주세요.", Toast.LENGTH_SHORT).show()
           }
         })


    }

    // chatromms의 message에 생성하기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun MakeMessage(name: String) {
        var messageModel = MessageModel(name, "${name}님 입장하였습니다", timeStamp)

        db.collection("chatrooms").document(timeStamp)
                .collection("message").document(timeStamp)
                .set(messageModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "채팅방 생성 성공", Toast.LENGTH_SHORT).show()
                    MakeUser(name, timeStamp)
                    var intent = Intent(this, MessageActivity::class.java)
                    intent.putExtra("chatroomId", timeStamp)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "채팅방 생성 실패", Toast.LENGTH_SHORT).show()
                }
    }

    // chatromms의 users에 생성하기
    private fun MakeUser(name: String, timeStamp: String) {
        var personModel = PersonModel(name)
        db.collection("chatrooms").document(timeStamp)
                .collection("users").document(user!!.getUid())
                .set(personModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "채팅방 유저 생성 성공", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "채팅방 유저 생성 실패", Toast.LENGTH_SHORT).show()
                }
    }

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }
}



