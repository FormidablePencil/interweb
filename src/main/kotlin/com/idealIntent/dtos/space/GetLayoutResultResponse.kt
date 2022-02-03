package com.idealIntent.dtos.space

import com.idealIntent.dtos.ApiResponse
import dtos.portfolio.GetLayoutResultError
import models.portfolio.LayoutComponent

data class GetLayoutResultResponse(
    val layoutComponents: List<LayoutComponent>, val layoutArrangement: List<Int>
) : ApiResponse<GetLayoutResultError, GetLayoutResultResponse>(GetLayoutResultError)

