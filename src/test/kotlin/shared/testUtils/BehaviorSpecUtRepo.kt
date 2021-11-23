package shared.testUtils

/** Unit testing repositories class. */
abstract class BehaviorSpecUtRepo(body: BehaviorSpecUtRepo.() -> Unit = {}): BehaviorSpecIT(), SqlColConstraint {
    init {
        body()
    }
}