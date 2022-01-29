package com.idealIntent.repositories.components

import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.dtos.libOfComps.RecordUpdate
import dtos.libOfComps.genericStructures.privileges.Privilege
import dtos.libOfComps.genericStructures.privileges.PrivilegedAuthor
import dtos.libOfComps.genericStructures.privileges.PrivilegedAuthorCOL
import dtos.libOfComps.genericStructures.privileges.PrivilegedAuthorIdentifiableRecordByCol
import models.genericStructures.IPrivilegeSchema
import models.genericStructures.PrivilegedAuthors
import models.genericStructures.Privileges
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// todo privilege and privileges are used interchangeably. Fix this typo
class PrivilegeRepository : RepositoryBase(), ICompRecordCrudStructure<PrivilegedAuthor, IPrivilegeSchema, Privilege> {
    private val Database.privileges get() = this.sequenceOf(Privileges)

    // region Get
    override fun getAssortmentById(collectionId: Int): Privilege {
        val privCol = Privileges.aliased("privCol")
        val priv = PrivilegedAuthors.aliased("priv")

        var privilegesTo = ""
        val privilegedAuthors = database.from(privCol)
            .rightJoin(priv, priv.privilegeId eq privCol.id)
            .select(privCol.privilegesTo, priv.modLvl, priv.authorId)
            .where { privCol.id eq collectionId }
            .map { row ->
                privilegesTo = row[privCol.privilegesTo]!!
                PrivilegedAuthor(
                    authorId = row[priv.authorId]!!,
                    modLvl = row[priv.modLvl]!! // todo - might fail. error handling
                )
            }
        return Privilege(privilegesTo, privilegedAuthors)
    }

    override fun getCollection(id: Int): IPrivilegeSchema? {
        return database.privileges.find { it.id eq id }
    }
    // endregion Get


    // region Insert
    // todo - creates new collection. rename insertNewRecord and insertNewRecord(privilegeId: Int). Do the same for CarouselRepo, ImageRepository, TextRepository
    override fun insertNewRecord(record: PrivilegedAuthor, collectionOf: String): Int? {
        val privilegeId = insertRecordCollection(collectionOf)
            ?: TODO("handle gracefully")

        val id = insertRecord(record, privilegeId)
        return
    }

    override fun batchInsertNewRecords(records: List<PrivilegedAuthor>, privilegesTo: String): Int? {
        val privilegeId = insertRecordCollection(privilegesTo)
            ?: TODO("handle gracefully")

        val ids = batchInsertRecords(records, privilegeId)
            ?: TODO("handle gracefully. Insert all or insert non")
        return privilegeId
    }

    override fun insertRecord(record: PrivilegedAuthor, collectionId: Int): Boolean {
        return database.insert(PrivilegedAuthors) { // todo - test that all have been generated
            set(it.privilegeId, collectionId)
            set(it.authorId, record.authorId)
            set(it.modLvl, record.modLvl)
        } != 0
    }

    override fun batchInsertRecords(records: List<PrivilegedAuthor>, collectionId: Int): Boolean {
        return database.batchInsert(PrivilegedAuthors) { // todo - test that all have been generated
            records.map { privilegedAuthor ->
                item {
                    set(it.privilegeId, collectionId)
                    set(it.authorId, privilegedAuthor.authorId)
                    set(it.modLvl, privilegedAuthor.modLvl)
                }
            }
        }
    }

    override fun insertRecordCollection(privilegesTo: String): Int {
        return database.insertAndGenerateKey(Privileges) {
            set(it.privilegesTo, privilegesTo)
        } as Int? ?: TODO("shouldn't ever fail, so throw a server error exception")
    }
    // endregion Insert


    // region Update
    override fun updateRecord(collectionId: Int, record: RecordUpdate): Boolean {
        val collection = getCollection(collectionId) ?: return // todo - handle failure gracefully

        val res = database.update(PrivilegedAuthors) {
            record.updateTo.map { updateCol ->
                when (PrivilegedAuthorCOL.fromInt(updateCol.column)) {
                    PrivilegedAuthorCOL.ModLvl -> set(it.modLvl, updateCol.value.toInt())
                    PrivilegedAuthorCOL.AuthorId -> set(it.authorId, updateCol.value.toInt())
                    // todo - remove changing of authorId
                    // todo - toInt() may fail
                }
            }
            where {
                when (PrivilegedAuthorIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                    PrivilegedAuthorIdentifiableRecordByCol.AuthorId ->
                        (it.privilegeId eq collection.id) and (it.authorId eq record.recordIdentifiableByColOfValue.toInt())
                } // todo - handle incorrect recordIdentifiableByCol gracefully
            }
        }
    }

    override fun batchUpdateRecords(collectionId: Int, records: List<RecordUpdate>): Boolean {
        val collection = getCollection(collectionId) ?: return // todo - handle failure gracefully

        database.batchUpdate(PrivilegedAuthors) {
            records.map { record ->
                record.updateTo.map { updateCol ->
                    item {
                        when (PrivilegedAuthorCOL.fromInt(updateCol.column)) {
                            PrivilegedAuthorCOL.AuthorId -> set(it.authorId, updateCol.value.toInt())
                            PrivilegedAuthorCOL.ModLvl -> set(it.modLvl, updateCol.value.toInt())
                            // todo - toInt() may fail, handle gracefully
                        }
                        where {
                            when (PrivilegedAuthorIdentifiableRecordByCol.fromInt(record.recordIdentifiableByCol)) {
                                PrivilegedAuthorIdentifiableRecordByCol.AuthorId ->
                                    (it.privilegeId eq collection.id) and (it.authorId eq record.recordIdentifiableByColOfValue.toInt())
                            } // todo - handle incorrect recordIdentifiableByCol gracefully
                        }
                    }
                }
            }
        }
    }
    // endregion Update


    // region Delete
    override fun deleteRecord(collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun batchDeleteRecords(collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteAllRecordsInCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteCollectionOfRecords() {
        TODO("Not yet implemented")
    }
    // region Delete
}
