package com.lokavo.ui.profileEdit

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.lokavo.databinding.ActivityProfileEditBinding
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection

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

        binding.btnSave.setOnClickListener {
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            }else{
                uploadImage()
            }
        }
        if (user?.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(binding.userImage)
        }

        binding.btnUpload.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            fileUri = uri
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            binding.userImage.setImageBitmap(bitmap)
        }
    }

    private fun uploadImage() {
        if (fileUri!= null){
            fileUri?.let { uri ->
                binding.progress.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
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
                                binding.progress.visibility = View.GONE
                                binding.btnSave.isEnabled = true
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil Diperbarui",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                binding.progress.visibility = View.GONE
                                binding.btnSave.isEnabled = true
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil Diperbarui",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        binding.progress.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                        Toast.makeText(applicationContext, "Gagal Diperbarui", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else {
            Toast.makeText(applicationContext, "Silahkan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
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