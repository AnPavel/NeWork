package ru.netology.nework.model

import ru.netology.nework.dto.Job

data class JobModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)
