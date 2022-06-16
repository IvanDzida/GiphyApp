package com.yolo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GiphyItem(
    val id: String,
    val url: String,
    val username: String?,
    val title: String?,
) : Parcelable