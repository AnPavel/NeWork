package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Media
import ru.netology.nework.dto.MediaUpload

interface EventRepository {

    val data: Flow<PagingData<Event>>
    suspend fun saveEvent(event: Event)
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload)
    suspend fun uploadWithContent(upload: MediaUpload): Media
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun participate(id: Long)
    suspend fun doNotParticipate(id: Long)
}
