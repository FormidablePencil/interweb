package managers

import managers.interfaces.IUserStaticManager

// a class inherited by every manager for saving simpleEmail
open class OwnerOrViewManager : IUserStaticManager {
    val requesterUsername: String = ""
    var requesterId: Int = 0

    constructor(token: String) {
        // validate and if successful, get requesterUsername and requesterId
    }
}
