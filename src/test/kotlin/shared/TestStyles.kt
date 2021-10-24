package shared

import configurations.DIHelper
import io.kotlintest.specs.FunSpec
import org.koin.core.context.startKoin
import org.koin.test.KoinTest

fun startKoinDI() {
    startKoin {
        modules(DIHelper.CoreModule, DITestHelper.CoreModule)
    }
}

open class KoinFunSpec: FunSpec(), KoinTest { init { startKoinDI() } }
