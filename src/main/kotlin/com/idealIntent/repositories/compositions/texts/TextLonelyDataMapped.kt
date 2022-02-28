package com.idealIntent.repositories.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.texts.TextLonelyRes
import com.idealIntent.repositories.compositions.headers.IDataMapper

class TextLonelyDataMapped : IDataMapper<TextLonelyRes> {
    val data: MutableList<Pair<Int, TextLonelyRes>> = mutableListOf()
    val privilegedAuthors: MutableList<Pair<Int, PrivilegedAuthor>> = mutableListOf()

    override fun get(): List<TextLonelyRes> {
        TODO("Not yet implemented")
    }
}