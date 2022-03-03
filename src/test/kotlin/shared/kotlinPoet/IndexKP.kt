package shared.kotlinPoet

import com.idealIntent.repositories.compositions.carousels.carouselOfImagesRepositoryKP
import com.idealIntent.repositories.compositions.grids.gridOneOffRepositoryKP
import java.io.File

fun main() {
    val (writeTo, func) = carouselOfImagesRepositoryKP()
    func
        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)

//    gridOneOffRepositoryKP()
//        .writeTo(File("src/test/kotlin"))
//        .writeTo(System.out)

}
