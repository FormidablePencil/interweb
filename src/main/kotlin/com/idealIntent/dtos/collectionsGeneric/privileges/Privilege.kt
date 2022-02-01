package com.idealIntent.dtos.collectionsGeneric.privileges

import kotlinx.serialization.Serializable

@Serializable
data class Privilege(val privilegeTo: String, val privilegedAuthors: List<AuthorToPrivilege>)