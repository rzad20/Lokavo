package com.lokavo.ui.confirmEmail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.databinding.ActivityConfirmEmailBinding
import com.lokavo.ui.login.LoginActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection

class ConfirmEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConfirmEmailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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

        binding.btnProccess.setOnClickListener {
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                val email = binding.edEmail.text.toString()
                if (email.isEmpty()) {
                    binding.root.showSnackbar("Email tidak boleh kosong")
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.root.showSnackbar("Format email tidak valid")
                } else {
                    showLoading()
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            hideLoading()
                            showConfirmationDialog()
                        }
                        .addOnFailureListener {
                            hideLoading()
                            binding.root.showSnackbar("Terjadi kesalahan")
                        }
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Silahkan cek email anda untuk verifikasi")
            .setPositiveButton("OK") { dialog, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.btnProccess.isEnabled = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.btnProccess.isEnabled = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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