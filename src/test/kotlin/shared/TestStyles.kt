package shared

import configurations.DIHelper
import io.kotlintest.specs.*
import org.koin.core.context.startKoin
import org.koin.test.KoinTest

fun startKoinDI() {
    startKoin {
        modules(DIHelper.CoreModule, DITestHelper.CoreModule)
    }
}

open class KoinStringSpec: StringSpec(), KoinTest { init { startKoinDI() } }
open class KoinFunSpec: FunSpec(), KoinTest { init { startKoinDI() } }
open class KoinShouldSpec: ShouldSpec(), KoinTest { init { startKoinDI() } }
open class KoinWordSpec: WordSpec(), KoinTest { init { startKoinDI() } }
open class KoinFeatureSpec: FeatureSpec(), KoinTest { init { startKoinDI() } }
open class KoinBehaviorSpec: BehaviorSpec(), KoinTest { init { startKoinDI() } }
open class KoinFreeSpec: FreeSpec(), KoinTest { init { startKoinDI() } }
open class KoinDescribeSpec: DescribeSpec(), KoinTest { init { startKoinDI() } }
open class KoinExpectSpec: ExpectSpec(), KoinTest { init { startKoinDI() } }

fun startUt() {
    startKoin {
        modules(DITestHelper.UnitTestModule)
    }
}

open class UtStringSpec: StringSpec(), KoinTest { init { startUt() } }
open class UtFunSpec: FunSpec(), KoinTest { init { startUt() } }
open class UtShouldSpec: ShouldSpec(), KoinTest { init { startUt() } }
open class UtWordSpec: WordSpec(), KoinTest { init { startUt() } }
open class UtFeatureSpec: FeatureSpec(), KoinTest { init { startUt() } }
open class UtBehaviorSpec: BehaviorSpec(), KoinTest { init { startUt() } }
open class UtFreeSpec: FreeSpec(), KoinTest { init { startUt() } }
open class UtDescribeSpec: DescribeSpec(), KoinTest { init { startUt() } }
open class UtExpectSpec: ExpectSpec(), KoinTest { init { startUt() } }