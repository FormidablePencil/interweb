package shared

import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.CompositionFlow
import integrationTests.compositions.banners.BannerCompositionsFlow
import integrationTests.compositions.carousels.CarouselCompositionsFlow
import integrationTests.compositions.grids.GridCompositionsFlow
import integrationTests.compositions.headers.HeaderCompositionsFlow
import integrationTests.compositions.texts.TextCompositionsFlow
import io.mockk.mockk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module

object DITestHelper {
    val FlowModule = module {
        single { SignupFlow() }
        single { LoginFlow() }
        single { CompositionFlow() }
        single { CarouselCompositionsFlow() }
        single { HeaderCompositionsFlow() }
        single { GridCompositionsFlow() }
        single { TextCompositionsFlow() }
        single { BannerCompositionsFlow() }
        // mocks email manager so that sending emails did happen on tests
        val eMailer: SimpleEmail = mockk(relaxed = true)
        single { eMailer }
    }
}