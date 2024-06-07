package com.lokavo.ui.login

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.databinding.ActivityLoginBinding
import com.lokavo.ui.MainActivity
import com.lokavo.ui.confirmEmail.ConfirmEmailActivity
import com.lokavo.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.apply {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            val color = typedValue.data
            val colorDrawable = ColorDrawable(color)
            supportActionBar?.setBackgroundDrawable(colorDrawable)
            setDisplayHomeAsUpEnabled(true)
            title = ""
            elevation = 0f
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val pass = binding.edPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showErrorSnackbar("Kesalahan terjadi atau tidak ada koneksi internet")
                    }
                }

            } else {
                showErrorSnackbar("Tidak boleh ada fields yang kosong")
            }
        }

        binding.btnForgot.setOnClickListener {
            val intent = Intent(this, ConfirmEmailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showErrorSnackbar(message: String) {
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