package com.lokavo.ui.register

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.lokavo.databinding.ActivityRegisterBinding
import com.lokavo.ui.login.LoginActivity
import com.lokavo.utils.showSnackbar

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
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
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

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.btnRegister.windowToken, 0)

        if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.root.showSnackbar("Format email tidak valid")
            } else if (pass.length < 6) {
                binding.root.showSnackbar("Password harus memiliki minimal 6 karakter")
            } else if (pass == confirmPass) {
                registerUser(email, pass, name)
            } else {
                binding.root.showSnackbar("Password tidak sama")
            }
        } else {
            binding.root.showSnackbar("Tidak boleh ada bagian yang kosong")
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
                                Toast.makeText(this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show()
                                firebaseAuth.signOut()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                binding.root.showSnackbar(it.exception.toString())
                            }
                        }
                }
            } else if (it.exception is FirebaseAuthUserCollisionException) {
                binding.progress.visibility = View.GONE
                binding.root.showSnackbar("Email sudah terdaftar")
            } else {
                binding.progress.visibility = View.GONE
                binding.root.showSnackbar("Terjadi Kesalahan")
            }
        }
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