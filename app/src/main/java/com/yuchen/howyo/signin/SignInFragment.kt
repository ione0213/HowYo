package com.yuchen.howyo.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.data.User
import com.yuchen.howyo.databinding.FragmentSignInBinding
import com.yuchen.howyo.ext.getVmFactory

const val RC_SIGN_IN = 0X00

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    val viewModel by viewModels<SignInViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        createSignInIntent()

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.createUserResult.observe(viewLifecycleOwner) {
            it?.let {
                when {
                    it.isNotEmpty() -> {
                        viewModel.setUser()
                        when (UserManager.isLoggedIn) {
                            true -> {
                                findNavController().navigate(NavigationDirections.navToHomeFragment())
                                mainViewModel.setIsAccessAppFirstTime()
                            }
                            false -> {}
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val currentUser = Firebase.auth.currentUser

                when {
                    currentUser != null -> {
                        val user = User(
                            id = currentUser.uid,
                            name = currentUser.displayName,
                            email = currentUser.email,
                            avatar = currentUser.photoUrl.toString()
                        )

                        viewModel.createUser(user)
                    }
                }
            } else {
                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    return
                }

                if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(
                        HowYoApplication.instance,
                        response.error?.errorCode.toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return
                }

                activity?.finish()
            }
        } else {
            activity?.finish()
        }
    }
}
