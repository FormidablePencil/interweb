package com.idealIntent.dtos.libOfComps

import dtos.libOfComps.ComponentType
import dtos.space.IUserComponent
import kotlinx.serialization.Serializable

@Serializable
data class CreateComponentRequest(
    val spaceAddress: String,
    val userComponent: IUserComponent,
)

@Serializable
data class UserComponent(
    override val componentType: ComponentType,
    override val jsonData: String,
) : IUserComponent
