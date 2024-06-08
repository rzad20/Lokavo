package com.lokavo.ui.changePassword

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.databinding.FragmentChangePasswordBinding
import com.lokavo.ui.welcome.WelcomeActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            show()
            title = "Ubah Password"
            elevation = 0f
            val typedValue = TypedValue()
            activity?.theme?.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            val color = typedValue.data
            val colorDrawable = ColorDrawable(color)
            setBackgroundDrawable(colorDrawable)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun changePassword() {
        val newPassword = binding.edNewPassword.text.toString()
        val confirmPassword = binding.edConfirmNewPassword.text.toString()
        val currentPassword = binding.edOldPassword.text.toString()

        when {
            newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty() -> {
                binding.root.showSnackbar("Silahkan isi semua kolom")
            }
            newPassword.length < 6 -> {
                binding.root.showSnackbar("Password harus memiliki minimal 6 karakter")
            }
            newPassword != confirmPassword -> {
                binding.root.showSnackbar("Password dan konfirmasi password tidak cocok")
            }
            !requireContext().isOnline() -> {
                binding.root.showSnackbar("Tidak ada koneksi internet")
            }
            else -> {
                val user = firebaseAuth.currentUser
                user?.let {
                    val credential = EmailAuthProvider.getCredential(it.email!!, currentPassword)
                    binding.progress.visibility = View.VISIBLE
                    it.reauthenticate(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            it.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                binding.progress.visibility = View.GONE
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Password berhasil diubah",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity?.finish()
                                } else {
                                    binding.root.showSnackbar("Gagal mengubah password")
                                }
                            }
                        } else {
                            binding.progress.visibility = View.GONE
                            binding.root.showSnackbar("Password lama tidak cocok")
                        }
                    }
                } ?: run {
                    startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = null
    }

}
