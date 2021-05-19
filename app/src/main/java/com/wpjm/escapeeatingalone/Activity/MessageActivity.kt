package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Adapter.MessageAdapter
import com.wpjm.escapeeatingalone.Adapter.MsgPersonAdaper
import com.wpjm.escapeeatingalone.Model.MessageModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityMessageBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMessageBinding? = null
    private val binding get() = mBinding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        mBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이름, 프로필 이미지
        var name = ""
        var imageUrl = ""
        db.collection("users").document(user!!.getUid()).get()
            .addOnSuccessListener { result ->
                name=result["name"] as String
                imageUrl=result["profileImageUrl"] as String
            }

        // 채팅룸 id
        var chatrommId = intent.getStringExtra("chatroomId").toString()

        // 인원수
        db.collection("chatrooms").document(chatrommId)
            .addSnapshotListener { result, e ->
                if (e != null) {
                    Log.e("error", e.toString())
                    return@addSnapshotListener
                }
                var chatroomCount = result!!["count"] as Number

                // 제목
                var messageTitle = intent.getStringExtra("messageTitle")
                binding.messageActivityEdittextTitle.setText("${messageTitle}(${chatroomCount})")
            }


        // fireStore chatrooms 에서 읽어오기
        val chatRef = db.collection("chatrooms").document(chatrommId)
        var messageList = arrayListOf<MessageModel>()
        var personList = mutableListOf<String>()
        var adapter = MessageAdapter(messageList)
        var padaper = MsgPersonAdaper(personList)

        chatRef
                .collection("message")
                .orderBy("timeStamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener { result, e ->
                    if (e != null) {
                        Log.e("error", e.toString())
                        return@addSnapshotListener
                    }
                    messageList.clear()
                    for (document in result!!) {
                        val item = MessageModel(document["profile"] as String, document["name"] as String, document["contents"] as String, document["timeStamp"] as String)
                        messageList.add(item)
                    }
                    adapter.notifyDataSetChanged() // 리사이클러뷰 갱신
                }

        chatRef
                .addSnapshotListener { result, e ->
                    if (e != null) {
                        Log.e("error", e.toString())
                        return@addSnapshotListener
                    }

                    personList.clear()
                    var uList = result!!["users"] as MutableList<String>?

                    if (uList != null) {
                        for (name in uList) {
                            personList.add(name)
                        }
                        chatRef.update("count", uList.count())
                    } else {
                        Log.e("MessageActivity", "참가자없음")
                    }
                    padaper.notifyDataSetChanged()
                }

        binding.chatPesonList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.chatPesonList.adapter = padaper
        binding.messageActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageActivityRecyclerView.setHasFixedSize(true)
        binding.messageActivityRecyclerView.adapter = adapter

        // messageActivity_button_gotoMessageList 눌렀을 때
        binding.messageActivityButtonGotoMessageList.setOnClickListener {
            gotoActivity(ChatActivity::class.java)
        }

        // 네비게이션 버튼 눌렀을 때
        binding.messageActivityButtonUserNavi.setOnClickListener {
            binding.layoutDrawer2.openDrawer(GravityCompat.START)
        }

        binding.userView.setNavigationItemSelectedListener(this)
        binding.exitChat.setOnClickListener {
            chatRef.get()
                    .addOnSuccessListener { result ->
                        personList.clear()
                        var uList = result["users"] as MutableList<String>?

                        if (uList != null) {
                            uList.remove(name)
                            if (uList.isEmpty()) {
                                chatRef.delete()
                            }
                            chatRef.update(mapOf("count" to uList.count()-1, "users" to uList)
                            )
                        } else {
                            // chatrooms 삭제하기
                            chatRef.delete()
                            Log.e("MessageActivity", "참가자없음")
                        }
                        finish()
                        gotoActivity(ChatActivity::class.java)
                    }
        }

        // 전송 버튼을 눌렀을 때
        binding.messageActivityButtonSend.setOnClickListener {
            // 버튼 눌렀을 때 현재시간
            val btn_current = LocalDateTime.now()
            val btn_formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 SSS")
            val btn_timeStamp = btn_current.format(btn_formatter)

            var message = binding.messageActivityEdittextMessage.getText().toString()
            var messageModel = MessageModel(imageUrl, name, message, btn_timeStamp)

            db.collection("chatrooms").document(chatrommId)
                    .collection("message").document(btn_timeStamp)
                    .set(messageModel)
                    .addOnSuccessListener {
                        binding.messageActivityEdittextMessage.setText(null)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //R.id.exit_chat -> Toast.makeText(this, "방 나가기!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)

    }
}