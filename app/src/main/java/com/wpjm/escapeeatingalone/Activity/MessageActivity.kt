package com.wpjm.escapeeatingalone.Activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.Query
import com.wpjm.escapeeatingalone.Adapter.MessageAdapter
import com.wpjm.escapeeatingalone.Model.MessageModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityMessageBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageActivity : AppCompatActivity() {
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
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
        val timeStamp = current.format(formatter)
//        val messageCurrent = LocalDateTime.now()
//        val mcTimeStamp = messageCurrent.format(formatter)

        // fireStore chatrooms 에서 읽어오기
        var messageList = arrayListOf<MessageModel>()
        var adapter = MessageAdapter(messageList)
        db.collection("chatrooms").document(chatrommId)
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
        
        binding.messageActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageActivityRecyclerView.setHasFixedSize(true)
        binding.messageActivityRecyclerView.adapter = adapter

        // 네비게이션 버튼 눌렀을 때
        binding.messageActivityButtonUserNavi.setOnClickListener{
            binding.layoutDrawer2.openDrawer(GravityCompat.START)
        }
        
        // 전송 버튼을 눌렀을 때
        binding.messageActivityButtonSend.setOnClickListener{
            var message = binding.messageActivityEdittextMessage.getText().toString()
            var messageModel = MessageModel(name, message, timeStamp)

            db.collection("chatrooms").document(chatrommId)
                .collection("message").document(timeStamp)
                .set(messageModel)
                    .addOnSuccessListener {
                        binding.messageActivityEdittextMessage.setText(null)
                        Log.e("채팅 올리기 성공", "채팅 올리기 성공")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
        }


    }
}

