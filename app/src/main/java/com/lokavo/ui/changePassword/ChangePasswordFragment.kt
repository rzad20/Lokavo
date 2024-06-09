package com.lokavo.ui.changePassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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

        // Mengatur toolbar
        val toolbar = binding.topAppBar
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }
    }
    private fun changePassword() {
        val newPassword = binding.edNewPassword.text.toString()
        val confirmPassword = binding.edConfirmNewPassword.text.toString()
        val currentPassword = binding.edOldPassword.text.toString()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

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
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
