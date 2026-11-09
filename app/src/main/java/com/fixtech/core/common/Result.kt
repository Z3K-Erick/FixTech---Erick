package com.fixtech.core.common

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}

typealias Result<T> = Resource<T>

object Constants {
    const val DATABASE_NAME = "fixtech_database.db"
    const val DEFAULT_USER_ID = "local_user"
    const val DEFAULT_TECH_ID = "admin-current"
    const val DEFAULT_TECH_NAME = "Carlos M."
    const val SECONDARY_TECH_ID = "admin-other"
    const val SECONDARY_TECH_NAME = "Ana R."
    const val TOOLS_SEPARATOR = "||"
}
