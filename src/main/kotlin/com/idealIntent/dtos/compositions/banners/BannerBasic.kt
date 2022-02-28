package com.idealIntent.dtos.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import kotlinx.serialization.Serializable

@Serializable
data class BannerImageCreateReq(
    val name: String,
    val privilegeLevel: Int,
    val privilegedAuthors: List<PrivilegedAuthor>,

    val imageUrl: String,
    val imageAlt: String,
)

@Serializable
data class BannerImageRes(
    val compositionId: Int,
    val sourceId: Int,
    val name: String,
    val privilegeLevel: Int,
    val privilegedAuthors: List<PrivilegedAuthor>,

    val imageUrl: String,
    val imageAlt: String,
)