package shared.kotlinPoet

import com.idealIntent.repositories.compositions.carousels.carouselOfImagesRepositoryKP
import java.io.File

fun main() {
    val (writeTo, func) = carouselOfImagesRepositoryKP()
    func
//        .writeTo(File(writeTo))
        .writeTo(System.out)
}
