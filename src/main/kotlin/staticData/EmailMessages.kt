package staticData

interface IEmailMsgStructure {
    fun subject(): String
    fun message(): String
}

class EmailMessages {
    class WelcomeMsg(private val firstname: String?) : IEmailMsgStructure {
        override fun subject(): String {
            return if (firstname == null) "Welcome dear author."
            else "Welcome dear author $firstname"
        }

        override fun message() = "Welcome to InterWeb!"
    }

    class PasswordResetMsg(val username: String) : IEmailMsgStructure {
        override fun subject() = "Reset password $username"
        override fun message() = "You have requested a password reset."
    }

    class ValidateEmailMsg(val username: String) : IEmailMsgStructure {
        override fun subject() = "Validate your email address for '$username'"
        override fun message() = "Validate your email address"
    }
}