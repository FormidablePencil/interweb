package shared.testUtils

/** Unit testing repositories class. */
open class BehaviorSpecUtRepo(body: BehaviorSpecUtRepo.() -> Unit = {}): BehaviorSpecIT(), SqlColConstraint {
    init {
        body()
    }
}