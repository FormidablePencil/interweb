package helper

import configurations.IConfig
import org.mindrot.jbcrypt.BCrypt

class PassEncrypt(
    private val config: IConfig
) {
    fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun decryptPassword(password: String, hashPassword: String) {
        BCrypt.checkpw(password, hashPassword)
    }
}
