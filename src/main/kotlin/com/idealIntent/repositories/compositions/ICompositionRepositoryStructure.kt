package com.idealIntent.repositories.compositions

import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes

interface ICompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, CreateComposition, ComposePrepared> {
    /**
     * Get composition by its id and privileged author.
     *
     * @param compositionId id of composition.
     * @param authorId Id of author privileged to view.
     * @return Records of composition. Returns null if not found or author not privileged to view.
     */
    fun getSingleCompositionOfPrivilegedAuthor(compositionId: Int, authorId: Int): List<CarouselBasicImagesRes>

    fun getAllCompositionsAssociatedOfAuthor(authorId: Int): List<CarouselBasicImagesRes>

    // region Get
    /**
     * Get only composition's metadata and not it's associated records
     *
     * @param id ID of composition
     * @return Composition's metadata and not associated records or null if failed to find by [id]
     */
    fun getMetadataOfComposition(id: Int): CompositionMetadata?
    // endregion Get


    // region Insert
    /**
     * Take ids of collections, compositions and privilege source and insert them into composition's foreign key columns.
     *
     * @return Id of the newly created composition.
     */
    fun compose(composePrepared: ComposePrepared): Int?
    // endregion Insert

    // region Delete
    fun deleteComposition(id: Int): Boolean
    // endregion Delete
}