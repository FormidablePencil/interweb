package shared

import com.idealIntent.managers.EmailManager
import integrationTests.auth.flows.LoginFlow
import integrationTests.auth.flows.SignupFlow
import io.mockk.mockk
import io.mockk.spyk
import org.apache.commons.mail.SimpleEmail
import org.koin.dsl.module

object DITestHelper {
    val FlowModule = module {
        single { SignupFlow() }
        single { LoginFlow() }
        // mocks email manager so that sending emails did happen on tests
        val eMailer: SimpleEmail = mockk(relaxed = true)
        single { eMailer }
    }

//    val UnitTestModule = module {
//        single { connectionToDbMK() }
//        single { appEnvMK() }
//        single { mockk<SimpleEmail>(relaxed = true) }
//    }
}