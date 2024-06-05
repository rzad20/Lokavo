package com.lokavo.ui.changePassword

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
import com.lokavo.R
import com.lokavo.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnChangePassword.isEnabled = binding.edOldPassword.text?.isNotEmpty() == true &&
                        binding.edNewPassword.text?.isNotEmpty() == true &&
                        binding.edConfirmNewPassword.text?.isNotEmpty() == true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.edOldPassword.addTextChangedListener(textWatcher)
        binding.edNewPassword.addTextChangedListener(textWatcher)
        binding.edConfirmNewPassword.addTextChangedListener(textWatcher)
        binding.btnChangePassword.isEnabled = false
        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.postDelayed({
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
        }, 2000)
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
