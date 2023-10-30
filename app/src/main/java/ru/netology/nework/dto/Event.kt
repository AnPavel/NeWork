package ru.netology.nework.dto

import ru.netology.nework.extens.EventType

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coordinates: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
) {
    companion object {
        val emptyEvent = Event(
            id = 0,
            authorId = 0,
            author = "",
            authorAvatar = "",
            content = "",
            published = "2023-01-01T12:00:00.000000Z",
            datetime = "2023-01-01T12:00:00.000000Z",
            type = EventType.ONLINE,
            speakerIds = emptySet(),
        )
    }
}
