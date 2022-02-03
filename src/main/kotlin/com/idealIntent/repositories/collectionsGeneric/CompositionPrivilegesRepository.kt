package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthorsToComposition
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.models.privileges.IPrivilegedAuthorsToCompositionEntity
import com.idealIntent.models.privileges.PrivilegeSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorsToCompositionsModel
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.profile.AuthorRepository
import models.privileges.ICompositionsGenericPrivileges
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

data class PrivilegeRecord(val id: Int)

data class CompositionsGenericPrivileges(
    override val modify: Int,
    override val view: Int,
) : ICompositionsGenericPrivileges

// todo privilege and privileges are used interchangeably. Fix this typo
class CompositionPrivilegesRepository(
    private val authorRepository: AuthorRepository,
) : RepositoryBase() {
    private val Database.privilegeSource get() = this.sequenceOf(PrivilegeSourcesModel)
    private val Database.privilegedAuthorsToCompositions get() = this.sequenceOf(PrivilegedAuthorsToCompositionsModel)

    // region Get
    fun getPrivilegesByAuthorId(getPrivilegesOfAuthorId: Int): Pair<List<PrivilegeRecord>, Int> {
        TODO()
//        val privCol = Privileges.aliased("privCol")
//        val priv = PrivilegedAuthors.aliased("priv")
//
//        var privilegesTo = ""
//        val privilegedAuthors = database.from(privCol)
//            .rightJoin(priv, priv.privilegeId eq privCol.id)
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

    fun getRecordToCollectionInfo(recordId: Int, collectionId: Int): IPrivilegedAuthorsToCompositionEntity? {
        TODO()
    }

    // todo check if privileged and what level...
    fun checkIfPrivileged(authorId: Int, privilegeId: Int) =
        database.privilegedAuthorsToCompositions.find { (it.privilegeId eq privilegeId) and (it.authorId eq authorId) }
    // endregion Get


    // region Insert
    /**
     * Give multiple authors privileges by username
     *
     * @return Pair(Truthy for success or falsy for failure, username that failed to find)
     */
    fun giveMultipleAuthorsPrivilegesByUsername(
        privilegedAuthors: List<PrivilegedAuthor>, privilegeId: Int
    ): Pair<Boolean, String?> = database.useTransaction { transaction ->
        privilegedAuthors.forEach {
            if (giveAnAuthorPrivilegesByUsername(it, privilegeId)) {
                transaction.rollback()
                return Pair(false, it.username)
            }
        }
        return Pair(true, null)
    }

    /**
     * Give an author privileges by username
     * @return Truthy for success or falsy for failure.
     */
    fun giveAnAuthorPrivilegesByUsername(privilegedAuthor: PrivilegedAuthor, privilegeId: Int): Boolean {
        val author = authorRepository.getByUsername(privilegedAuthor.username) ?: return false

        giveAnAuthorPrivilege(
            privileges = CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
            authorId = author.id,
            privilegeId = privilegeId
        )
        return true
    }

    /**
     * Give an author privilege
     *
     * @param privileges What kind of privileges, view mod, etc.
     */
    fun giveAnAuthorPrivilege(privileges: CompositionsGenericPrivileges, authorId: Int, privilegeId: Int) {
        database.insert(PrivilegedAuthorsToCompositionsModel) {
            set(it.modify, privileges.modify)
            set(it.view, privileges.view)
            set(it.privilegeId, privilegeId)
            set(it.authorId, authorId)
        }
    }

    /**
     * Add privilege source which is a record for compositions to key off of as a source of truth for privileges,
     * making the privileges unique to every kind of compositional record that references it.
     *
     * @return Id to privilege source.
     */
    fun addPrivilegeSource(privilegeLevel: Int = 0): Int =
        database.insertAndGenerateKey(PrivilegeSourcesModel) {
            set(it.privilegeLevel, privilegeLevel)
        } as Int
// endregion Insert


    // region Update
    fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean {
        TODO()
//        val collection =
//            validateRecordToCollectionRelationship(collectionId) ?: return false // todo - handle failure gracefully
//
//        val res = database.update(PrivilegedAuthors) {
//            record.updateTo.map { updateCol ->
//                when (PrivilegedAuthorCOL.fromInt(updateCol.column)) {
//                    PrivilegedAuthorCOL.ModLvl -> set(it.modLvl, updateCol.value.toInt())
//                    PrivilegedAuthorCOL.AuthorId -> set(it.authorId, updateCol.value.toInt())
//                    // todo - remove changing of authorId
//                    // todo - toInt() may fail
//                }
//            }
//            where {
//                when (PrivilegedAuthorIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                    PrivilegedAuthorIdentifiableRecordByCol.AuthorId ->
//                        (it.privilegeId eq collection.id) and (it.authorId eq record.recordIdentifiableByColOfValue.toInt())
//                } // todo - handle incorrect recordIdentifiableByCol gracefully
//            }
//        }
    }

    fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
        TODO()
//        val collection =
//            validateRecordToCollectionRelationship(collectionId) ?: return false // todo - handle failure gracefully
//
//        database.batchUpdate(PrivilegedAuthors) {
//            records.map { record ->
//                record.updateTo.map { updateCol ->
//                    item {
//                        when (PrivilegedAuthorCOL.fromInt(updateCol.column)) {
//                            PrivilegedAuthorCOL.AuthorId -> set(it.authorId, updateCol.value.toInt())
//                            PrivilegedAuthorCOL.ModLvl -> set(it.modLvl, updateCol.value.toInt())
//                            // todo - toInt() may fail, handle gracefully
//                        }
//                        where {
//                            when (PrivilegedAuthorIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
//                                PrivilegedAuthorIdentifiableRecordByCol.AuthorId ->
//                                    (it.privilegeId eq collection.id) and (it.authorId eq record.recordIdentifiableByColOfValue.toInt())
//                            } // todo - handle incorrect recordIdentifiableByCol gracefully
//                        }
//                    }
//                }
//            }
//        }
    }
// endregion Update


    // region Delete
    fun deleteRecord(recordId: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    fun batchDeleteRecords(id: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    fun deleteAllRecordsInCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    fun deleteCollectionButNotRecord() {
        TODO("Not yet implemented")
    }

    fun batchCreateRecordToCollectionRelationship(
        records: List<PrivilegeRecord>,
        collectionId: Int,
    ): Boolean {
        TODO("Not yet implemented")
    }

    fun createRecordToCollectionRelationship(recordToCollection: PrivilegedAuthorsToComposition): Boolean {
        TODO("Not yet implemented")
    }

    fun getRecordOfCollection(recordId: Int, collectionId: Int): PrivilegeRecord? {
        TODO("Not yet implemented")
    }

    fun getRecordsQuery(recordId: Int?, collectionId: Int): List<PrivilegeRecord> {
        TODO("Not yet implemented")
    }
// endregion Delete
}
