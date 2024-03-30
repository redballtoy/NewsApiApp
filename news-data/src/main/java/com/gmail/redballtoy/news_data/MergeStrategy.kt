package com.gmail.redballtoy.news_data

interface MergeStrategy<E> {

    fun merge(left: E, right: E): E
}

internal class DefaultMergeStrategy<T: Any> : MergeStrategy<RequestResult<T>> {

    override fun merge(left: RequestResult<T>, right: RequestResult<T>): RequestResult<T> {
        return when {
            left is RequestResult.InProgress && right is RequestResult.InProgress ->
                merge(left, right)
            left is RequestResult.Success && right is RequestResult.InProgress ->
                merge(left, right)
            left is RequestResult.InProgress && right is RequestResult.Success ->
                merge(left, right)
            left is RequestResult.Success && right is RequestResult.Error ->
                merge(left, right)

            else -> error("Unimplemented branch")
        }
    }

    private fun merge(
        cacheDB: RequestResult.InProgress<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return when {
            server.data != null -> RequestResult.InProgress(server.data)
            else -> RequestResult.InProgress(cacheDB.data)
        }
    }

    private fun merge(
        cacheDB: RequestResult.Success<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(cacheDB.data)
    }

    private fun merge(
        cacheDB: RequestResult.InProgress<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(server.data)
    }

    private fun merge(
        cacheDB: RequestResult.Success<T>,
        server: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = cacheDB.data, error =server.error )
    }

}