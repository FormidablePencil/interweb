package managers

import repositories.GroupRepository

class GroupManager(
    private val categoriesOfGroupRepository: CategoriesOfGroupRepository,
    private val groupingRepository: GroupRepository,
    private val groupingCommentsRepository: GroupCommentsRepository
) {
    fun GetGroupDataById(groupingId: Int) {
        groupingRepository.GetGroup(groupingId)

        groupingCommentsRepository.GetCommentsByGroupId(groupingId)
    }

    fun GetGroupComments(groupingId: Int) {
        categoriesOfGroupRepository.GetCategories(groupingId)
    }

}