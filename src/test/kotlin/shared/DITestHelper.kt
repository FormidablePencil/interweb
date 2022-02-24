package shared

import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import integrationTests.compositions.CompositionFlow
import integrationTests.compositions.carousels.CarouselCompositionFlow
import io.mockk.mockk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module

object DITestHelper {
    val FlowModule = module {
        single { SignupFlow() }
        single { LoginFlow() }
        single { CompositionFlow() }
        single { CarouselCompositionFlow() }
        // mocks email manager so that sending emails did happen on tests
        val eMailer: SimpleEmail = mockk(relaxed = true)
        single { eMailer }
    }
}