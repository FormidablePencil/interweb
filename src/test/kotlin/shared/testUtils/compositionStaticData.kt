package shared.testUtils

import com.google.gson.Gson
import com.idealIntent.dtos.collectionsGeneric.images.Image
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.Text
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.dtos.compositions.carousels.CreateCarouselBasicImagesReq

// region compositions and collections
// todo - put in an companion object
val images = listOf(
    Image(
        10000,
        "PAO Mnemonic System",
        "https://i.ibb.co/1K7jQJw/pao.png"
    ),
    Image(
        20000,
        "Emoji Tack Toes",
        "https://i.ibb.co/pXgcQ16/ticktacktoe.png"
    ),
    Image(
        30000,
        "Crackalackin",
        "https://i.ibb.co/4YP7yDb/crackalackin.png"
    ),
    Image(
        40000,
        "Pokedex",
        "https://i.ibb.co/w0m4pF3/pokedex.png"
    ),
)

val texts = listOf(
    Text(10000, "first"),
    Text(20000, "second"),
    Text(30000, "third"),
    Text(40000, "fourth"),
)

val privilegedAuthors = listOf(
    PrivilegedAuthor("billy", modify = 1, view = 0),
    PrivilegedAuthor("bob", modify = 0, view = 1),
    PrivilegedAuthor("lex", modify = 1, view = 1),
    PrivilegedAuthor("freya", modify = 0, view = 0),
)
// endregion compositions and collections

// region categories of compositions
val createCarouselBasicImagesReq = CreateCarouselBasicImagesReq("Projects", images, texts, privilegedAuthors)
val carouselBasicImagesRes =
    CarouselBasicImagesRes(99999999, 88888888, "Projects", giveIdsToImages(images), giveIdsToTexts(texts), privilegedAuthors)

fun giveIdsToImages(records: List<Image> = images): List<ImagePK> =
    records.mapIndexed { idx, it ->
        ImagePK(id = 99999 + idx, orderRank = it.orderRank, url = it.url, description = it.description)
    }

fun giveIdsToTexts(records: List<Text> = texts): List<TextPK> =
    records.mapIndexed { idx, it ->
        TextPK(id = 99999 + idx, orderRank = it.orderRank, text = it.text)
    }

val carouselBasicImagesReqStingified =
    Gson().toJson(createCarouselBasicImagesReq, createCarouselBasicImagesReq::class.java)
// endregion categories of compositions
