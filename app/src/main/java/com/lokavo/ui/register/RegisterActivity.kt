package com.lokavo.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.lokavo.R
import com.lokavo.databinding.ActivityRegisterBinding
import com.lokavo.ui.login.LoginActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection

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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnRegister.windowToken, 0)
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                val email = binding.edEmail.text.toString()
                val pass = binding.edPassword.text.toString()
                val confirmPass = binding.edConfirmPassword.text.toString()
                val name = binding.edName.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.root.showSnackbar(getString(R.string.email_error))
                    } else if (pass.length < 6) {
                        binding.root.showSnackbar(getString(R.string.password_error))
                    } else if (pass == confirmPass) {
                        registerUser(email, pass, name)
                    } else {
                        binding.root.showSnackbar(getString(R.string.password_confirmation_not_match))
                    }
                } else {
                    binding.root.showSnackbar(getString(R.string.please_fill_all_fields))
                }
            }
        }
    }

    private fun registerUser(email: String, pass: String, name: String) {
        showLoading()
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
                                user.sendEmailVerification().addOnCompleteListener { sendTask ->
                                    hideLoading()
                                    if (sendTask.isSuccessful) {
                                        binding.progress.visibility = View.GONE
                                        Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                                        firebaseAuth.signOut()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        binding.progress.visibility = View.GONE
                                        binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
                                        user.delete()
                                    }
                                }
                            } else {
                                hideLoading()
                                binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
                                user.delete()
                            }
                        }
                }
            } else if (it.exception is FirebaseAuthUserCollisionException) {
                hideLoading()
                binding.root.showSnackbar(getString(R.string.email_already_exist))
            } else {
                hideLoading()
                binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.btnRegister.isEnabled = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    @Deprecated("Deprecated in Java")
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

            else -> false
        }
    }
}