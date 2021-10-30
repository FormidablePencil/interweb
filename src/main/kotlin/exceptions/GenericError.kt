package exceptions

enum class GenericError {
    ServerError;

    companion object {
        fun getMsg(enum: GenericError): String {
            return when (enum) {
                ServerError -> "Server error."
            }
        }
    }
}