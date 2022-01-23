package helper

class RandomStringGenerator() {
    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        fun string(STRING_LENGTH: Int): String {
            val randomString = (1..STRING_LENGTH)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            return randomString
        }
    }
}