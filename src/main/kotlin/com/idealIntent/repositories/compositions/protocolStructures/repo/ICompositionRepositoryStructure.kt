package com.idealIntent.repositories.compositions.protocolStructures.repo

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionCode.CollectionOfRecordsNotFound
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.models.privileges.CompositionInstanceToSourcesModel
import org.ktorm.database.Database
import org.ktorm.dsl.*

interface ICompositionRepositoryStructure<ResponseOfComposition, CompositionMetadata, ComposedPrepared,
        CreateComposition, DtoMapper> {
    val database: Database

    /**
     * Map clause builder carousel of images.
     *
     * Used in [SpaceRepository][com.idealIntent.repositories.compositions.SpaceRepository] to query layouts of compositions.
     *
     * @param row Table row.
     * @param dto To save records under.
     */
    fun compositionQueryMap(row: QueryRowSet, dto: DtoMapper)


    // region Get compositions
    /**
     * Get public composition.
     *
     * @param compositionSourceId Id of composition source.
     * @return Composition
     */
    fun getPublicComposition(compositionSourceId: Int): ResponseOfComposition?

    /**
     * Get private composition.
     *
     * @param compositionSourceId Id of composition source.
     * @param authorId Validate that author is privileged to view composition.
     * @return Composition
     */
    fun getPrivateComposition(compositionSourceId: Int, authorId: Int): ResponseOfComposition?
    // endregion


    /**
     * Composes composition by saving ids of collections and compositions under one composition record.
     * Then associating newly created composition to a composition source which keeps the uniqueness of id.
     *
     * Think of privilege source as a door with a lock required to get through
     * to get your data.
     *
     * @param composePrepared All collection and compositions of ids to compose as one composition.
     * @return Id of newly created composition or throw.
     */
    fun compose(composePrepared: ComposedPrepared, sourceId: Int): Int


    /**
     * Associate composition to source. Used only by [compose].
     */
    fun associateCompToSource(compositionCategory: Int, compositionType: Int, compositionId: Int, sourceId: Int) {
        if (database.insert(CompositionInstanceToSourcesModel) {
                set(it.compositionCategory, compositionCategory)
                set(it.compositionType, compositionType)
                set(it.sourceId, sourceId)
                set(it.compositionId, compositionId)
            } == 0) throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)
    }

    /**
     * Delete composition.
     *
     * Invokes [getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify][IComplexCompositionRepositoryStructure.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify]
     * to query all the ids of collection and compositions
     * composed of composition requested to delete then calls delete methods of its respective repositories to
     * delete each collection and composition composed of composition requested to delete.
     *
     * This method handles one exception which is [when collection by id of composition was not found][CollectionOfRecordsNotFound].
     * This could only mean that composition is corrupt because the composition is composed of compositions
     * and collections that do exist.
     *
     * @param compositionSourceId Id of composition source and not id of composition itself.
     * @param authorId Author's id to validate if privileged to delete.
     * @return success or fail.
     * @throws CompositionException [ CompositionNotFound][CompositionCode.CompositionNotFound].
     */
    fun deleteComposition(compositionSourceId: Int, authorId: Int)
}