package staticData

interface IEmailMsgStructure {
    fun subject(): String
    fun message(): String
}

class EmailMessages {
    class WelcomeMsg(private val firstname: String) : IEmailMsgStructure {
        override fun subject() = "Welcome dear author $firstname"
        override fun message() = "Welcome to InterWeb!"
    }

    class PasswordResetMsg(val username: String) : IEmailMsgStructure {
        override fun subject() = "Reset password $username"
        override fun message() = "You have requested a password reset."
    }
}