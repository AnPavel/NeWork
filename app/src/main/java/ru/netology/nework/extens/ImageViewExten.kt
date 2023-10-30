package ru.netology.nework.extens

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nework.R

object ImageViewExten {

    fun loadCircleImageView(imageView: ImageView, url: String){
        Glide.with(imageView)
            .load(url)
            .placeholder(R.drawable.ic_avatar_loading)
            .error(R.drawable.ic_baseline_user_24)
            .timeout(10_000)
            .circleCrop()
            .into(imageView)
    }

    fun loadImageView(imageView: ImageView, url: String){
        Glide.with(imageView)
            .load(url)
            .placeholder(R.drawable.ic_avatar_loading)
            .error(R.drawable.ic_baseline_attach_file_24)
            .timeout(10_000)
            .into(imageView)
    }


}
