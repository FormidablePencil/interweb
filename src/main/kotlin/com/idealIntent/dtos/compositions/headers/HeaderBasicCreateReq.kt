package com.idealIntent.dtos.compositions.headers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import dtos.compositions.headers.IHeaderProfile
import models.IWithPK
import models.IWithPrivilegeLevel
import models.IWithPrivilegeSourcePK

data class HeaderBasicCreateReq(
    override val bgImg: String,
    override val profileImg: String,
    override val privilegeLevel: Int,
    override val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
) : IHeaderProfile, IWithPrivilegeLevel

data class HeaderBasicRes(
    override val id: Int,
    override val sourceId: Int,
    override val bgImg: String,
    override val profileImg: String,
    override val privilegeLevel: Int,
    override val name: String,
    val privilegedAuthors: List<PrivilegedAuthor>,
) : IHeaderProfile, IWithPrivilegeLevel, IWithPK, IWithPrivilegeSourcePK

data class HeaderBasicTopLvlIds(
    val sourceId: Int,
    val id: Int,
    val name: String,
)