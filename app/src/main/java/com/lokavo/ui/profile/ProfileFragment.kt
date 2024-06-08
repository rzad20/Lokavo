package com.lokavo.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lokavo.R
import com.lokavo.databinding.FragmentProfileBinding
import com.lokavo.ui.profileEdit.ProfileEditActivity
import com.lokavo.ui.welcome.WelcomeActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var firebaseAuth: FirebaseAuth
    private var uri: Uri? = null
    private var name: String? = null
    private var email: String? = null
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        profileViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.darkModeSwitch.isChecked = isDarkModeActive
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            profileViewModel.saveThemeSetting(isChecked)
        }
        binding.logoutButton.setOnClickListener {
            if (!requireContext().isOnline()) {
                binding.root.showSnackbarOnNoConnection(requireContext())
            } else {
                showLogoutConfirmationDialog()
            }
        }
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_changePasswordFragment)
        }

        binding.detailProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivity(intent)
        }

        if (user != null) {
            name = user?.displayName
            email = user?.email
            binding.profileName.text = user?.displayName
            binding.userEmail.text = user?.email
            if (user?.photoUrl != null) {
                uri = user?.photoUrl
                Glide.with(this)
                    .load(uri)
                    .into(binding.userImage)
            }
        }
    }

override fun onResume() {
    super.onResume()
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        startActivity(intent)
    }
    user?.reload()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            if (uri != user.photoUrl) {
                uri = user.photoUrl
                Glide.with(this)
                    .load(uri)
                    .into(binding.userImage)
            }
            if (name != user.displayName) {
                name = user.displayName
                binding.profileName.text = user.displayName
            }
        }
    }
}

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin untuk keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                firebaseAuth.signOut()
                proceedToLogout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun proceedToLogout() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}