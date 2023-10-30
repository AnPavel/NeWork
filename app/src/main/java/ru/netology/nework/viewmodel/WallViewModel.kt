package ru.netology.nework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.repository.WallRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    fun loadUserWall(id: Long) = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            wallRepository.loadUserWall(id).map { pagingData ->
                pagingData.map { post ->
                    post.copy(
                        ownedByMe = post.authorId == myId,
                        likedByMe = post.likeOwnerIds.contains(myId)
                    )
                }
            }
        }
}
