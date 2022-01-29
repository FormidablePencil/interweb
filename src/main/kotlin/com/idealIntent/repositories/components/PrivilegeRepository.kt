package com.idealIntent.repositories.components

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
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.serialized.libOfComps.RecordUpdate

class PrivilegeRepository : RepositoryBase() {
    private val Database.privileges get() = this.sequenceOf(Privileges)

    // todo - creates new collection. rename insertNewPrivilegedAuthor and insertNewPrivilegedAuthor(privilegeId: Int). Do the same for CarouselRepo, ImageRepository, TextRepository
    fun insertNewPrivilegedAuthor(privilegedAuthor: PrivilegedAuthor, privilegesTo: String) {
        val privilegeId = insertPrivileges(privilegesTo)
            ?: TODO("handle gracefully")

        val id = insertPrivilegedAuthor(privilegedAuthor, privilegeId)
        return
    }

    fun batchInsertNewPrivilegedAuthors(privilegedAuthors: List<PrivilegedAuthor>, privilegesTo: String): Int? {
        val privilegeId = insertPrivileges(privilegesTo)
            ?: TODO("handle gracefully")

        val ids = batchInsertPrivilegedAuthors(privilegedAuthors, privilegeId)
            ?: TODO("handle gracefully. Insert all or insert non")
        return privilegeId
    }

    fun insertPrivilegedAuthor(privilegedAuthor: PrivilegedAuthor, privilegeId: Int): Int {
        val privilegedAuthorsId = database.insert(PrivilegedAuthors) { // todo - test that all have been generated
            set(it.privilegeId, privilegeId)
            set(it.authorId, privilegedAuthor.authorId)
            set(it.modLvl, privilegedAuthor.modLvl)
        }
        return privilegeId
    }

    fun batchInsertPrivilegedAuthors(privilegedAuthors: List<PrivilegedAuthor>, privilegeId: Int): IntArray {
        return database.batchInsert(PrivilegedAuthors) { // todo - test that all have been generated
            privilegedAuthors.map { privilegedAuthor ->
                item {
                    set(it.privilegeId, privilegeId)
                    set(it.authorId, privilegedAuthor.authorId)
                    set(it.modLvl, privilegedAuthor.modLvl)
                }
            }
        }
    }

    private fun insertPrivileges(privilegesTo: String): Int? {
        return database.insertAndGenerateKey(Privileges) {
            set(it.privilegesTo, privilegesTo)
        } as Int?
    }

    fun getAssortmentById(id: Int): Privilege {
        val privCol = Privileges.aliased("privCol")
        val priv = PrivilegedAuthors.aliased("priv")

        var privilegesTo = ""
        val privilegedAuthors = database.from(privCol)
            .rightJoin(priv, priv.privilegeId eq privCol.id)
            .select(privCol.privilegesTo, priv.modLvl, priv.authorId)
            .where { privCol.id eq id }
            .map { row ->
                privilegesTo = row[privCol.privilegesTo]!!
                PrivilegedAuthor(
                    authorId = row[priv.authorId]!!,
                    modLvl = row[priv.modLvl]!! // todo - might fail. error handling
                )
            }
        return Privilege(privilegesTo, privilegedAuthors)
    }

    fun updatePrivilegedAuthor(collectionId: Int, record: RecordUpdate) {
        val collection = getPrivilege(collectionId) ?: return // todo - handle failure gracefully

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

    fun batchUpdatePrivilegedAuthors(collectionId: Int, records: List<RecordUpdate>) {
        val collection = getPrivilege(collectionId) ?: return // todo - handle failure gracefully

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

    private fun getPrivilege(id: Int): IPrivilegeSchema? {
        return database.privileges.find { it.id eq id }
    }
}
