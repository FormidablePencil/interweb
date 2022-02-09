package com.idealIntent.managers.compositions.carousels

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import kotlinx.serialization.Serializable

/**
 * Update data of composition
 *
 * @property updateDataOf Enum of the composition to update such as [images][models.compositions.basicsCollections.images.IImage].
 * @property recordUpdate Update the records to.
 */
@Serializable
data class UpdateDataOfComposition(
    val updateDataOf: Int,
    val recordUpdate: RecordUpdate,
)