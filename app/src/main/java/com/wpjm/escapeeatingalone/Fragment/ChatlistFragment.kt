package com.wpjm.escapeeatingalone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wpjm.escapeeatingalone.Adapter.FragmentChatlistAdapter
import com.wpjm.escapeeatingalone.Model.ChatlistModel
import com.wpjm.escapeeatingalone.databinding.FragmentChatBinding

class ChatlistFragment : Fragment() {
    private var mBinding: FragmentChatBinding? = null
    private val binding get() = mBinding!!
    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()

    lateinit var reyclerView: RecyclerView
    private val list = ArrayList<ChatlistModel>()
    private val adapter: FragmentChatlistAdapter = FragmentChatlistAdapter(list)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChatBinding.inflate(inflater, container, false)
        var fragmentChatlistView = inflater.inflate(R.layout.fragment_chat, container, false)



        return fragmentChatlistView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FragmentChatlistAdapter(list)

        reyclerView = view.findViewById(R.id.fragmentchat_recyclerView)
        reyclerView.layoutManager = LinearLayoutManager(activity)
        reyclerView.adapter = adapter


        // fireStore에서 해당 user의 이름을 불러온 뒤 chatrooms를 recycleriew로 읽어오기
        db.collection("users").document(user!!.getUid()).get()
                .addOnSuccessListener { result ->
                    var name = result["name"] as String

                    db.collection("chatrooms")
                            .whereArrayContains("users", name)
                            .addSnapshotListener { documents, e ->
                                if (e != null) {
                                    Log.e("error", e.toString())
                                    return@addSnapshotListener
                                }

                                list.clear()
                                for (document in documents!!){
                                    val item = ChatlistModel(
                                            R.drawable.user,
                                            document["title"] as String,
                                            document["storeName"] as String,
                                            document["date"] as String,
                                            document["chatroomId"] as String
                                    )
                                    list.add(item)
                                }
                                adapter.notifyDataSetChanged()
                            }
                }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}