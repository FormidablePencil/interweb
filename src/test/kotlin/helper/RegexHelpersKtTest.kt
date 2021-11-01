package helper

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class RegexHelpersKtTest : BehaviorSpec({
    given("isStrongPassword()") {
        When("weak password") {
            val weakPasswords = listOf(
                "for", "formidable", "Formidable", "fo3", "formidable234",
                "formidable2!34", "Formidable321"
            )
            withData(weakPasswords) { a -> isStrongPassword(a) shouldBe false }
        }

        When("strong password") {
            isStrongPassword("Formidable!1") shouldBe true
        }
    }

    given("isEmailFormatted()") {
        When("format incorrect") {
            val emailIncorrectlyFormatted = listOf("a", "a@", "a@g", "a@g.")
            withData(emailIncorrectlyFormatted) { a -> isEmailFormatted(a) shouldBe false }
        }

        When("format correct") {
            isEmailFormatted("a@g.c") shouldBe true
        }
    }

    given("maskEmail()") {
        data class Emails(val email: String, val masked: String)
        withData(
            Emails("persodfdn@gmail.com", "p********@gmail.com"),
            Emails("person@g.c", "p*****@g.c")
        ) { (a, b) ->
            maskEmail(a) shouldBe b
        }
    }
})
