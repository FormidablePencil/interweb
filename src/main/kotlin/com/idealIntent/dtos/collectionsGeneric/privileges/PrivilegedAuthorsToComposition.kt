package com.idealIntent.dtos.collectionsGeneric.privileges

import kotlinx.serialization.Serializable
import models.privileges.IPrivilegedAuthorsToComposition

// todo - rename to AuthorAndPrivileges and use PrivilegedAuthorsToComposition to carry ids
@Serializable
data class PrivilegedAuthorsToComposition(
    override val privilegeId: Int,
    override val authorId: Int,
    override val modify: Int,
    override val view: Int
) : IPrivilegedAuthorsToComposition