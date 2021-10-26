package helper

fun isStrongPassword(password: String): Boolean {
    val passwordRegex = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{8,}""".toRegex()
    return passwordRegex.matches(password)
}

fun isEmailFormatted(email: String): Boolean {
    val emailFormattedRegex = """[^@ \t\r\n]+@[^@ \t\r\n]+\.[^@ \t\r\n]+""".toRegex()
    return emailFormattedRegex.matches(email)
}