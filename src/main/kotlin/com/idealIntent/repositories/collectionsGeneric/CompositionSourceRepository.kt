package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.models.privileges.CompositionSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorToCompositionSourcesModel
import com.idealIntent.repositories.RepositoryBase
import dtos.collectionsGeneric.privileges.PrivilegedAuthorCOL
import models.privileges.ICompositionsGenericPrivileges
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

data class PrivilegeRecord(val id: Int)

data class CompositionsGenericPrivileges(
    override val modify: Int,
    override val deletion: Int,
    override val modifyUserPrivileges: Int,
) : ICompositionsGenericPrivileges

// todo - compositionSource
class CompositionSourceRepository : RepositoryBase() {
    private val Database.compositionSource get() = this.sequenceOf(CompositionSourcesModel)
    private val Database.privilegedAuthorsToCompositions
        get() = this.sequenceOf(PrivilegedAuthorToCompositionSourcesModel)

    // region Privileges

    /**
     * Get author's privileges.
     */
    fun getPrivilegesOfAuthor(getPrivilegesOfAuthorId: Int): Pair<List<PrivilegeRecord>, Int> {
        TODO("implement when there's a need for this")
//        val privCol = Privileges.aliased("privCol")
//        val priv = PrivilegedAuthors.aliased("priv")
//
//        var privilegesTo = ""
//        val privilegedAuthors = database.from(privCol)
//            .rightJoin(priv, priv.sourceId eq privCol.id)
//            .select(privCol.privilegesTo, priv.modLvl, priv.authorId)
//            .where { privCol.id eq id }
//            .map { row ->
//                privilegesTo = row[privCol.privilegesTo]!!
//                PrivilegedAuthor(
//                    authorId = row[priv.authorId]!!,
//                    modLvl = row[priv.modLvl]!! // todo - might fail. error handling
//                )
//            }
//        return PrivilegedAuthorsToComposition(privilegesTo, privilegedAuthors)
    }

    /**
     * Validate if user is privileged to modify a composition.
     */
    fun isUserPrivilegedToModifyComposition(compositionSourceId: Int, authorId: Int) =
        database.privilegedAuthorsToCompositions.find {
            (it.sourceId eq compositionSourceId) and (it.authorId eq authorId) and (it.modify eq 1)
        } != null

    /**
     * Validate if user permitted to update.
     *
     * @return success or failure.
     */
    fun isUserPermittedToUpdatePrivileges(authorId: Int, compositionSourceId: Int): Boolean =
        database.privilegedAuthorsToCompositions.find {
            it.authorId eq authorId and (it.sourceId eq compositionSourceId) and (it.modifyUserPrivileges eq 1)
        } != null

    /**
     * Give an author privilege.
     *
     * Used in conjunction with [isUserPermittedToUpdatePrivileges] to validate user is permitted to give
     * privileges to others.
     *
     * @param privileges What kind of privileges, view mod, etc.
     */
    fun giveAnAuthorPrivilegeToComposition(
        privileges: CompositionsGenericPrivileges, compositionSourceId: Int, authorId: Int
    ) = database.insert(PrivilegedAuthorToCompositionSourcesModel) {
        set(it.modify, privileges.modify)
        set(it.deletion, privileges.deletion)
        set(it.sourceId, compositionSourceId)
        set(it.modifyUserPrivileges, privileges.modifyUserPrivileges)
        set(it.authorId, authorId)
    } == 1

    /**
     * Remove author from all privileges.
     */
    fun removeAuthorFromAllPrivileges(authorId: Int) =
        database.delete(PrivilegedAuthorToCompositionSourcesModel) { it.authorId eq authorId }

    /**
     * Update privileges of authors.
     *
     * Update author's privileges of modify, deletion and whether author may update privileges of other users.
     *
     * Used to conjunction with [isUserPermittedToUpdatePrivileges] to validate if user is permitted.
     *
     * @param record Rows and columns to update and value to update to.
     * @param authorIdToUpdatePrivilegesOf Id of author to update on.
     * @param compositionSourceId Id of composition's source.
     * @return success or failure.
     */
    fun updatePrivilegesOfAuthors(
        record: RecordUpdate,
        authorIdToUpdatePrivilegesOf: Int,
        compositionSourceId: Int,
    ): Boolean = database.update(PrivilegedAuthorToCompositionSourcesModel) {
        record.updateTo.map { updateCol ->
            when (PrivilegedAuthorCOL.fromInt(updateCol.column)) {
                PrivilegedAuthorCOL.Modify -> set(it.modify, updateCol.value.toInt())
                PrivilegedAuthorCOL.Deletion -> set(it.deletion, updateCol.value.toInt()) // todo - toInt() may fail
            }
        }
        where { it.authorId eq authorIdToUpdatePrivilegesOf and (it.sourceId eq compositionSourceId) }
    } != 0
    // endregion

    /**
     * Add privilege source which is a record for compositions to key off of as a source of truth for privileges,
     * making the privileges unique to every kind of compositional record that references it.
     *
     * For now, a [privilegeLevel] of 0 is public and [privilegeLevel] of 1 is private.
     *
     * @param privilegeLevel level of privileges such as whether it is a viewable for everyone or private.
     * @return Id to privilege source.
     */
    fun addCompositionSource(privilegeLevel: Int = 0, name: String, compositionType: Int): Int =
        database.insertAndGenerateKey(CompositionSourcesModel) {
            set(it.name, name)
            set(it.compositionType, compositionType)
            set(it.privilegeLevel, privilegeLevel)
        } as Int

    /**
     * Change name of composition.
     */
    fun renameComposition(name: String) =
        database.update(CompositionSourcesModel) { set(it.name, name) } == 1
}
