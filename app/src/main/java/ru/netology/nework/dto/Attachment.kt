package ru.netology.nework.dto

import ru.netology.nework.extens.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
