package com.gmail.redballtoy.news_data

interface MergeStrategy<E> {

    fun merge(right: E, left: E): E
}

internal class DefaultResponseMergeStrategy<T: Any> : MergeStrategy<RequestResult<T>> {

    override fun merge(right: RequestResult<T>, left: RequestResult<T>): RequestResult<T> {
        return when {
            right is RequestResult.InProgress && left is RequestResult.InProgress ->
                merge(right, left)
            right is RequestResult.Success && left is RequestResult.InProgress ->
                merge(right, left)

            else -> error("Unimplemented branch")
        }
    }

    private fun merge(
        right: RequestResult.InProgress<T>,
        left: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return when {
            left.data != null -> RequestResult.InProgress(left.data)
            else -> RequestResult.InProgress(right.data)
        }
    }

    private fun merge(
        right: RequestResult.Success<T>,
        left: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(right.data)
    }
}