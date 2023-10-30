package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.Job
import ru.netology.nework.model.JobModel
import ru.netology.nework.model.StateModel
import ru.netology.nework.repository.JobRepository
import ru.netology.nework.utils.SingleLiveEvent
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
)

@ExperimentalCoroutinesApi
@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    appAuth: AppAuth,
) : ViewModel() {

    val data: Flow<List<Job>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                jobRepository.data.map {
                    JobModel()
                    it.map { job ->
                        job.copy(
                            ownedByMe = userId.value == myId
                        )
                    }
                }
            }
    private val userId = MutableLiveData<Long>()

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    fun loadJobs(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            jobRepository.getJobByUserId(id)
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun setId(id: Long) {
        userId.value = id
    }

    fun save() {
        edited.value?.let { job ->
            viewModelScope.launch {
                _dataState.postValue(StateModel(loading = true))
                try {
                    jobRepository.saveJob(job)
                    _dataState.postValue(StateModel())
                    _jobCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.postValue(StateModel(error = true))
                }
            }
        }
        edited.value = empty
    }

    fun changeJobData(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?,
    ) {
        edited.value?.let {
            val textName = name.trim()
            if (edited.value?.name != textName) {
                edited.value = edited.value?.copy(name = textName)
            }

            val textPosition = position.trim()
            if (edited.value?.position != textPosition) {
                edited.value = edited.value?.copy(position = textPosition)
            }

            if (edited.value?.start != start) {
                edited.value = edited.value?.copy(start = start)
            }

            if (edited.value?.finish != finish) {
                edited.value = edited.value?.copy(finish = finish)
            }

            val textLink = link?.trim()
            if (edited.value?.link != textLink) {
                edited.value = edited.value?.copy(link = textLink)
            }
        }
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun removeById(id: Long) =
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
                jobRepository.removeById(id)
                _dataState.postValue(StateModel())
            } catch (e: Exception) {
                _dataState.postValue(StateModel(error = true))
            }
        }

    fun startDate(date: String) {
        edited.value = edited.value?.copy(start = date)
    }

    fun finishDate(date: String) {
        edited.value = edited.value?.copy(finish = date)
    }
}
