package com.taskease.yksfoundation.Activities.Chat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taskease.yksfoundation.Activities.CreatePostActivity
import com.taskease.yksfoundation.Adapter.ChatAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ChatMessage
import com.taskease.yksfoundation.Model.RequestModel.CreatePostRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.CreatePostResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityChattingBinding
import retrofit2.Callback
import retrofit2.Response

class ChattingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChattingBinding
    private val chatMessages = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Community"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this@ChattingActivity,chatMessages, userId.toString())
        binding.chatRecyclerView.adapter = chatAdapter

        val role = SharedPreferenceManager.getString(SharedPreferenceManager.ROLE)

        if (role.equals("ROLE_USER", ignoreCase = true) || role.equals(
                "ROLE_ADMIN",
                ignoreCase = true
            )
        ) {
            val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)
            fetchChatMessages(societyId)
            binding.send.setOnClickListener {
                val message = binding.message.text.toString()
                if (message.isNotEmpty()) {
                    callSendMessageApi(message, userId, societyId)
                }
            }
        } else if (role.equals("ROLE_SUPER_ADMIN")) {
            val societyId = intent.getIntExtra("societyId", 0)
            fetchChatMessages(societyId)
            binding.send.setOnClickListener {
                val message = binding.message.text.toString()
                if (message.isNotEmpty()) {
                    callSendMessageApi(message, userId, societyId)
                }
            }
        }
    }

    private fun callSendMessageApi(message: String, userId: Int, societyId: Int) {
        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().sendMessage(userId, societyId, message)
                .enqueue(object :
                    Callback<UniversalModel> {
                    override fun onResponse(
                        call: retrofit2.Call<UniversalModel>,
                        response: Response<UniversalModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    Constant.success(this@ChattingActivity, data.MSG)
                                    binding.message.setText("")
                                } else {
                                    Constant.error(this@ChattingActivity, data.MSG)
                                }
                            } else {
                                Constant.error(this@ChattingActivity, "No data received")
                            }
                        } else {
                            Constant.error(this@ChattingActivity, "Response unsuccessful")
                            Log.e(
                                "SelectSocietyFragment",
                                "Error response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<UniversalModel>, t: Throwable) {
                        progress.dismiss()
                        Constant.error(this@ChattingActivity, "Something went wrong: ${t.message}")
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@ChattingActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun fetchChatMessages(societyId: Int) {

        val databaseRef = FirebaseDatabase.getInstance().getReference("chats/$societyId/messages")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatMessages.clear()
                for (messageSnapshot in snapshot.children) {
                    val chat = messageSnapshot.getValue(ChatMessage::class.java)
                    chat?.let { chatMessages.add(it) }
                }
                chatAdapter.notifyDataSetChanged()

                binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChattingActivity, "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}