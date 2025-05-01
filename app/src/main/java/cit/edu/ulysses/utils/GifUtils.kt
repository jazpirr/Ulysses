package cit.edu.ulysses.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object GifUtils {
    fun loadGif(context: Context, imageView: ImageView, gifUrl: String) {
        Glide.with(context)
            .asGif()
            .load(gifUrl)
            .apply(RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop())
            .into(imageView)
    }

    fun loadGifFromResource(context: Context, imageView: ImageView, resourceId: Int) {
        Glide.with(context)
            .asGif()
            .load(resourceId)
            .apply(RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop())
            .into(imageView)
    }
} 