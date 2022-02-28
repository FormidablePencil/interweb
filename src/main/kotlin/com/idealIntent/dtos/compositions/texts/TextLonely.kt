package com.idealIntent.dtos.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import kotlinx.serialization.Serializable

@Serializable
data class TextLonelyCreateReq(
    val name: String,
    val text: String,
    val privilegeLevel: Int,
    val privilegedAuthors: List<PrivilegedAuthor>,
)

@Serializable
data class TextLonelyRes(
    val compositionId: Int,
    val name: String,
    val text: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)
