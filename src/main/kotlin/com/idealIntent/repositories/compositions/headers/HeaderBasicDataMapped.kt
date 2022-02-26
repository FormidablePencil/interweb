package com.idealIntent.repositories.compositions.headers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.headers.HeaderBasicRes

class HeaderBasicDataMapped : IDataMapper<HeaderBasicRes> {
    var data: HeaderBasicRes? = null
    val privilegedAuthors = mutableListOf<PrivilegedAuthor>()

    override fun get(): List<HeaderBasicRes> {
        data?.let {
            return listOf(
                HeaderBasicRes(
                    id = it.id,
                    name = it.name,
                    sourceId = it.sourceId,
                    bgImg = it.bgImg,
                    profileImg = it.profileImg,
                    privilegeLevel = it.privilegeLevel,
                    privilegedAuthors = privilegedAuthors,
                )
            )
        }
        return listOf()
    }
}