package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.banners.BannerBasicRes
import com.idealIntent.repositories.compositions.headers.IDataMapper

class BannerImageDataMapped : IDataMapper<BannerBasicRes> {
    val data: MutableList<Pair<Int, BannerBasicRes>> = mutableListOf()
    val privilegedAuthors: MutableList<Pair<Int, PrivilegedAuthor>> = mutableListOf()

    override fun get(): List<BannerBasicRes> {
        TODO("Not yet implemented")
    }
}