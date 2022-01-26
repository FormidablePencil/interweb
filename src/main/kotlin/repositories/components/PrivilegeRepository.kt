package repositories.components

import dtos.libOfComps.genericStructures.IPrivilegedAuthor
import models.genericStructures.PrivilegedAuthors
import models.genericStructures.Privileges
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.sequenceOf
import repositories.RepositoryBase

class PrivilegeRepository : RepositoryBase() {
    private val Database.privileges get() = this.sequenceOf(Privileges)

    fun insertPrivileges(privilegedAuthors: List<IPrivilegedAuthor>, privilegesTo: String): Int? {
        val privilegeId = database.insertAndGenerateKey(Privileges) {
            set(it.privilegesTo, privilegesTo)
        } as Int?

        val privilegedAuthorsIds = database.batchInsert(PrivilegedAuthors) {
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

    fun givePrivilege() {

    }
}