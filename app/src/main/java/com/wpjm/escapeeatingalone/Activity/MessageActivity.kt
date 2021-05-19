package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

        // 이름
        var name = ""
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    name = result["name"] as String
                }

        // 제목
        var messageTitle = intent.getStringExtra("messageTitle")
        binding.messageActivityEdittextTitle.setText(messageTitle)


        // 채팅룸 id
        var chatrommId = intent.getStringExtra("chatroomId").toString()

        // 현재시간
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 SSS")
        val timeStamp = current.format(formatter)

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
                        val item = MessageModel(document["name"] as String, document["contents"] as String, document["timeStamp"] as String)
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
                        Log.e("chatroomCount", uList.count().toString())
                    } else {
                        Log.d("MessageActivity", "참가자없음")
                    }
                    padaper.notifyDataSetChanged()
                }

        binding.chatPesonList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.chatPesonList.adapter = padaper
        binding.messageActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageActivityRecyclerView.setHasFixedSize(true)
        binding.messageActivityRecyclerView.adapter = adapter

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
                            chatRef.update("users", uList)
                        } else {
                            Log.d("MessageActivity", "참가자없음")
                        }
                        finish()
                        gotoActivity(ChatActivity::class.java)
                    }
        }

        // 전송 버튼을 눌렀을 때
        binding.messageActivityButtonSend.setOnClickListener {
            var message = binding.messageActivityEdittextMessage.getText().toString()
            var messageModel = MessageModel(name, message, timeStamp)

            db.collection("chatrooms").document(chatrommId)
                    .collection("message").document(timeStamp)
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