package ru.netology.nework.model

import android.net.Uri
import ru.netology.nework.extens.AttachmentType
import java.io.InputStream

data class MediaModel(
    val uri: Uri? = null,
    val inputStream: InputStream? = null,
    val type: AttachmentType? = null,
)
