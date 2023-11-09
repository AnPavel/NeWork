package ru.netology.nework.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.extens.AttachmentType.*
import ru.netology.nework.extens.*
import ru.netology.nework.utils.DataConverter

interface OnPostInteractionListener {
    fun onOpenPost(post: Post) {}
    fun onEditPost(post: Post) {}
    fun onRemovePost(post: Post) {}
    fun onLikePost(post: Post) {}
    fun onMentionPost(post: Post) {}
    fun onSharePost(post: Post) {}
    fun onOpenLikers(post: Post) {}
    fun onOpenMentions(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onPlayAudio(post: Post) {}
    fun onOpenImageAttachment(post: Post) {}
    fun goToPageUser(post: Post) {}
}

class PostsAdapter(
    private val onPostInteractionListener: OnPostInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onPostInteractionListener: OnPostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(post: Post) {

        binding.apply {
            authorCardPost.text = post.author
            publishedCardPost.text = DataConverter.convertDataTime(post.published)
            contentCardPost.text = post.content
            buttonLikeCardPost.text = "${post.likeOwnerIds.size}"
            buttonMentionCardPost.text = "${post.mentionIds.size}"

            imageViewAttachmentImageCardPost.visibility =
                if (post.attachment != null && post.attachment.type == IMAGE) VISIBLE else GONE

            groupAttachmentAudioCardPost.visibility =
                if (post.attachment != null && post.attachment.type == AUDIO) VISIBLE else GONE

            groupAttachmentVideoCardPost.visibility =
                if (post.attachment != null && post.attachment.type == VIDEO) VISIBLE else GONE

            var url = "${post.authorAvatar}"
            ImageViewExten.loadCircleImageView(imageViewAvatarCardPost, url)

            url = "${post.attachment?.url}"
            ImageViewExten.loadImageView(imageViewAttachmentImageCardPost, url)

            buttonMenuCardPost.isVisible = post.ownedByMe
            buttonMenuCardPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onPostInteractionListener.onRemovePost(post)
                                true
                            }
                            R.id.edit -> {
                                onPostInteractionListener.onEditPost(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            buttonLikeCardPost.setOnClickListener {
                val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1F, 1.25F, 1F)
                ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                    duration = 500
                    repeatCount = 1
                    interpolator = BounceInterpolator()
                }.start()
                onPostInteractionListener.onLikePost(post)
            }

            buttonMentionCardPost.visibility =
                if (post.ownedByMe) VISIBLE else INVISIBLE
            buttonMentionCardPost.setOnClickListener {
                onPostInteractionListener.onMentionPost(post)
            }

            buttonShareCardPost.setOnClickListener {
                onPostInteractionListener.onSharePost(post)
            }

            imageButtonBackgroundVideoCardPost.setOnClickListener {
                onPostInteractionListener.onPlayVideo(post)
            }

            imageButtonPlayVideoCardPost.setOnClickListener {
                onPostInteractionListener.onPlayVideo(post)
            }

            imageButtonPlayPauseAudioCardPost.setOnClickListener {
                onPostInteractionListener.onPlayAudio(post)
            }

            imageViewAttachmentImageCardPost.setOnClickListener {
                onPostInteractionListener.onOpenImageAttachment(post)
            }

            authorCardPost.setOnClickListener {
                onPostInteractionListener.goToPageUser(post)
            }

            contentCardPost.setOnClickListener {
                onPostInteractionListener.onOpenPost(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any = Unit
}
