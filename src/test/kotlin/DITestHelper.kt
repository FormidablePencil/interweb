import org.koin.dsl.module
import tests.login.LoginE2E
import tests.signup.SignupE2E

object DITestHelper {
    val CoreModule = module {
        single { LoginE2E() }
        single { SignupE2E() }
    }
}
