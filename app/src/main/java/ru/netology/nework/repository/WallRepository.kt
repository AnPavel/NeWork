package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Post

interface WallRepository {

    fun loadUserWall(userId: Long): Flow<PagingData<Post>>
}
