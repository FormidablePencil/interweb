package shared

//import io.kotest.datatest.WithDataTestName
//
//data class PythagTriple(val a: Int, val b: Int, val c: Int) : WithDataTestName {
//    override fun dataTestName() = "$a, $b, $c"
//}
//
//
//data class PythagTriple3(val a: Int, val b: Int, val c: Int) : ExtendDataTest<PythagTriple3>(), WithDataTestName {
////    operator fun component1(): Int {
////
////    }
//}

abstract class ExtendDataTest<T>: DataTestName<T>() {
   override var testName: String = ""
    fun dataTestName() = testName.dropLast(3)

    fun setName(name: String): T {
        testName = name
        return this as T
    }
}

open class DataTestName <T> {
    open var testName: String = ""

    fun set(name: String, value: Any): T {
        testName = "$testName$name: $value - "
        return this as T
    }
}

//context("test again 1") {
//    withData(
//        PythagTriple3(6, 8, 10)
//            .set("strong password", false)
//            .set("email formatted", false),
//        PythagTriple3(3, 4, 5)
//            .set("strong password", true)
//            .set("email formatted", false),
//        PythagTriple3(3, 4, 5)
//            .set("strong password", false)
//            .set("email formatted", true),
//        PythagTriple3(6, 8, 10)
//            .set("strong password", true)
//            .set("email formatted", true),
//    ) { (a, b, c) ->
//        isPythagTriple(a, b, c) shouldBe true
//    }
//}