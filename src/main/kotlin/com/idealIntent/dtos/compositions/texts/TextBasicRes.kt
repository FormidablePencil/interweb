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

/**
 * Text basic response data.
 *
 * @property id Id of composition.
 * @property sourceId Id of the source of the composition.
 */
@Serializable
data class TextBasicRes(
    val id: Int,
    val sourceId: Int,
    val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
)