package com.idealIntent.dtos.compositionCRUD

import dtos.space.IUserComposition
import kotlinx.serialization.Serializable

/**
 * Create a single composition such as [carousel of images][models.compositions.carousels.ImagesCarousels]
 * or [one off grid][models.compositions.grids.Grids].
 *
 * @property layoutId [Id][models.compositions.CompositionLayouts.id] of the [layout][models.compositions.CompositionLayouts] to save under.
 * @property userComposition Id of the composition such as [carousel of images][models.compositions.carousels.ImagesCarousels]
 * or [one off grid][models.compositions.grids.Grids].
 * @constructor Create empty Create composition request
 */
@Serializable
data class CreateCompositionRequest(
    val layoutId: String, // todo - change to layout id and which spaceAddress references.
    val userComposition: IUserComposition,
)
