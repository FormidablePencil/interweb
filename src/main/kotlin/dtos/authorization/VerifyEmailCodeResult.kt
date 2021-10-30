package dtos.authorization

import dtos.DtoResult

class VerifyEmailCodeResult : DtoResult<VerifyEmailCodeResultFailed>() {
}

enum class VerifyEmailCodeResultFailed {
    DoesNotExistEmailCode, InvalidEmailCode;

    companion object {
        fun getMsg(enum: VerifyEmailCodeResultFailed): String {
            return when (enum) {
                DoesNotExistEmailCode -> "User has not requested a email verification code."
                InvalidEmailCode -> "Invalid email code"
            }
        }
    }
}
