package repositories.components

import dtos.libOfComps.genericStructures.privileges.*
import models.genericStructures.IPrivilegeSchema
import models.genericStructures.PrivilegedAuthors
import models.genericStructures.Privileges
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase
import serialized.libOfComps.RecordUpdate

class PrivilegeRepository : RepositoryBase() {
    private val Database.privileges get() = this.sequenceOf(Privileges)

    // todo - creates new collection. rename insertNewPrivilege and insertPrivilege(privilegeId: Int). Do the same for CarouselRepo, ImageRepository, TextRepository
    fun insertPrivilege(privilegedAuthor: PrivilegedAuthor, privilegesTo: String): Int? {
        val privilegeId = database.insertAndGenerateKey(Privileges) {
            set(it.privilegesTo, privilegesTo)
        } as Int?

        val privilegedAuthorsId = database.insert(PrivilegedAuthors) { // todo - test that all have been generated
            set(it.privilegeId, privilegeId)
            set(it.authorId, privilegedAuthor.authorId)
            set(it.modLvl, privilegedAuthor.modLvl)
        }
        return privilegeId
    }

    fun batchInsertPrivileges(privilegedAuthors: List<PrivilegedAuthor>, privilegesTo: String): Int? {
        val privilegeId = database.insertAndGenerateKey(Privileges) {
            set(it.privilegesTo, privilegesTo)
        } as Int?

        val privilegedAuthorsIds = database.batchInsert(PrivilegedAuthors) { // todo - test that all have been generated
            privilegedAuthors.map { privilegedAuthor ->
                item {
                    set(it.privilegeId, privilegeId)
                    set(it.authorId, privilegedAuthor.authorId)
                    set(it.modLvl, privilegedAuthor.modLvl)
                }
            }
        }
        return privilegeId
    }

//    fun insertPrivilege

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
            record.updateRecord.map { updateCol ->
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
                record.updateRecord.map { updateCol ->
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
