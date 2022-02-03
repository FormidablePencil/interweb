package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.dtos.ApiDataResponse
import com.idealIntent.exceptions.CompositionCode

class CompositionResponse : ApiDataResponse<Int, CompositionCode, CompositionResponse>(CompositionCode)