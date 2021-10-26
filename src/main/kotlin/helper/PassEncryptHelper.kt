package helper

import org.mindrot.jbcrypt.BCrypt

class PassEncrypt() {
    fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun decryptPassword(password: String, hashPassword: String) {
        BCrypt.checkpw(password, hashPassword)
    }
}
