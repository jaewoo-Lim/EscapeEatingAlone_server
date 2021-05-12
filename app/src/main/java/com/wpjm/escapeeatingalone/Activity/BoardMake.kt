package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Model.BoardModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityBoardMakeBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardMake : AppCompatActivity() {
    private var mBinding: ActivityBoardMakeBinding? = null
    private val binding get() = mBinding!!
    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_make)
        mBinding = ActivityBoardMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var name = ""
        var imageUrl = ""
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    name=result["name"] as String
                    imageUrl=result["profileImageUrl"] as String
                }

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 SSS")
        val timeStamp = current.format(formatter)

        // 확인버튼을 눌렀을 때
        binding.boardMakeButtonOk.setOnClickListener(View.OnClickListener {
            if (binding.boardMakeEdittextTitle.getText().toString().length > 0 &&
                    binding.boardMakeEdittextContents.getText().toString().length > 0
            ) {
                var boardModel = BoardModel(
                        imageUrl!!,
                        name!!,
                        binding.boardMakeEdittextTitle.getText().toString(),
                        binding.boardMakeEdittextContents.getText().toString(),
                        timeStamp.toString()
                )

                db.collection("board").document(timeStamp.toString()).set(boardModel)
                        .addOnSuccessListener { // 성공할 때
                            Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
                            gotoActivity(BoardActivity::class.java)
                        }
                        .addOnFailureListener { // 실패할 때
                            Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                        }
            } else {
                Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }

        })
    }

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }
}