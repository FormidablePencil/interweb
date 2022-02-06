package shared.recordDeletionAutomation

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.collectionsGeneric.texts.TextPK
import com.idealIntent.dtos.compositions.carousels.CarouselBasicImagesRes
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.compositions.carousels.ImagesCarouselsModel
import com.idealIntent.models.privileges.PrivilegeSourcesModel
import com.idealIntent.models.privileges.PrivilegedAuthorsToCompositionsModel
import com.idealIntent.repositories.profile.AuthorRepository
import models.profile.AuthorsModel
import org.koin.test.KoinTest
import org.koin.test.inject
import org.ktorm.dsl.*

class DeleteCarouselBasicImageUtil : KoinTest {
    private val authorRepository: AuthorRepository by inject()
    private val deleteCollectionsUtil: DeleteCollectionsUtil by inject()
    private val appEnv: AppEnv by inject()
    private val defaultIdValue = 90000000

    fun deleteAllOfAuthorsCarouselBasicImage(authorId: Int) {
        val compositions = getCarouselBasicImagesQuery(onlyIds = true, authorId = authorId)

        compositions.forEach {
            appEnv.database.delete(ImagesCarouselsModel) { item -> item.id eq it.id }

            it.imgOnclickRedirects.forEach { record ->
                if (record.id == defaultIdValue) return@forEach
                deleteCollectionsUtil.deleteTextRecords(record.id)
            }

            it.images.forEach { record ->
                if (record.id == defaultIdValue) return@forEach
                deleteCollectionsUtil.deleteImageRecords(record.id)
            }

            it.privilegedAuthors.forEach { record ->
                if (record.username.isEmpty()) return@forEach
                val authorId = authorRepository.getByUsername(record.username)?.id
                    ?: throw Exception("Did not find author by username of ${record.username}")

                deleteCollectionsUtil.deletePrivilegedAuthors(authorId)
            }
            appEnv.database.delete(PrivilegeSourcesModel) { record -> record.id eq record.privilegeLevel }
        }
    }

    /**
     * Get carousel basic images query
     *
    SELECT comp.name AS comp_name, comp.privilege_id AS comp_privilege_id, comp.id AS comp_id, img.id AS img_id, textRedirect.id AS textRedirect_id, author.username AS author_username
    FROM privilege_sources prvSource
    LEFT JOIN image_carousels comp ON comp.privilege_id = prvSource.id
    LEFT JOIN privileged_authors_to_compositions prvAuthorsComp ON prvAuthorsComp.privilege_id = prvSource.id
    LEFT JOIN authors author ON author.id = prvAuthorsComp.author_id
    LEFT JOIN image_to_collections img2Col ON img2Col.collection_id = comp.image_collection_id
    LEFT JOIN images img ON img.id = img2Col.image_id
    LEFT JOIN text_to_collections textRedirect2Col ON textRedirect2Col.collection_id = comp.redirect_text_collection_id
    LEFT JOIN texts textRedirect ON textRedirect.id = textRedirect2Col.text_id
    WHERE prvAuthorsComp.author_id = ?;
     *
     * @param onlyIds
     * @param layoutId
     * @param editable
     * @param deletable
     * @param compositionId
     * @param authorId
     * @return
     */
    private fun getCarouselBasicImagesQuery(
        onlyIds: Boolean = true,
        layoutId: Int? = null,
        editable: Int? = null,
        deletable: Int? = null,
        compositionId: Int? = null,
        authorId: Int? = null,
    ): List<CarouselBasicImagesRes> {
        val prvSource = PrivilegeSourcesModel.aliased("prvSource")

        val comp = ImagesCarouselsModel.aliased("comp")

        val img2Col = ImageToCollectionsModel.aliased("img2Col")
        val img = ImagesModel.aliased("img")

        val text2Col = TextToCollectionsModel.aliased("textRedirect2Col")
        val text = TextsModel.aliased("textRedirect")

        val prvAth = PrivilegedAuthorsToCompositionsModel("prvAuthorsComp")
        val author = AuthorsModel.aliased("author")

        val selectOnlyIds = listOf(comp.name, prvSource.id, comp.id, img.id, text.id, author.username)
        val select = listOf(
            comp.name, comp.id,
            img2Col.orderRank, img.id, img.url, img.description,
            text2Col.orderRank, text.id, text.text,
            prvAth.modify, prvAth.view,
            author.username
        )

        val idAndNameOfCompositions = mutableSetOf<Triple<Int, Int, String>>()
        val images = mutableListOf<Pair<Int, ImagePK>>()
        val imgOnclickRedirects = mutableListOf<Pair<Int, TextPK>>()
        val privilegedAuthors = mutableListOf<Pair<Int, PrivilegedAuthor>>()

        appEnv.database.from(prvSource)
            .leftJoin(comp, comp.privilegeId eq prvSource.id)
            .leftJoin(prvAth, prvAth.privilegeId eq prvSource.id)
            .leftJoin(author, author.id eq prvAth.authorId)

            .leftJoin(img2Col, img2Col.collectionId eq comp.imageCollectionId)
            .leftJoin(img, img.id eq img2Col.imageId)

            .leftJoin(text2Col, text2Col.collectionId eq comp.redirectTextCollectionId)
            .leftJoin(text, text.id eq text2Col.textId)

            .select(if (onlyIds) selectOnlyIds else select)
            .whereWithConditions {
                if (!onlyIds) it += (img2Col.orderRank eq text2Col.orderRank)
                if (compositionId != null) it += (comp.id eq compositionId)
                if (authorId != null) it += (prvAth.authorId eq authorId)
                if (editable != null) it += (prvAth.modify eq editable)
                if (deletable != null) it += (prvAth.view eq deletable)
            }
            .map {
                println(
                    "${it[comp.name]}," +
                            " ${it[img.id]}," +
                            " ${it[img2Col.orderRank]}," +
                            " ${it[img.url]}," +
                            " ${it[img.description]}," +
                            " ${it[author.username]}, ${it[prvSource.id]}"
                )

                if (onlyIds) {
                    idAndNameOfCompositions += Triple(
                        if (it[comp.id] == null) defaultIdValue else it[comp.id]!!,
                        if (it[prvSource.id] == null) defaultIdValue else it[prvSource.id]!!,
                        if (it[comp.name] == null) "" else it[comp.name]!!
                    )
                    images.add(
                        Pair(
                            if (it[comp.id] == null) defaultIdValue else it[comp.id]!!,
                            ImagePK(
                                id = defaultIdValue,
                                orderRank = 0,
                                url = "",
                                description = ""
                            )
                        )
                    )
                    imgOnclickRedirects.add(
                        Pair(
                            if (it[comp.id] == null) defaultIdValue else it[comp.id]!!,
                            TextPK(
                                id = defaultIdValue,
                                orderRank = 0,
                                text = ""
                            )
                        )
                    )
                    privilegedAuthors.add(
                        Pair(
                            if (it[comp.id] == null) defaultIdValue else it[comp.id]!!,
                            PrivilegedAuthor(
                                username = it[author.username]!!,
                                modify = 0,
                                view = 0,
                            )
                        )
                    )
                } else {
                    idAndNameOfCompositions += Triple(
                        it[comp.id]!!,
                        it[comp.privilegeId]!!,
                        it[comp.name]!!
                    )
                    images.add(
                        Pair(
                            it[comp.id]!!,
                            ImagePK(
                                id = it[text.id]!!,
                                orderRank = it[img2Col.orderRank]!!,
                                url = it[img.url]!!,
                                description = it[img.description]!!
                            )
                        )
                    )
                    imgOnclickRedirects.add(
                        Pair(
                            it[comp.id]!!,
                            TextPK(
                                id = it[text.id]!!,
                                orderRank = it[text2Col.orderRank]!!,
                                text = it[text.text]!!
                            )
                        )
                    )
                    privilegedAuthors.add(
                        Pair(
                            it[comp.id]!!,
                            PrivilegedAuthor(
                                username = it[author.username]!!,
                                modify = it[prvAth.modify]!!,
                                view = it[prvAth.view]!!,
                            )
                        )
                    )
                }
            }

        return idAndNameOfCompositions.map { idAndNameOfComposition ->
            val (compId, privilegeSourceId, name) = idAndNameOfComposition

            val compImages = mutableListOf<ImagePK>()
            images.forEach { if (it.first == compId) compImages.add(it.second) }

            val compRedirects = mutableListOf<TextPK>()
            images.forEach { if (it.first == compId) compImages.add(it.second) }
            imgOnclickRedirects.forEach { if (it.first == compId) compRedirects.add(it.second) }

            val compPrivilegedAuthors = mutableListOf<PrivilegedAuthor>()
            privilegedAuthors.forEach { if (it.first == compId) compPrivilegedAuthors.add(it.second) }

            return@map CarouselBasicImagesRes(
                id = compId,
                privilegeSourceId = privilegeSourceId,
                name = name,
                images = compImages,
                imgOnclickRedirects = compRedirects,
                privilegedAuthors = compPrivilegedAuthors
            )
        }
    }
}