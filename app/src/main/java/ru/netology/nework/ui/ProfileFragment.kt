package ru.netology.nework.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.adapter.UserProfileAdapter
import ru.netology.nework.databinding.FragmentProfileBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.JobViewModel
import ru.netology.nework.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val authViewModel by activityViewModels<AuthViewModel>()

    private val postViewModel by activityViewModels<PostViewModel>()

    private val eventViewModel by activityViewModels<EventViewModel>()

    private val jobViewModel by activityViewModels<JobViewModel>()

    private val profileTitles = arrayOf(
        R.string.title_posts,
        R.string.title_jobs,
        R.string.title_events,
        R.string.title_posts,
    )

    private var visibilityFabGroup = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val viewPagerProfile = binding.viewPagerFragmentProfile
        val tabLayoutProfile = binding.tabLayoutFragmentProfile
        val id = arguments?.getLong("id")
        val avatar = arguments?.getString("avatar")
        val name = arguments?.getString("name")

        (activity as AppCompatActivity).supportActionBar?.title = name

        viewPagerProfile.adapter = UserProfileAdapter(this)

        TabLayoutMediator(tabLayoutProfile, viewPagerProfile) { tab, position ->
            tab.text = getString(profileTitles[position])
            Log.d("MyAppLog", "ProfileFragment * start tab : ${tab.text}")
        }.attach()

        Log.d("MyAppLog", "ProfileFragment * start binding : $id / $name / $avatar")

        with(binding) {
            textViewUserNameFragmentProfile.text = name

            imageViewUserAvatarFragmentProfile.setOnClickListener {
                Log.d("MyAppLog", "ProfileFragment * imageViewUserAvatarFragmentProfile : $avatar")
                val bundle = Bundle().apply {
                    putString("url", avatar)
                }
                findNavController().navigate(R.id.navImageAttachmentFragment, bundle)
            }

            Glide.with(imageViewUserAvatarFragmentProfile)
                .load("$avatar")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_baseline_user_24)
                .into(imageViewUserAvatarFragmentProfile)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized && id == it.id) {
                binding.fabAdd.visibility = View.VISIBLE
            }
        }

        binding.fabAdd.setOnClickListener {
            if (!visibilityFabGroup) {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_close_24)
                binding.fabGroup.visibility = View.VISIBLE
            } else {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_add_24)
                binding.fabGroup.visibility = View.GONE
            }
            visibilityFabGroup = !visibilityFabGroup
        }

        binding.fabAddPost.setOnClickListener {
            postViewModel.edit(Post.emptyPost)
            findNavController().navigate(R.id.action_navProfileFragment_to_newPostFragment)
        }

        binding.fabAddEvent.setOnClickListener {
            eventViewModel.edit(Event.emptyEvent)
            findNavController().navigate(R.id.action_navProfileFragment_to_newEventFragment)
        }

        binding.fabAddJob.setOnClickListener {
            jobViewModel.edit(Job.emptyJob)
            findNavController().navigate(R.id.action_navProfileFragment_to_newJobFragment)
        }

        return binding.root
    }
}
