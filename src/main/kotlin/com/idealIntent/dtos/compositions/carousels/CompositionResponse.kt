package com.idealIntent.dtos.compositions.carousels

import com.idealIntent.exceptions.CompositionCodeReport
import dtos.ApiDataResponse

class CompositionResponse : ApiDataResponse<Int, CompositionCodeReport, CompositionResponse>(CompositionCodeReport)