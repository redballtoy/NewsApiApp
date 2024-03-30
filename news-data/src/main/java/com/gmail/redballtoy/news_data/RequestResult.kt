package com.gmail.redballtoy.news_data

sealed class RequestResult<out E : Any>(internal val data: E? = null) {

    class InProgress<E : Any>(data: E? = null) : RequestResult<E>(data)

    class Success<E : Any>(data: E) : RequestResult<E>(data)

    class Error<E : Any>(data: E? = null) : RequestResult<E>(data)
}

//custom mapping
internal fun <In: Any, Out: Any> RequestResult<In>.map(mapper: (In) -> Out): RequestResult<Out> {
    return when (this) {
        is RequestResult.Success -> {
            val outData: Out = mapper(checkNotNull(data))
            RequestResult.Success(outData)
        }

        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
    }
}


//convert Result(kotlin) to ResultRequest
internal fun <T: Any> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}