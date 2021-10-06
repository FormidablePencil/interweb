package domainServices

import managers.GroupManager
import models.Group
import models.GroupComments
import repositories.GroupRepository

class GroupDomainService(
    private val groupRepository: GroupRepository,
    private val groupManager: GroupManager,
) {

    //region Get

    fun GetGroupById(groupId: Int): Group {
        var group = groupRepository.GetGroupById(groupId)
        return group
    }

    fun GetGroupByAuthorsCategory(groupIds: List<Int>, category: List<String>): Group {
        return groupRepository.FilterGroupByCategories(groupIds, category)
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