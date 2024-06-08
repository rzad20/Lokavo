package com.lokavo.ui.profileEdit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.lokavo.databinding.ActivityProfileEditBinding
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
import java.util.concurrent.CountDownLatch

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private var fileUri: Uri? = null
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            val color = typedValue.data
            val colorDrawable = ColorDrawable(color)
            supportActionBar?.setBackgroundDrawable(colorDrawable)
            setDisplayHomeAsUpEnabled(true)
            title = "Edit"
            elevation = 0f
        }

        if (user != null) {
            binding.edName.setText(user.displayName)
            binding.edEmail.setText(user.email)
            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .into(binding.userImage)
            }
        }

        binding.btnUpload.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnSave.windowToken, 0)

            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                val nameChanged = binding.edName.text.toString() != user?.displayName
                val imageChanged = fileUri != null

                val operations = mutableListOf<() -> Unit>()
                val latch = CountDownLatch(if (nameChanged) 1 else 0 + if (imageChanged) 1 else 0)

                var isUpdated = false

                if (nameChanged) {
                    operations.add {
                        updateName { success ->
                            isUpdated = isUpdated || success
                            if (!imageChanged) {
                                latch.countDown()
                            }
                        }
                    }
                }
                if (imageChanged) {
                    operations.add {
                        uploadImage { success ->
                            isUpdated = isUpdated || success
                            latch.countDown()
                        }
                    }
                }

                showLoading()
                operations.forEach { it.invoke() }

                Thread {
                    latch.await()
                    runOnUiThread {
                        hideLoading()
                        if (isUpdated) {
                            binding.root.showSnackbar("Profil berhasil diperbarui")
                            binding.btnSave.visibility = View.GONE
                        }
                    }
                }.start()
            }
        }

        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                checkForChanges()
            }
        })
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            fileUri = uri
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            binding.userImage.setImageBitmap(bitmap)
        }
        checkForChanges()
    }

    private fun checkForChanges() {
        val nameChanged = binding.edName.text.toString() != user?.displayName
        val imageChanged = fileUri != null

        if (nameChanged || imageChanged) {
            binding.btnSave.visibility = View.VISIBLE
        } else {
            binding.btnSave.visibility = View.GONE
        }
    }

    private fun updateName(callback: (Boolean) -> Unit) {
        val newName = binding.edName.text.toString()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
               binding.root.showSnackbar("Terjadi kesalahan")
                callback(false)
            }
        }
    }

    private fun uploadImage(callback: (Boolean) -> Unit) {
        fileUri?.let { uri ->
            val ref = FirebaseStorage.getInstance().getReference().child("images/${user?.uid}")
            ref.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUri)
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            fileUri = null
                            callback(true)
                        } else {
                            binding.root.showSnackbar("Terjadi kesalahan")
                            callback(false)
                        }
                    }
                } else {
                    binding.root.showSnackbar("Terjadi kesalahan")
                    callback(false)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.btnSave.isEnabled = true
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