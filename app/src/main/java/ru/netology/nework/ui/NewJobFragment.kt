package ru.netology.nework.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AndroidUtils.selectDateDialog
import ru.netology.nework.utils.pickDate
import ru.netology.nework.viewmodel.JobViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val jobViewModel by activityViewModels<JobViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_job)

        binding.editTextStartFragmentNewJob.setOnClickListener {
            selectDateDialog(binding.editTextStartFragmentNewJob, requireContext())
            val startDate = binding.editTextStartFragmentNewJob.text.toString()
            jobViewModel.startDate(startDate)
        }

        binding.editTextFinishFragmentNewJob.setOnClickListener {
            selectDateDialog(binding.editTextFinishFragmentNewJob, requireContext())
            val endDate = binding.editTextFinishFragmentNewJob.text.toString()
            jobViewModel.finishDate(endDate)
        }

        val name = arguments?.getString("name")
        val position = arguments?.getString("position")
        val start = arguments?.getString("start")
        val finish = arguments?.getString("finish")
        val link = arguments?.getString("link")

        binding.editTextNameFragmentNewJob.setText(name)
        binding.editTextPositionFragmentNewJob.setText(position)
        binding.editTextStartFragmentNewJob.setText(start)
        binding.editTextFinishFragmentNewJob.setText(
            if (finish == "") " " else finish
        )
        binding.editTextLinkFragmentNewJob.setText(link)


        binding.buttonSaveFragmentNewJob.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            binding.let {
                if (
                    it.editTextNameFragmentNewJob.text.isNullOrBlank() ||
                    it.editTextPositionFragmentNewJob.text.isNullOrBlank() ||
                    it.editTextStartFragmentNewJob.text.isNullOrBlank()
                ) {
                    Toast.makeText(
                        activity,
                        R.string.error_field_required,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    jobViewModel.changeJobData(
                        it.editTextNameFragmentNewJob.text.toString(),
                        it.editTextPositionFragmentNewJob.text.toString(),
                        it.editTextStartFragmentNewJob.text.toString(),
                        it.editTextFinishFragmentNewJob.text.toString(),
                        it.editTextLinkFragmentNewJob.text.toString(),
                    )
                    jobViewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                }
            }
        }

        binding.editTextStartFragmentNewJob.setOnClickListener {
            context?.let { item ->
                pickDate(binding.editTextStartFragmentNewJob, item)
            }
        }

        binding.editTextFinishFragmentNewJob.setOnClickListener {
            context?.let { item ->
                pickDate(binding.editTextFinishFragmentNewJob, item)
            }
        }

        jobViewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentNewJob.isVisible = it.loading
        }

        return binding.root
    }
}
