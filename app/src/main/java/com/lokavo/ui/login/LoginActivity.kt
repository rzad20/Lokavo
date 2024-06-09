package com.lokavo.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.lokavo.R
import com.lokavo.databinding.ActivityLoginBinding
import com.lokavo.ui.MainActivity
import com.lokavo.ui.confirmEmail.ConfirmEmailActivity
import com.lokavo.ui.register.RegisterActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                val email = binding.edEmail.text.toString()
                val pass = binding.edPassword.text.toString()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)

                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.root.showSnackbar("Format email tidak valid")
                    } else if (pass.length < 6) {
                        binding.root.showSnackbar("Password harus memiliki minimal 6 karakter")
                    } else {
                        showLoading()
                        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                            if (it.isSuccessful) {
                                hideLoading()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                hideLoading()
                                if (it.exception is FirebaseAuthInvalidCredentialsException) {
                                    binding.root.showSnackbar("Email atau password tidak cocok")
                                } else {
                                    binding.root.showSnackbar("Terjadi kesalahan")
                                }
                            }
                        }
                    }
                } else {
                    binding.root.showSnackbar("Tidak boleh ada bagian yang kosong")
                }
            }
        }

        binding.btnForgot.setOnClickListener {
            val intent = Intent(this, ConfirmEmailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}