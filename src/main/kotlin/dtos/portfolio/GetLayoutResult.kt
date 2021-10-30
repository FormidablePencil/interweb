package dtos.portfolio

import dtos.ApiResponse
import models.portfolio.LayoutComponent

data class GetLayoutResult(
    val layoutComponents: List<LayoutComponent>, val layoutArrangement: List<Int>
) : ApiResponse<GetLayoutResultError>()

enum class GetLayoutResultError {

}
