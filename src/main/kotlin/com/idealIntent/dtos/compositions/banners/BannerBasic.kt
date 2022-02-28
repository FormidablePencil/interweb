package com.idealIntent.dtos.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import kotlinx.serialization.Serializable

@Serializable
data class BannerBasicCreateReq(
    val imageUrl: String,
    val imageAlt: String,
    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class BannerBasicRes(
    val compositionId: Int,
    val sourceId: Int,
    val imageUrl: String,
    val imageAlt: String,
//    val privilegeLevel: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)