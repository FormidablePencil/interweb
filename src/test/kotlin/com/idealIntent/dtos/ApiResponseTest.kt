package com.idealIntent.dtos

import dtos.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.ktor.http.*

data class ExampleDto(val sample: String, val example: Boolean)

class ExampleApiResponse : ApiResponse<E, ExampleApiResponse>(E)
class ExampleDataApiResponse : ApiDataResponse<ExampleDto, E, ExampleDataApiResponse>(E)

internal typealias E = SampleResponseFailed

enum class SampleResponseFailed {
    WeakPassword,
    InvalidEmailFormat,
    EmailTaken,
    UsernameTaken;

    companion object : IApiResponseEnum<E> {
        override fun getClientMsg(code: E): String {
            return when (code) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
            }
        }

        override fun getHttpCode(code: E): HttpStatusCode {
            return when (code) {
                WeakPassword,
                InvalidEmailFormat,
                EmailTaken,
                UsernameTaken -> HttpStatusCode.BadRequest
            }
        }
    }
}

// todo - use an actual response object. Use CompositionCode and CompositionResponse

class ApiResponseKtTest : BehaviorSpec({

    given("ResponseFailed enum class") {
        SampleResponseFailed.getClientMsg(SampleResponseFailed.WeakPassword) shouldBe "Not a strong enough password."
        SampleResponseFailed.getHttpCode(SampleResponseFailed.WeakPassword) shouldBe HttpStatusCode.BadRequest
    }

    given("ApiResponse") {

        then("succeeded") {
            val statusCode = HttpStatusCode.OK
            val result = ExampleApiResponse().succeeded(statusCode)

            println(result.message())

            result.code shouldBe null
            result.statusCode() shouldBe statusCode
            result.message() shouldBe null
        }

        then("failed") {
            val enumCode = SampleResponseFailed.EmailTaken
            val result = ExampleApiResponse().failed(enumCode)

            result.code shouldBe enumCode
            result.statusCode() shouldBe SampleResponseFailed.getHttpCode(enumCode)
            result.message() shouldBe SampleResponseFailed.getClientMsg(enumCode)
        }
    }

    given("ApiDataResponse") {

        then("succeeded") {
            val sample = ExampleDto("example", false)
            val statusCode = HttpStatusCode.OK
            val result = ExampleDataApiResponse().succeeded(statusCode, sample)

            result.code shouldBe null
            result.statusCode() shouldBe statusCode
            result.message() shouldBe null

            val data = result.data ?: throw Exception("test failed")
            data.sample shouldBe sample.sample
            data.example shouldBe sample.example
        }

        then("failed") {
            val enumCode = SampleResponseFailed.EmailTaken
            val result = ExampleDataApiResponse().failed(enumCode)

            result.code shouldBe enumCode
            result.statusCode() shouldBe SampleResponseFailed.getHttpCode(enumCode)
            result.message() shouldBe SampleResponseFailed.getClientMsg(enumCode)

            result.data shouldBe null
        }
    }
})
