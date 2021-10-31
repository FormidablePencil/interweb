package dtos.authorization

import dtos.ApiResponse

class VerifyEmailCodeResponse : ApiResponse<VerifyEmailCodeResultFailed>() {
}

enum class VerifyEmailCodeResultFailed {
    DoesNotExistEmailCode, InvalidEmailCode;

    companion object {
        fun getMsg(enum: VerifyEmailCodeResultFailed): String {
            return when (enum) {
                DoesNotExistEmailCode,
                InvalidEmailCode -> "Invalid email code"
            }
        }
    }
}
