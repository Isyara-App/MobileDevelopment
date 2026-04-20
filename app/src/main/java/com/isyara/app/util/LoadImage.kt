package com.isyara.app.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class LoadImage {
    companion object {

        fun load(
            context: Context,
            imageView: ImageView?,
            imageUrl: String,
            placeholder: Int,
            isCircle: Boolean = false,
            keepFullImageVisible: Boolean = false
        ) {
            val placeholderDrawable = ColorDrawable(ContextCompat.getColor(context, placeholder))
            if (imageUrl.isEmpty()) {
                imageView?.setImageDrawable(placeholderDrawable)
                return
            }
            
            var finalUrl = imageUrl
            if (finalUrl.startsWith("/storage/")) {
                finalUrl = com.isyara.app.data.remote.retrofit.ApiConfig.BASE_URL.dropLast(1) + finalUrl
            } else if (finalUrl.startsWith("storage/")) {
                finalUrl = com.isyara.app.data.remote.retrofit.ApiConfig.BASE_URL + finalUrl
            }

            val picassoBuilder = Picasso.get()
                .load(finalUrl)
                .placeholder(placeholderDrawable)
                .error(placeholderDrawable)
                .fit()

            if (keepFullImageVisible) {
                picassoBuilder.centerInside().onlyScaleDown()
            } else {
                picassoBuilder.centerCrop()
            }

            if (isCircle) {
                picassoBuilder.transform(CropCircleTransformation())
            } else {
                picassoBuilder.transform(RoundedCornersTransformation(12, 0))
            }

            picassoBuilder.into(imageView)
        }
    }
}
