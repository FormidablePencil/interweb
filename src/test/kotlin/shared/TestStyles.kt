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