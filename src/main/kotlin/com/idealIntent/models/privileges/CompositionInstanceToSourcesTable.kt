package com.idealIntent.models.privileges

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

/**
 * @property compositionType Is used to query the correct compositions.
 */
interface ICompositionInstanceToSource {
    val compositionId: Int
    val sourceId: Int
    val compositionCategory: Int
    val compositionType: Int
}

interface ICompositionInstanceToSourceEntity : Entity<ICompositionInstanceToSourceEntity>, ICompositionInstanceToSource {
    companion object : Entity.Factory<ICompositionInstanceToSourceEntity>()
}

open class CompositionInstanceToSourcesTable(alias: String?) :
    Table<ICompositionInstanceToSourceEntity>("composition_to_privileges", alias) {
    companion object : CompositionInstanceToSourcesTable(null)

    override fun aliased(alias: String) = CompositionInstanceToSourcesTable(alias)

    val compositionId = int("composition_id").bindTo { it.compositionId }
    val sourceId = int("source_id").bindTo { it.sourceId }
    val compositionCategory = int("composition_category").bindTo { it.compositionCategory }
    val compositionType = int("composition_type").bindTo { it.compositionType }
}
