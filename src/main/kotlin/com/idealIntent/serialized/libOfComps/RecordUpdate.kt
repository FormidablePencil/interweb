package com.idealIntent.serialized.libOfComps

import dtos.libOfComps.IRecordUpdate
import kotlinx.serialization.Serializable

@Serializable
data class RecordUpdate(
    override val recordIdentifiableByCol: Int, // maps to enums
    override val recordIdentifiableByColOfValue: String,
    override val updateRecord: List<UpdateColumn>,
): IRecordUpdate