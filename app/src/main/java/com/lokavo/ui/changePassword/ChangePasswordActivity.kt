package com.lokavo.ui.changePassword

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.databinding.ActivityChangePasswordBinding
import com.lokavo.ui.welcome.WelcomeActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
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
        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val newPassword = binding.edNewPassword.text.toString()
        val confirmPassword = binding.edConfirmNewPassword.text.toString()
        val currentPassword = binding.edOldPassword.text.toString()

        if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
            if (newPassword.length < 6) {
                binding.root.showSnackbar("Password harus memiliki minimal 6 karakter")
            } else if (newPassword == confirmPassword) {
                if (this.isOnline()) {
                    val user = firebaseAuth.currentUser
                    if (user !== null) {
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        binding.progress.visibility = View.VISIBLE
                        user.reauthenticate(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    user.updatePassword(newPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                binding.progress.visibility = View.GONE
                                                Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }
                                } else {
                                    binding.progress.visibility = View.GONE
                                    binding.root.showSnackbar("Password lama tidak cocok")
                                }
                            }
                    } else {
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                } else {
                    binding.root.showSnackbar("Tidak ada koneksi internet")
                }
            } else {
                binding.root.showSnackbar("Password dan konfirmasi password tidak cocok")
            }
        } else {
            binding.root.showSnackbar("Silahkan isi semua kolom")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
