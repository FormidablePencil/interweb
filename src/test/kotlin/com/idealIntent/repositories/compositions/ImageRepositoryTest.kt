package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.collectionsGeneric.images.ImagePK
import com.idealIntent.dtos.collectionsGeneric.images.ImageToCollection
import com.idealIntent.repositories.collectionsGeneric.ImageRepository
import io.kotest.assertions.failure
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.test.inject
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.images
import shared.testUtils.rollback

// todo
class ImageRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
    private val imageRepository: ImageRepository by inject()

    init {
        beforeEach {
            clearAllMocks()
        }

        // region Get

        given("getAllRecordsOfCollection") {
            then("provided id of collection that doesn't exist") {
                rollback {
                    val records = imageRepository.getAllRecordsOfCollection(93434433)
                    records shouldBe null
                }
            }

            then("provided id of collection that doesn't exist") {
                rollback {
                    val records = imageRepository.getAllRecordsOfCollection(93434433)
                    records shouldBe null
                }
            }
        }

        given("getRecordsQuery") {
            // Not meant to be tested. This method would have been private
            // if it wasn't in ICollectionStructure protocol.
        }

        given("getRecordToCollectionRelationship") {}
        // endregion Get


        // region Insert
        given("batchInsertRecordsToNewCollection") {}


        given("addRecordCollection") {}

        given("batchAssociateRecordsToCollection") {}

        given("associateRecordToCollection") {}
        // endregion Insert


        // region Update
        // endregion Update

        // region Delete
        // endregion Delete
    }
}
