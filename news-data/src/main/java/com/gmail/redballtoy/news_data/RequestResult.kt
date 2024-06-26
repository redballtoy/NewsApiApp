package com.gmail.redballtoy.news_data

sealed class RequestResult<out E : Any>(open val data: E? = null) {

    class InProgress<E : Any>(data: E? = null) : RequestResult<E>(data)

    class Success<E : Any>(override val data: E) : RequestResult<E>(data)

    class Error<E : Any>(data: E? = null, val error: Throwable? = null) : RequestResult<E>(data)
}

//custom mapping
fun <In : Any, Out : Any> RequestResult<In>.map(mapper: (In) -> Out): RequestResult<Out> {
    return when (this) {
        is RequestResult.Success -> {
            val outData: Out = mapper(data)
            RequestResult.Success(outData)
        }

        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
    }
}


//convert Result(kotlin) to ResultRequest
internal fun <T : Any> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}