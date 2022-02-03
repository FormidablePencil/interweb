package com.idealIntent.repositories.compositions

interface ICompositionRepoStructure<Composition, CompositionMetadata, CreateComposition, ComposePrepared> {
    /**
     * Get composition.
     *
     * @param id Id of composition to get by.
     * @return All records of composition.
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