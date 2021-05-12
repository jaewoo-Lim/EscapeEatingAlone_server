package com.wpjm.escapeeatingalone


import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.wpjm.escapeeatingalone.Adapter.FragmentPersonAdapter
import com.wpjm.escapeeatingalone.Model.FriendsList
import com.wpjm.escapeeatingalone.Model.PersonModel
import com.wpjm.escapeeatingalone.databinding.ActivityAddFriendBinding
import com.wpjm.escapeeatingalone.databinding.FragmentPersonBinding


class PersonFragment : Fragment() {
    private var mBinding: FragmentPersonBinding? = null
    private val binding get() = mBinding!!
    private var mBindings: ActivityAddFriendBinding? = null
    private val bindings get() = mBindings!!
    var firestore : FirebaseFirestore? = null
    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val mRootRef = FirebaseDatabase.getInstance().getReference()
    private val conditionRef = mRootRef.child("users")



    lateinit var reyclerView:RecyclerView

    private val list = ArrayList<PersonModel>()
    @RequiresApi(Build.VERSION_CODES.O)
    private val adapter:FragmentPersonAdapter = FragmentPersonAdapter(list)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentPersonBinding.inflate(inflater, container, false)
        var frgmentPersonView = inflater.inflate(R.layout.fragment_person, container, false)
        var fab = frgmentPersonView.findViewById<Button>(R.id.fab)
        var name=""

        db.collection("users").document(user!!.getUid()).get()
            .addOnSuccessListener { result ->
                name=result["name"] as String
                var imageUrl=result["profileImageUrl"] as String?
                //binding.fragmentpersonTextView23.setText(name)
                //시작
                if (imageUrl != ""){
                    Log.d("ProfileImageTest!!!!!","설정한 프로필 사진 있음")
                    Picasso.get().load(imageUrl).into(frgmentPersonView.findViewById<ImageView>(R.id.fragmentperson_image_profile))
                }
                else{
                    Log.d("ProfileImageTest!!!!!","설정한 프로필 사진 없음")
                }
                //끝
                frgmentPersonView.findViewById<TextView>(R.id.fragmentperson_textView23).setText(
                    name
                )
            }
        return frgmentPersonView

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reyclerView = view.findViewById(R.id.fragmentperson_recyclerView)
        val adapter = FragmentPersonAdapter(list)
        reyclerView.layoutManager = LinearLayoutManager(activity)
        reyclerView.adapter = adapter

        val nameRef = db.collection("users")
        var button = view.findViewById<Button>(R.id.fab)
        button.setOnClickListener {
            var dialogview = LayoutInflater.from(context).inflate(R.layout.activity_add_friend, null)
            var builder = AlertDialog.Builder(context).setView(dialogview)
                .setTitle("Add Contact")
            var alert = builder.show()
            dialogview.findViewById<Button>(R.id.btn_add).setOnClickListener {
                //var name=bindings.dialogNameId.getText().toString()
                var name= dialogview.findViewById<TextView>(R.id.dialog_name_id).text.toString()
                var nameQuery=nameRef.whereEqualTo("name", name).get().addOnSuccessListener { documents ->
                    for (document in documents){
                        Log.d("QueryTest", "${document.data}")
                        val docRef: DocumentReference = db.collection("friends").document(user!!.getUid())
                        docRef.get().addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                var friendNames: FriendsList
                                val document = task.result
                                if (document != null){
                                    if (document!!.exists()) {
                                        var fList =document["friendNames"] as MutableList<String>
                                        if (name in fList){
                                            Toast.makeText(context, "이미 추가된 친구 입니다.", Toast.LENGTH_SHORT).show()
                                            Log.d("TESTFDSFSD", "이미 등록된 친구임")
                                        }else{
                                            fList.add(name)
                                            friendNames=FriendsList(fList)
                                            docRef.set(friendNames)
                                            Log.d("TESTFDSFSD", "친구 등록 업뎃완료 ${fList}")
                                            Toast.makeText(context, "친구등록 성공!", Toast.LENGTH_SHORT).show()
                                            refreshFragment(this, parentFragmentManager)
                                            //refreshFragment(this,fragmentManager())

                                        }


                                    } else {
                                        friendNames=FriendsList(mutableListOf(name))
                                        docRef.set(friendNames)
                                        refreshFragment(this, parentFragmentManager)
                                        Toast.makeText(context, "친구 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                        Log.d("TESTFDSFSD", "친구 등록 성공!!!!")
                                    }
                                }
                            } else {
                                Log.e("msg", "에러")
                            }
                        }



                    }
                }
                Log.d("queryTest", "name: ${nameQuery}")
                alert.dismiss()
                //val name: String = bindings.dialogNameId.text.toString()
                //bindings.dialogNameId.getText().toString()

/*
                db.collection("users")
                        .whereEqualTo("name", name)
                        .get()
                        .addOnSuccessListener { querysnapshot ->
                            if (querysnapshot != null) {
                                for (dc in querysnapshot.documents) {
                                    val name = dc["name"].toString()

                                }
                            }
                        }*/
            }
        }

    }

    private fun refreshFragment(fragment: PersonFragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }



    /*private fun refreshFragment(fragment: PersonFragment, fragmentManager: FragmentManager?) {
         var ft = fragmentManager.beginTransaction()
         ft.detach(fragment).attach(fragment).commit()

     }*/






/*
    private fun addfriend() {

        var dialogview = LayoutInflater.from(context).inflate(
            R.layout.activity_add_friend,
            null
        )
        final String email = editFriendEmail.getText().toString();
        final String myEmail = PreferenceUtil.getString(this, Const.SP_EMAIL);

        // 현재 내 아이디일 경우
        if(email.equals(myEmail)){
            Toast.makeText(AddFriendActivity.this, "내 아이디입니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터베이스에서 전체 친구 목록 가져와서 입력한 아이디 존재하는지 검사
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                String friendEmail = StringUtil.recoverEmailComma(snapshot.getKey());
                // 존재할 경우 내 데이터베이스에 추가
                if(email.equals(friendEmail)){
                    userRef.child(StringUtil.replaceEmailComma(myEmail)).child("friend_list").child(snapshot.getKey()).setValue(snapshot.getValue());
                    Toast.makeText(AddFriendActivity.this, friendEmail+"님이 추가되었습니다", Toast.LENGTH_SHORT).show();
                    ifExists = true;
                }
            }
                // 친구가 추가되었으면 액티비티 종료
                if(ifExists){
                    finish();
                } else {
                    Toast.makeText(AddFriendActivity.this, "일치하는 아이디가 없습니다", Toast.LENGTH_SHORT).show();
                }
            }

    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}