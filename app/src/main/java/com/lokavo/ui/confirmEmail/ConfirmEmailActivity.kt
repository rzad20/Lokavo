package com.lokavo.ui.confirmEmail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.databinding.ActivityConfirmEmailBinding
import com.lokavo.ui.login.LoginActivity
import com.lokavo.utils.showSnackbar

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
            val email = binding.edEmail.text.toString()

            if (email.isEmpty()) {
                binding.root.showSnackbar("Email tidak boleh kosong")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.root.showSnackbar("Format email tidak valid")
            } else {
                binding.progress.visibility = View.VISIBLE
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        binding.progress.visibility = View.GONE
                        showConfirmationDialog()
                    }
                    .addOnFailureListener {
                        binding.progress.visibility = View.GONE
                        binding.root.showSnackbar("Terjadi kesalahan")
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