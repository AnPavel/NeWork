package ru.netology.nework.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentSignInBinding
import ru.netology.nework.utils.AndroidUtils.hideKeyboard
import ru.netology.nework.viewmodel.SignInViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            textFieldLogin.requestFocus()
            buttonSignIn.setOnClickListener {
                Log.d("MyAppLog", "SignInFragment * buttonSignIn: $textFieldLogin.editText?.text.toString() / $textFieldPassword.editText?.text.toString()")
                viewModel.authorizationUser(
                    textFieldLogin.editText?.text.toString(),
                    textFieldPassword.editText?.text.toString()
                )
                hideKeyboard(requireView())
            }
        }

        binding.textFieldPassword.setErrorIconOnClickListener {
            Log.d("MyAppLog", "SignInFragment * password (error):")
            binding.textFieldPassword.error = null
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            Log.d("MyAppLog", "SignInFragment * data: ${it.id} / ${it.token}")
            findNavController().navigate(R.id.navPostsFragment)
        }

        //вернуться обратно
        binding.buttonReturnOut.setOnClickListener {
            Log.d("MyAppLog", "SignInFragment * buttonReturnOut: Выход без сохранения")
            viewModel.clean()
            findNavController().navigateUp()
        }

        //зарегистрироваться
        binding.buttonSignUp.setOnClickListener {
            Log.d("MyAppLog", "SignInFragment * buttonSignUp: Перейти к регистрации")
            findNavController().navigate(R.id.navSignUpFragment)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.loginError -> {
                    binding.textFieldPassword.error = getString(R.string.error_login)
                }
                state.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentSignIn.isVisible = state.loading
        }

        return binding.root
    }
}
