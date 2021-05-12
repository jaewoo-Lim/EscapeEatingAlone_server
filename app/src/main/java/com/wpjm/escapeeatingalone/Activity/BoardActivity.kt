package com.wpjm.escapeeatingalone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Adapter.BoardAdapter
import com.wpjm.escapeeatingalone.Model.BoardModel
import com.wpjm.escapeeatingalone.R
import com.wpjm.escapeeatingalone.databinding.ActivityBoardBinding


class BoardActivity : AppCompatActivity() {
    private var mBinding: ActivityBoardBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        mBinding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // fireStore에서 읽어오기
        var boardList = arrayListOf<BoardModel>()
        var adapter = BoardAdapter(boardList)

        db.collection("board")
                .get()
                .addOnSuccessListener { result ->
                    boardList.clear()
                    for (document in result) {
                        val item = BoardModel(document["profile"] as String, document["name"] as String, document["title"] as String, document["contents"] as String, document["date"] as String)
                        boardList.add(item)
                    }
                    adapter.notifyDataSetChanged() // 리사이클러뷰 갱신
                }
                .addOnFailureListener { exception ->
                    Log.e("no data", "$exception")
                }

        binding.boardActivityRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.boardActivityRecyclerView.setHasFixedSize(true)
        binding.boardActivityRecyclerView.adapter = adapter

        // 추가 버튼 눌렀을 때
        binding.boardActivityButtonAdd.setOnClickListener(View.OnClickListener {
            gotoActivity(BoardMake::class.java)
        })
    }

    // Intent function
    private fun gotoActivity(c: Class<*>) {
        var intent = Intent(this, c)
        startActivity(intent)
    }

}