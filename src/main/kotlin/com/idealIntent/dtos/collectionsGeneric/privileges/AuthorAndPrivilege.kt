package com.idealIntent.dtos.collectionsGeneric.privileges

import kotlinx.serialization.Serializable
import models.privileges.IAuthorAndPrivilege
import models.privileges.IPrivilegeEntity
import models.profile.IAuthorEntity

@Serializable
data class AuthorAndPrivilege(
    override val modLvl: Int,
    override val privilege: IPrivilegeEntity,
    override val author: IAuthorEntity
) : IAuthorAndPrivilege
