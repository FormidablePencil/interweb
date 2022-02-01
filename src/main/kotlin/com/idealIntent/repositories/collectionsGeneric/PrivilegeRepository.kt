package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.AuthorToPrivilege
import com.idealIntent.dtos.collectionsGeneric.privileges.Privilege
import com.idealIntent.dtos.compositionCRUD.RecordUpdate
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collections.ICollectionStructure
import models.privileges.AuthorToPrivilegesModel
import models.privileges.IAuthorToPrivilegeEntity
import models.privileges.PrivilegesModel
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

data class PrivilegeRecord(val id: Int)

// todo privilege and privileges are used interchangeably. Fix this typo
class PrivilegeRepository : RepositoryBase(),
    ICollectionStructure<PrivilegeRecord, IAuthorToPrivilegeEntity, AuthorToPrivilege, Privilege> {
    private val Database.privileges get() = this.sequenceOf(PrivilegesModel)
    private val Database.authorToPrivileges get() = this.sequenceOf(AuthorToPrivilegesModel)

    // region Get
    override fun getCollectionOfRecords(collectionId: Int): Privilege {
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
//        return AuthorToPrivilege(privilegesTo, privilegedAuthors)
    }

    override fun getRecordToCollectionInfo(recordId: Int, collectionId: Int): IAuthorToPrivilegeEntity? {
        TODO()
    }
    // endregion Get


    // region Insert
    override fun insertRecord(record: PrivilegeRecord): PrivilegeRecord? {
        TODO()
//        return database.insert(PrivilegedAuthors) { // todo - test that all have been generated
//            set(it.privilegeId, collectionId)
//            set(it.authorId, record.authorId)
//            set(it.modLvl, record.modLvl)
//        } != 0
    }

    override fun batchInsertRecords(records: List<PrivilegeRecord>): List<PrivilegeRecord> {
        TODO()
//        database.batchInsert(PrivilegedAuthors) { // todo - test that all have been generated
//            records.map { privilegedAuthor ->
//                item {
//                    set(it.privilegeId, collectionId)
//                    set(it.authorId, privilegedAuthor.authorId)
//                    set(it.modLvl, privilegedAuthor.modLvl)
//                }
//            }
//        }
    }

    override fun addRecordCollection(): Int {
        TODO()
//        return database.insertAndGenerateKey(Privileges) {
//            set(it.privilegesTo, privilegesTo)
//        } as Int? ?: TODO("shouldn't ever fail, so throw a server error exception")
    }
    // endregion Insert


    // region Update
    override fun updateRecord(record: RecordUpdate, imageId: Int, collectionId: Int): Boolean {
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

    override fun batchUpdateRecords(records: List<RecordUpdate>, collectionId: Int): Boolean {
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
    override fun deleteRecord(recordId: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun batchDeleteRecords(id: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun disassociateRecordFromCollection(recordId: Int, collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionButNotRecord() {
        TODO("Not yet implemented")
    }

    override fun batchCreateRecordToCollectionRelationship(images: List<Image>, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun createRecordToCollectionRelationship(recordToCollection: AuthorToPrivilege): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRecordOfCollection(recordId: Int, collectionId: Int): PrivilegeRecord? {
        TODO("Not yet implemented")
    }

    override fun getRecordsQuery(recordId: Int?, collectionId: Int): List<PrivilegeRecord> {
        TODO("Not yet implemented")
    }
    // endregion Delete
}
