package shared

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName

fun nameDataTests(
    methodName: String,
    argName1: String, arg1: String,
    argName2: String, arg2: String,
//        argName3: String, arg3: String
): String {
    return "$methodName: $argName1-$arg1, $argName2-$arg2"
//               + " $argName3-$arg3"
}

data class PythagTriple(val a: Int, val b: Int, val c: Int) : WithDataTestName {
    override fun dataTestName() = "$a, $b, $c"
}


data class PythagTriple3(val a: Int, val b: Int, val c: Int) : A<PythagTriple3>(), WithDataTestName {
    override fun dataTestName() = theDataTestName
}

abstract class A<T> {
    lateinit var theDataTestName: String

    fun setName(name: String, value: Any): T {
        theDataTestName = "$theDataTestName $name: $value"
        return this as T
    }
}