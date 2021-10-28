package dtos.portfolio

import dtos.DtoResult
import models.portfolio.LayoutComponent

data class GetLayoutResult(
    val layoutComponents: List<LayoutComponent>, val layoutArrangement: List<Int>
) : DtoResult<GetLayoutResultError>()

enum class GetLayoutResultError {

}
