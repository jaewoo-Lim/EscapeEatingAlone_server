package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityBoardModifyBinding

class BoardModify : AppCompatActivity() {
    private var mBinding: ActivityBoardModifyBinding? = null
    private val binding get() = mBinding!!
    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_modify)
        mBinding = ActivityBoardModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var title: String? = intent.getStringExtra("title")
        var contents: String? = intent.getStringExtra("contents")
        var boardTimeStamp: String? = intent.getStringExtra("boardTimeStamp")
        binding.boardModifyEdittextTitle.setText(title)
        binding.boardModifyEdittextContents.setText(contents)

        binding.boardModifyButtonOk.setOnClickListener ( View.OnClickListener {
            if (boardTimeStamp != null) {
                db.collection("board").document("${boardTimeStamp}")
                          .update(mapOf(
                                  "title" to "${binding.boardModifyEdittextTitle.getText().toString()}",
                                  "contents" to "${binding.boardModifyEdittextContents.getText().toString()}"

                          ))
                        .addOnCompleteListener { Log.e("success", "success") }
                        .addOnFailureListener { Log.e("fail", "fail") }
            }
            gotoActivity(BoardActivity::class.java)
        })

    }

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }
}