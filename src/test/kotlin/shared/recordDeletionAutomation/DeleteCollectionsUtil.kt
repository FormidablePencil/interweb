package shared.recordDeletionAutomation

import com.idealIntent.configurations.AppEnv
import com.idealIntent.models.compositions.basicCollections.images.ImageCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImageToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.images.ImagesModel
import com.idealIntent.models.compositions.basicCollections.texts.TextCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextToCollectionsModel
import com.idealIntent.models.compositions.basicCollections.texts.TextsModel
import com.idealIntent.models.privileges.PrivilegedAuthorToCompositionSourcesModel
import org.koin.test.KoinTest
import org.koin.test.inject
import org.ktorm.dsl.*

class DeleteCollectionsUtil: KoinTest {
    private val appEnv: AppEnv by inject()

    fun deleteTextRecords(collectionId: Int) {
        // get record ids of collection
        val textIds = appEnv.database.from(TextToCollectionsModel)
            .select(TextToCollectionsModel.textId)
            .where { TextToCollectionsModel.collectionId eq collectionId }
            .map { it[TextToCollectionsModel.textId]!! }

        // delete association between records and collection then collection and records
        appEnv.database.delete(TextToCollectionsModel) { it.collectionId eq collectionId }
        appEnv.database.delete(TextCollectionsModel) { it.id eq collectionId }
        textIds.map { appEnv.database.delete(TextsModel) { it.id eq it.id } }
    }

    fun deleteImageRecords(collectionId: Int) {
        // get record ids of collection
        val imageIds = appEnv.database.from(ImageToCollectionsModel)
            .select(ImageToCollectionsModel.imageId)
            .where { ImageToCollectionsModel.collectionId eq collectionId }
            .map { it[ImageToCollectionsModel.imageId]!! }

        // delete association between records and collection then collection and records
        appEnv.database.delete(ImageToCollectionsModel) { it.collectionId eq collectionId }
        appEnv.database.delete(ImageCollectionsModel) { it.id eq collectionId }
        imageIds.map { appEnv.database.delete(ImagesModel) { it.id eq it.id } }
    }

    fun deletePrivilegedAuthors(authorId: Int) {
        appEnv.database.delete(PrivilegedAuthorToCompositionSourcesModel) { it.authorId eq authorId }
    }
}