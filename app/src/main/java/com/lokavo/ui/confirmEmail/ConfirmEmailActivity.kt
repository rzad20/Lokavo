package com.lokavo.ui.confirmEmail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.databinding.ActivityConfirmEmailBinding
import com.lokavo.ui.login.LoginActivity

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
        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailInput = s.toString().trim()
                if (emailInput.isNotEmpty()) {
                    binding.btnProcess.isEnabled = true
                    binding.btnProcess.setBackgroundColor(ContextCompat.getColor(this@ConfirmEmailActivity, R.color.colorPrimary))
                } else {
                    binding.btnProcess.isEnabled = false
                    binding.btnProcess.setBackgroundColor(ContextCompat.getColor(this@ConfirmEmailActivity, R.color.lightBlue))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnProcess.isEnabled = false
        binding.btnProcess.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue))

        binding.btnProcess.setOnClickListener {
            firebaseAuth.sendPasswordResetEmail(binding.edEmail.text.toString())
                .addOnSuccessListener {
                    showConfirmationDialog()
                }
                .addOnFailureListener{
                    showSnackbar(it.message.toString())
                }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}