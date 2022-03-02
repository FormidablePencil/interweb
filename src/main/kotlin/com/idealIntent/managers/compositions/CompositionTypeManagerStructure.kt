package com.idealIntent.managers.compositions

import com.google.gson.Gson
import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import com.idealIntent.repositories.compositions.protocolStructures.repo.ComplexCompositionRepositoryStructure
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class CompositionTypeManagerStructure<Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response>
    : ICompositionTypeManagerStructure<Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response>,
    KoinComponent {
    val gson = Gson()
    val appEnv: AppEnv by inject()
}

/**
 * Directs CRUD operations of composition types.
 *
 * While [composition category manager][ICompositionCategoryManagerStructure] directs actions of the category of
 * compositions, [composition type manager][ICompositionTypeManagerStructure] directs actions of the type of category,
 *
 * @param Composition Records of composition.
 * @param CompositionMetadata Composition information, ids, ect.
 * @param CreateRequest Ids of compositions and collections to compose and raw data to save before composition.
 * @param ComposePrepared Ids of composition and collections created beforehand to compose into one composition.
 */
private interface ICompositionTypeManagerStructure<Composition, CompositionMetadata, CreateRequest, ComposePrepared, Response> {
    /**
     * Get public composition. Only can get composition by its source id.
     *
     * @param compositionSourceId Id of composition's source.
     * @return Composition, including all its related records.
     * @see ICompositionTypeManagerStructure.getPublicComposition
     */
    fun getPublicComposition(compositionSourceId: Int): Composition?

    /**
     * Get private composition. Only for the author of [authorId] that's privileged to private composition will it return composition.
     *
     * @param compositionSourceId Id of composition's source.
     * @param authorId Id of author that is privileged.
     * @return Composition, including all its related records.
     * @see ICompositionTypeManagerStructure.getPrivateComposition
     */
    fun getPrivateComposition(compositionSourceId: Int, authorId: Int): Composition?

    /**
     * Firstly creates composition's collections and compositions (side note - some compositions are nested in one another),
     * inserts the records and creates a record to collection relationship.
     * Then creates privileges for the composition and assigns them to author by specified authorIds their privileges.
     * Then takes the collection and composition ids and sends them off to compose. If everything went
     * well the user will get the id of the newly composed composition.
     *
     * todo - delete
     * Insert images and redirection texts, create a collection for each, create an association between imageUrl and
     * assign privileges compositions to specified authors. If either looking up author by id or assigning privileges to
     * authors fails then return a response a fail response to client with the author's username that failed.
     * Otherwise, if all went well, pass ids of imageUrl's and redirection text's collections to
     * [compose][CarouselOfImagesRepository.compose]. Then return id of the newly created composition.
     *
     * @param createRequest Composition of records.
     * @param authorId Id of user to validate that they are privileged.
     * @return Id of the newly created composition.
     * @throws CompositionException [FailedToFindAuthorByUsername].
     * @see ICompositionTypeManagerStructure.createComposition
     */
    fun createComposition(
        createRequest: CreateRequest,
        layoutId: Int,
        authorId: Int
    ): Int

    /**
     * Update composition
     *
     * First validate that the author is privileged to update a composition of source id provided. Then transforms provided
     * value from json to a type object corresponding to collection of records. Validate that the id of
     * item to update to is of the id of the composition source provided. Then update property of composition.
     *
     * @param compositionUpdateQue Update que. Gives you what column to update and to what value.
     * @param authorId Id of author to get only the composition they are privileged to update.
     * @throws CompositionException [ModifyPermittedToAuthorOfCompositionNotFound], [IdOfRecordProvidedNotOfComposition], [ProvidedStringInPlaceOfInt].
     */
    fun updateComposition(
        compositionUpdateQue: List<UpdateDataOfComposition>,
        compositionSourceId: Int,
        authorId: Int
    )

    /**
     * Delete composition and records it is composed of.
     * @throws CompositionException [CompositionNotFound]
     * @see ComplexCompositionRepositoryStructure.deleteComposition
     */
    fun deleteComposition(compositionSourceId: Int, authorId: Int)
}