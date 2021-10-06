package repositories

import models.Group

class GroupRepository : IGroupingRepository {

    //region Get

    fun GetGroupById(id: Int): Group {
        return Group
    }

    fun GetGroups(groupsIds: List<Int>): List<Group> {
        return emptyList<Group>()
    }

    fun GetGroupsByTag(author: String) {

    }

    fun FilterGroupByCategories(groupIds: List<Int>, category: List<String>): Group {
        return Group
    }

    fun GetGroupByAuthorsTags(authorId: Int, tag: String): List<Group> {
        throw Exception()
    }

    //endregion Get

    fun CreateGroup() {

    }

    fun DeleteGroup() {

    }

}