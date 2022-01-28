package shared.testUtils

/** Unit testing com.idealIntent.repositories class. */
abstract class BehaviorSpecUtRepo(body: BehaviorSpecUtRepo.() -> Unit = {}): BehaviorSpecIT(), SqlColConstraint {
    init {
        body()
    }
}