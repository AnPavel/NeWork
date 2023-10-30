package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentExitBinding
import kotlin.system.exitProcess

class ExitFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentExitBinding.inflate(inflater, container, false)

        binding.buttonYes.setOnClickListener {
            ActivityCompat.finishAffinity(requireActivity())
            exitProcess(0)
        }

        binding.buttonNo.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}
