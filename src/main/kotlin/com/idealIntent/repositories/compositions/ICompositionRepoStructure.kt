package com.idealIntent.repositories.compositions

interface ICompositionRepoStructure<Composition, CompositionMetadata, CreateComposition, ComposePrepared> {
    /**
     * Get composition records by collection id
     *
     * @param id Get composition records under composition's id
     * @return Composition records under [id] or null if failed to find by [id]
     */
    fun getComposition(id: Int): Composition?

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
     * Composes compositions and collections.
     *
     * Would have been a private method if it wasn't for this interface. We opted for structure.
     *
     * @param composePrepared Ids of composition and collections to compose.
     * @return
     */
    fun compose(composePrepared: ComposePrepared): Int?
    // endregion Insert

    // region Delete
    fun deleteComposition(id: Int): Boolean
    // endregion Delete
}