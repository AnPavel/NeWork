package ru.netology.nework.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostPreviewBinding
import ru.netology.nework.utils.DataConverter
import ru.netology.nework.utils.IntArg
import ru.netology.nework.viewmodel.PostViewModel


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PostPreviewFragment : Fragment()  {

    private val postViewModel by activityViewModels<PostViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentPostPreviewBinding.inflate(
            inflater,
            container,
            false
        )

        if (arguments?.intArg != null) {
            Log.d("MyAppLog", "PostPreviewFragment * : ${arguments?.intArg}")
            val id = arguments?.intArg
            id?.let { postViewModel.getPost(it.toLong()) }
        } else {
            Log.d("MyAppLog", "PostPreviewFragment * : 0")
            postViewModel.getPost(0)
        }
        binding.allPost.visibility = View.GONE

        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
            Log.d("MyAppLog", "PostPreviewFragment * post (state): $state")
            binding.progress.isVisible = state.refreshing

            if (state.loading) {
                Log.e("MyAppLog", "PostPreviewFragment * post (state.loading): $state")
                Snackbar.make(binding.root, R.string.problem_loading, Snackbar.LENGTH_SHORT).show()
            }
        }

        postViewModel.postResponse.observe(viewLifecycleOwner) { post ->
            Log.d("MyAppLog", "PostPreviewFragment * postViewModel: $post")
            if (post.id.toInt() == arguments?.intArg) {
                Log.d("MyAppLog", "PostPreviewFragment * пост найден: ${post.id.toInt()}")
                Log.d("MyAppLog", "PostPreviewFragment * пост найден: ${arguments?.intArg}")
                binding.allPost.visibility = View.VISIBLE
                binding.postContent.apply {

                    Glide.with(this@PostPreviewFragment)
                        .load(post.authorAvatar)
                        .error(R.drawable.ic_baseline_user_24)
                        .timeout(10_000)
                        .circleCrop()
                        .into(imageViewAvatarCardPost)

                    authorCardPost.text = post.author
                    publishedCardPost.text = DataConverter.convertDataTime(post.published)
                    contentCardPost.text = post.content
                    buttonLikeCardPost.isChecked = post.likedByMe

                }
            }

        }
        return binding.root
    }

    companion object {
        var Bundle.intArg: Int by IntArg
    }

}
