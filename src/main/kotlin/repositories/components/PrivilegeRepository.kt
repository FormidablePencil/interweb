package repositories.components

import dtos.libOfComps.genericStructures.IPrivilegedAuthor
import dtos.libOfComps.genericStructures.Privilege
import dtos.libOfComps.genericStructures.PrivilegedAuthor
import models.genericStructures.PrivilegedAuthors
import models.genericStructures.Privileges
import org.ktorm.database.Database
import org.ktorm.dsl.*
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

    fun getPrivilegeById(id: Int): Privilege {
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
}