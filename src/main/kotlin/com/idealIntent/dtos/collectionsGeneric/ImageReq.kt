package com.idealIntent.dtos.collectionsGeneric

import models.compositions.basicsCollections.images.IImage

data class ImageReq(override val description: String, override val id: Int?, override val url: String) : IImage