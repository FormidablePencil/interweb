package shared

import io.kotest.datatest.WithDataTestName

data class PythagTriple(val a: Int, val b: Int, val c: Int) : WithDataTestName {
    override fun dataTestName() = "$a, $b, $c"
}


data class PythagTriple3(val a: Int, val b: Int, val c: Int) : ExtendDataTest<PythagTriple3>(), WithDataTestName {
//    operator fun component1(): Int {
//
//    }
}

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