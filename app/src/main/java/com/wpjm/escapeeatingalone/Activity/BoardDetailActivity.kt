package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Adapter.BoardDetailAdapter
import com.wpjm.escapeeatingalone.Model.BoardDetailModel
import com.wpjm.escapeeatingalone.Model.CommentModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityBoardDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardDetailActivity : AppCompatActivity() {
    private var mBinding: ActivityBoardDetailBinding? = null
    private val binding get() = mBinding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private var name=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)
        mBinding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이름
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    name = result["name"] as String
                }

        // 게시글 타임스탬프
        var boardTimeStamp = intent.getStringExtra("date")

        // 상단 board 정보
        Glide.with(binding.boardDetailImageViewProfile).load(intent.getStringExtra("profile").toString()).into(binding.boardDetailImageViewProfile)
        binding.boardDetailActivityTextviewWritername.text = intent.getStringExtra("writerName")
        binding.boardDetailTextViewTitle.text = intent.getStringExtra("title")
        binding.boardDetailTextViewContents.text = intent.getStringExtra("contents")
        binding.boardDetailTextViewDate.text = intent.getStringExtra("date")

        // 수정 삭제 보기
        var userName = intent.getStringExtra("userName")
        if(intent.getStringExtra("writerName") == userName){
            binding.boardDetailButtonModify.visibility = View.VISIBLE
            binding.boardDetailButtonDelete.visibility = View.VISIBLE
        }

        // 상단 board 수정 및 삭제
        binding.boardDetailButtonModify.setOnClickListener {
            var intent_modify = Intent(this, BoardModify::class.java)
            intent_modify.putExtra("title", intent.getStringExtra("title"))
            intent_modify.putExtra("contents", intent.getStringExtra("contents"))
            intent_modify.putExtra("boardTimeStamp", intent.getStringExtra("date"))
            startActivity(intent_modify)
        }

        binding.boardDetailButtonDelete.setOnClickListener {
            db.collection("board").document("${binding.boardDetailTextViewDate.text}")
                    .delete()
                    .addOnSuccessListener { Log.e("성공", "삭제") }
                    .addOnFailureListener { e -> Log.e("실패", "Error deleting document", e) }
            gotoActivity(BoardActivity::class.java)
        }

        // fireStore에서 comments를 recycleriew로 읽어오기
        var commentList = arrayListOf<CommentModel>()
        var adapter = BoardDetailAdapter(commentList)

        db.collection("comments")
                .whereEqualTo("boardTimeStamp", "${boardTimeStamp}")
                .addSnapshotListener{ result, e ->
                    if (e != null) {
                        Log.e("error", e.toString())
                        return@addSnapshotListener
                    }
                    commentList.clear()

                    for (doc in result!!.documentChanges) {
                            for (document in result) {
                                val item = CommentModel(
                                                document["name"] as String,
                                                document["contents"] as String,
                                                document["timestamp"] as String,
                                                document["boardTimeStamp"] as String
                                )
                                commentList.add(item)
                            }
                        adapter.notifyDataSetChanged() // 리사이클러뷰 갱신
                    }
                }

        // 전송 버튼 눌렀을 때
        binding.boardDetailActivityButtonSend.setOnClickListener ( View.OnClickListener {

            // 현재시간
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val timeStamp = current.format(formatter)

            // 댓글 내용, 게시글 타임스탬프
            var comment = binding.boardDetailActivityEdittextComment.getText().toString()
            var commentModel = CommentModel(name, comment, timeStamp, boardTimeStamp!!)

            db.collection("comments").document(timeStamp.toString()).set(commentModel)
                    .addOnSuccessListener { // 성공할 때
                        binding.boardDetailActivityEdittextComment.setText(null)
                        Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { // 실패할 때
                        Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
        })

        binding.boardDetailActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.boardDetailActivityRecyclerView.setHasFixedSize(true)

        binding.boardDetailActivityRecyclerView.adapter = adapter
    }
    

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }

}