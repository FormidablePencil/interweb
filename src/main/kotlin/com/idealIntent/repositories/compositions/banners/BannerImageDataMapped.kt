package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.repositories.compositions.headers.IDataMapper

class BannerImageDataMapped : IDataMapper<BannerImageRes> {
    val data: MutableList<Pair<Int, BannerImageRes>> = mutableListOf()
    val privilegedAuthors: MutableList<Pair<Int, PrivilegedAuthor>> = mutableListOf()

    override fun get(): List<BannerImageRes> {
        TODO("Not yet implemented")
    }
}