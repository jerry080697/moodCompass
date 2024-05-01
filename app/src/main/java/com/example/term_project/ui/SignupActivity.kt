package com.example.term_project.ui

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.term_project.data.entity.SignupInfo
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.signupBackBtn.setOnClickListener {
            finish()
        }
        binding.loginSignupBtn.setOnClickListener {
            val email = binding.signupIdEt.text.toString()
            val pwd = binding.signupPwdEt.text.toString()
            val pwdcon = binding.signupConfirmPwdEt.text.toString()
            val name = binding.signupNameEt.text.toString()
            val info = binding.signupInfoEt.text.toString()
            val pattern: Pattern = Patterns.EMAIL_ADDRESS

            if (email == "" || pwd == "" || pwdcon == "" ||name == "" || info == "") {
                binding.signupErrorTv.visibility = View.VISIBLE
                binding.signupErrorTv.text = "정보를 모두 입력해주세요"
            } else {       //모두 채워짐
                if (pattern.matcher(email).matches()) {
                    if (pwd.length < 8) {
                        binding.signupErrorTv.visibility = View.VISIBLE
                        binding.signupErrorTv.text = "비밀번호 8자리 이상을 입력해주세요"
                    }else {     //비밀번호 8자리 이상
                        if (pwd == pwdcon) {    //비밀번호 확인 통과
                            val user = SignupInfo(
                                binding.signupIdEt.text.toString(),
                                binding.signupPwdEt.text.toString(),
                                binding.signupNameEt.text.toString(),
                                binding.signupInfoEt.text.toString()
                            )
                            createUser(user)

                        } else {
                            binding.signupErrorTv.visibility = View.VISIBLE
                            binding.signupErrorTv.text = "비밀번호를 확인해주세요"
                        }
                    }
                }
                binding.signupErrorTv.visibility = View.VISIBLE
                binding.signupErrorTv.text = "이메일을 확인해주세요"
            }



        }
    }
    private fun createUser(user:SignupInfo) {
        auth.createUserWithEmailAndPassword(user.email, user.pwd)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    inputUserInfo(task.result.user?.let { UserInfo(user.email,user.name,user.info, it.uid, null ) })
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun inputUserInfo(user: UserInfo?) {
        user?.let {
            FirebaseFirestore.getInstance()
                .collection("clients")
                .document(user.uid)
                .set(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "정보 삽입 완료", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "정보 삽입 실패", Toast.LENGTH_LONG).show()
                }
        }
    }
}