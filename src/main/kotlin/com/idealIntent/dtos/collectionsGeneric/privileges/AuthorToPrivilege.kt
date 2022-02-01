package com.idealIntent.dtos.collectionsGeneric.privileges

import kotlinx.serialization.Serializable
import models.privileges.IAuthorToPrivilege

// todo - rename to AuthorAndPrivileges and use AuthorToPrivilege to carry ids
@Serializable
data class AuthorToPrivilege(
    override val modLvl: Int,
    override val privilegeId: Int,
    override val authorId: Int
) : IAuthorToPrivilege