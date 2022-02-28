package com.idealIntent.dtos.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import kotlinx.serialization.Serializable

@Serializable
data class TextLonelyCreateReq(
    val privilegeLevel: Int,
    val privilegedAuthors: List<PrivilegedAuthor>,

    val name: String,
    val text: String,
)

@Serializable
data class TextLonelyRes(
    val sourceId: Int,
    val compositionId: Int,
    val privilegeLevel: Int,
    val privilegedAuthors: List<PrivilegedAuthor>,

    val name: String,
    val text: String,
)
