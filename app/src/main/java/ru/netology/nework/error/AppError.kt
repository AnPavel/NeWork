package ru.netology.nework.error

import java.io.IOException
import java.sql.SQLException

sealed class AppError(code: Int, message: String) : Exception(message) {

    companion object {
        fun from(e: Throwable) =
            when (e) {
                is IOException -> NetworkError
                is SQLException -> DbError
                is ApiError -> e
                else -> UnknownError
            }
    }
}

class ApiError(code: Int, message: String) : AppError(code, message)

object NetworkError : AppError(-1, "Network Error")

object DbError : AppError(-1, "DB Error")

object UnknownError : AppError(-1, "Unknown Error")
