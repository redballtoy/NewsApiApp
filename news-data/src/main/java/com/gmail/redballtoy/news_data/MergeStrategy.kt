package com.gmail.redballtoy.news_data

interface MergeStrategy<E> {

    fun merge(left: E, right: E): E
}

internal class DefaultMergeStrategy<T : Any> : MergeStrategy<RequestResult<T>> {

    override fun merge(right: RequestResult<T>, left: RequestResult<T>): RequestResult<T> {
        return when {
            right is RequestResult.InProgress && left is RequestResult.InProgress ->
                merge(right, left)

            right is RequestResult.Success && left is RequestResult.InProgress ->
                merge(right, left)

            right is RequestResult.InProgress && left is RequestResult.Success ->
                merge(right, left)

            right is RequestResult.Success && left is RequestResult.Error ->
                merge(right, left)

            right is RequestResult.Success && left is RequestResult.Success ->
                merge(right, left)

            right is RequestResult.InProgress && left is RequestResult.Error ->
                merge(right, left)

            right is RequestResult.Error && left is RequestResult.InProgress ->
                merge(right, left)

            right is RequestResult.Error && left is RequestResult.Success ->
                merge(right, left)

            else -> error("Unimplemented branch right=$right & left=$left")
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

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cacheDB: RequestResult.Success<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(cacheDB.data)
    }

    @Suppress("UNUSED_PARAMETER")
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
        return RequestResult.Error(data = cacheDB.data, error = server.error)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cacheDB: RequestResult.Success<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.Success(data = server.data)
    }


    private fun merge(
        cacheDB: RequestResult.InProgress<T>,
        server: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = server.data ?: cacheDB.data, error = server.error)
    }

    private fun merge(
        cacheDB: RequestResult.Error<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return server
    }

    private fun merge(
        cacheDB: RequestResult.Error<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return server
    }

}