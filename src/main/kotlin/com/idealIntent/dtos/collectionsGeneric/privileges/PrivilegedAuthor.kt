package com.idealIntent.dtos.collectionsGeneric.privileges

import kotlinx.serialization.Serializable
import models.privileges.IPrivilegedAuthor

@Serializable
data class PrivilegedAuthor(
    override val username: String,
    override val modify: Int,
    override val view: Int,
) : IPrivilegedAuthor