package dtos.portfolio

import dtos.ApiDataResponse
import dtos.ApiResponse
import dtos.IApiResponseEnum
import io.ktor.http.*
import models.portfolio.LayoutComponent

data class GetLayoutResult(
    val layoutComponents: List<LayoutComponent>, val layoutArrangement: List<Int>
) : ApiResponse<GetLayoutResultError, GetLayoutResult>(GetLayoutResultError)

enum class GetLayoutResultError {
    F;

    companion object : IApiResponseEnum<GetLayoutResultError> {
        override fun getMsg(code: GetLayoutResultError): String {
            TODO("Not yet implemented")
        }

        override fun getStatusCode(code: GetLayoutResultError): HttpStatusCode {
            TODO("Not yet implemented")
        }

    }
}
