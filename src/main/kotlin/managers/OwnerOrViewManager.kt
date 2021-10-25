package managers

// a class inherited by every manager for saving email
open class OwnerOrViewManager : IUserStaticManager {
    val requesterUsername: String = ""
    var requesterId: Int = 0

    constructor(token: String) {
        // validate and if successful, get requesterUsername and requesterId
    }
}
