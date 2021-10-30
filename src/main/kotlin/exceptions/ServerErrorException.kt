package exceptions

class ServerErrorException : Exception {
    constructor(errorEnum: ServerError) : super(ServerError.getMsg(errorEnum)) {
        TODO("log to log service GenericError.getMsg(GenericError.WhatEverEnum)")
    }

    constructor(errorEnum: ServerError, cause: Exception) : super(ServerError.getMsg(errorEnum), cause) {
        TODO("log to log service GenericError.getMsg(GenericError.WhatEverEnum)")
    }
}

enum class ServerError {
    FailedToCreateAuthor, FailedToSetNewPassword;

    companion object {
        fun getMsg(enum: ServerError): String {
            return when (enum) {
                FailedToCreateAuthor -> "Failed to create author."
                FailedToSetNewPassword -> "Failed to set new password."
            }
        }
    }
}