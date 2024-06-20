package com.lokavo.ui.changePassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.databinding.FragmentChangePasswordBinding
import com.lokavo.ui.welcome.WelcomeActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection

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

        val toolbar = binding.topAppBar
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnChangePassword.windowToken, 0)

            if (!requireContext().isOnline()) {
                binding.root.showSnackbarOnNoConnection(requireContext())
            } else {
                changePassword()
            }
        }
        val navController = findNavController()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_profile) {
                navController.popBackStack(R.id.navigation_profile, false)
            }
        }

    }

    private fun changePassword() {
        val newPassword = binding.edNewPassword.text.toString()
        val confirmPassword = binding.edConfirmNewPassword.text.toString()
        val currentPassword = binding.edOldPassword.text.toString()

        when {
            newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty() -> {
                binding.root.showSnackbar(getString(R.string.please_fill_all_fields))
            }

            newPassword.length < 6 -> {
                binding.root.showSnackbar(getString(R.string.new_password_error))
            }

            newPassword != confirmPassword -> {
                binding.root.showSnackbar(getString(R.string.password_confirmation_not_match))
            }

            else -> {
                val user = firebaseAuth.currentUser
                user?.let {
                    val credential = EmailAuthProvider.getCredential(it.email!!, currentPassword)
                    showLoading()
                    it.reauthenticate(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            it.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                hideLoading()
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.password_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity?.supportFragmentManager?.popBackStack()
                                } else {
                                    binding.root.showSnackbar(getString(R.string.password_failed))
                                }
                            }
                        } else {
                            hideLoading()
                            if (authTask.exception?.message?.contains("incorrect") == true) {
                                binding.root.showSnackbar(getString(R.string.password_not_match))
                            } else {
                                binding.root.showSnackbar(getString(R.string.terjadi_kesalahan))
                            }
                        }
                    }
                } ?: run {
                    startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.btnChangePassword.isEnabled = false
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.btnChangePassword.isEnabled = true
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
