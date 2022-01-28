package serialized.space

import repositories.components.RecordUpdate

interface IUpdateComponent {
    val componentType: Int
    val componentId: Int
    val updateToData: RecordUpdate
    val where: List<IWhereIsComponentToUpdate>
}

interface IBatchUpdateComponent {
    val componentType: Int
    val componentId: Int
    val updateToData: List<RecordUpdate>
    val where: List<IWhereIsComponentToUpdate>
}

interface IWhereIsComponentToUpdate {
    val table: Int
//    val column: Int
}