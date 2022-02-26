package com.idealIntent.repositories.compositions.headers

interface IDataMapper<ResponseData> {
    /**
     * In a list in case if querying multiple composition at once.
     */
    fun get(): List<ResponseData?>
}