package shared.testUtils

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesReq

// region compositions and collections
val images = listOf(
    Image(
        null,
        10000,
        "PAO Mnemonic System",
        "https://i.ibb.co/1K7jQJw/pao.png"
    ),
    Image(
        null,
        20000,
        "Emoji Tack Toes",
        "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
    ),
    Image(
        null,
        30000,
        "Crackalackin",
        "https://i.ibb.co/4YP7yDb/crackalackin.png"
    ),
    Image(
        null,
        40000,
        "Pokedex",
        "https://i.ibb.co/w0m4pF3/pokedex.png"
    ),
)

val texts = listOf(
    Text(null, 10000, "first"),
    Text(null, 20000, "second"),
    Text(null, 30000, "third"),
    Text(null, 40000, "fourth"),
)

val privilegedAuthors = listOf(
    PrivilegedAuthor("billy", modify = true, view = false),
    PrivilegedAuthor("bob", modify = false, view = true),
    PrivilegedAuthor("lex", modify = true, view = true),
    PrivilegedAuthor("freya", modify = false, view = false),
)
// endregion compositions and collections

// region categories of compositions
val carouselBasicImagesReq = CarouselBasicImagesReq("Projects", images, texts, privilegedAuthors)

val carouselBasicImagesReqStingified = Gson().toJson(carouselBasicImagesReq, carouselBasicImagesReq::class.java)
// endregion categories of compositions
