package com.gmail.redballtoy.news_data

sealed class RequestResult<out E>(internal val data: E? = null) {

    class InProgress<E>(data: E? = null) : RequestResult<E>(data)

    class Success<E : Any>(data: E) : RequestResult<E>(data)

    class Error<E>(data: E? = null) : RequestResult<E>(data)
}

//custom mapping
internal fun <In, Out> RequestResult<In>.map(mapper: (In) -> Out): RequestResult<Out> {
    return when (this) {
        is RequestResult.Success -> {
            val outData: Out = mapper(checkNotNull(data))
            RequestResult.Success(checkNotNull(outData))
        }
        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
    }
}


//convert Result(kotlin) to ResultRequest
internal fun <T> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(checkNotNull(getOrThrow()))
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}