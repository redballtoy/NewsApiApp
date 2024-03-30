package com.gmail.redballtoy.news_data

interface RequestResponseMergeStrategy<E> {

    fun merge(right: RequestResult<E>, left: RequestResult<E>): RequestResult<E>
}