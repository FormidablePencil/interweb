package com.idealIntent.dtos.compositions.texts

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import kotlinx.serialization.Serializable

@Serializable
data class CreateTextBioReq(
    val name: String,
    val texts: List<Text>, // todo - not enough. Title, description, etc
    val privilegedAuthors: List<PrivilegedAuthor>,
)