package domainServices

import managers.GroupManager
import models.Group
import models.GroupComments
import repositories.GroupRepository

class GroupDomainService(
    private val categoriesOfGroupRepository: CategoriesOfGroupRepository,
    private val groupRepository: GroupRepository,
    private val groupManager: GroupManager,
    private val groupCommentsRepository: GroupCommentsRepository,
) {

    //region Get

    fun GetGroupById(id: Int): Group {
        return groupRepository.GetGroupById(id)
    }

    fun GetGroupComments(groupId: Int): List<GroupComments> {
        return groupCommentsRepository.GetCommentsByGroupId(groupId)
    }

    fun GetSubGroups(subGroupsIds: List<Int>): List<Group> {
        return groupRepository.GetGroups(subGroupsIds)
    }

    fun GetRelatedGroups(relatedGroupsIds: List<Int>): List<Group> {
        return groupRepository.GetGroups(relatedGroupsIds)
    }

    //endregion

    //region Create

    fun CreateGroup() {

    }

    //endregion

}