package com.idealIntent.repositories.compositions.headers

import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes

class HeaderBasicDataMapped : IDataMapper<HeaderBasicRes> {
    var data = mutableSetOf<HeaderBasicRes>()
    val images = mutableSetOf<Pair<Int, ImagePK>>()
    val imgOnclickRedirects = mutableSetOf<Pair<Int, TextPK>>()
    var privilegedAuthors = mutableSetOf<PrivilegedAuthor>()

    override fun get(): List<HeaderBasicRes> {
        return data.map { item ->
            with(item) {
                HeaderBasicRes(
                    id = id,
                    sourceId = sourceId,
                    bgImg = bgImg,
                    profileImg = profileImg,
                    privilegeLevel = privilegeLevel,
                    name = name,
                    privilegedAuthors = this@HeaderBasicDataMapped.privilegedAuthors.toList(),
                )
            }
        }
    }
}