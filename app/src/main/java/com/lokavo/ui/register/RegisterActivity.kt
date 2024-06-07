package com.lokavo.ui.register

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lokavo.R
import com.lokavo.databinding.ActivityRegisterBinding
import com.lokavo.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupActionBar()
        setupLoginButton()
        setupRegisterButton()
    }

    private fun setupBinding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            val color = typedValue.data
            val colorDrawable = ColorDrawable(color)
            setBackgroundDrawable(colorDrawable)
            setDisplayHomeAsUpEnabled(true)
            title = ""
            elevation = 0f
        }
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val pass = binding.edPassword.text.toString()
            val confirmPass = binding.edConfirmPassword.text.toString()
            val name = binding.edName.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
                if (pass == confirmPass) {
                    registerUser(email, pass, name)
                } else {
                    showSnackbar("Password tidak sama")
                }
            } else {
                showSnackbar("Tidak boleh ada bagian yang kosong")
            }
        }
    }

    private fun registerUser(email: String, pass: String, name: String) {
        binding.progress.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let { usr ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    usr.updateProfile(profileUpdates)
                        .addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                binding.progress.visibility = View.GONE
                                showSnackbar("Register Berhasil")
                                firebaseAuth.signOut()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                showSnackbar(it.exception.toString())
                            }
                        }
                }
            } else {
                showSnackbar("Terjadi Kesalahan")
            }
        }
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val context = snackbarView.context
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.snackbarStyle, typedValue, true)
        val backgroundColor = typedValue.data

        snackbarView.setBackgroundColor(backgroundColor)
        snackbar.setTextColor(context.getColor(R.color.white))

        snackbar.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> false
        }
    }
}