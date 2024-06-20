package com.lokavo.ui.profileDetail

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.lokavo.R
import com.lokavo.databinding.FragmentProfileDetailBinding
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
import java.util.concurrent.CountDownLatch

class ProfileDetailFragment : Fragment() {
    private var _binding: FragmentProfileDetailBinding? = null
    private val binding get() = _binding!!
    private var fileUri: Uri? = null
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val toolbar = binding.topAppBar
        toolbar.menu.findItem(R.id.save).isVisible = false

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
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

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    saveChanges()
                    true
                }

                else -> false
            }
        }

        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

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
            val bitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(context?.contentResolver, fileUri)
            binding.userImage.setImageBitmap(bitmap)
        }
        checkForChanges()
    }

    private fun saveChanges() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.topAppBar.windowToken, 0)

        if (!requireContext().isOnline()) {
            binding.root.showSnackbarOnNoConnection(requireContext())
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
                activity?.runOnUiThread {
                    hideLoading()
                    if (isUpdated) {
                        binding.root.showSnackbar(getString(R.string.profile_updated))
                        binding.topAppBar.menu.findItem(R.id.save).isVisible = false
                    }
                }
            }.start()
        }
    }

    private fun checkForChanges() {
        val nameChanged = binding.edName.text.toString() != user?.displayName
        val imageChanged = fileUri != null

        binding.topAppBar.menu.findItem(R.id.save).isVisible = nameChanged || imageChanged
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
                binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
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
                            binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
                            callback(false)
                        }
                    }
                } else {
                    binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
                    callback(false)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.topAppBar.menu.findItem(R.id.save).isEnabled = false
        binding.btnUpload.isEnabled = false
        binding.edName.isEnabled = false
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.topAppBar.menu.findItem(R.id.save).isEnabled = true
        binding.btnUpload.isEnabled = true
        binding.edName.isEnabled = true
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }

            else -> false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
