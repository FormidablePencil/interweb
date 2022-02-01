package com.idealIntent.dtos.compositionCRUD

import dtos.compositions.IRecordUpdate
import kotlinx.serialization.Serializable

@Serializable
data class RecordUpdate(
    override val recordId: Int,
    override val recordIdentifiableByCol: Int, // maps to enums
    override val recordIdentifiableByColOfValue: String,
    override val updateTo: List<UpdateColumn>,
): IRecordUpdate