package com.idealIntent.dtos

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
        override fun getMsg(code: E): String {
            return when (code) {
                WeakPassword -> "Not a strong enough password."
                InvalidEmailFormat -> "Email provided is not formatted as such."
                EmailTaken -> "Email taken."
                UsernameTaken -> "Username taken."
            }
        }

        override fun getStatusCode(code: E): HttpStatusCode {
            return when (code) {
                WeakPassword,
                InvalidEmailFormat,
                EmailTaken,
                UsernameTaken -> HttpStatusCode.BadRequest
            }
        }
    }
}

class ApiResponseKtTest : BehaviorSpec({

    given("ResponseFailed enum class") {
        SampleResponseFailed.getMsg(SampleResponseFailed.WeakPassword) shouldBe "Not a strong enough password."
        SampleResponseFailed.getStatusCode(SampleResponseFailed.WeakPassword) shouldBe HttpStatusCode.BadRequest
    }

    given("ApiResponse") {
        then("succeeded") {
            val statusCode = HttpStatusCode.OK
            val result = ExampleApiResponse().succeeded(statusCode)

            result.code shouldBe null
            result.statusCode() shouldBe statusCode
            result.message().shouldBeEmpty()
        }
        then("failed") {
            val enumCode = SampleResponseFailed.EmailTaken
            val result = ExampleApiResponse().failed(enumCode)

            result.code shouldBe enumCode
            result.statusCode() shouldBe SampleResponseFailed.getStatusCode(enumCode)
            result.message() shouldBe SampleResponseFailed.getMsg(enumCode)
        }
    }

    given("ApiDataResponse") {
        then("succeeded") {
            val sample = ExampleDto("example", false)
            val statusCode = HttpStatusCode.OK
            val result = ExampleDataApiResponse().succeeded(statusCode, sample)

            result.code shouldBe null
            result.statusCode() shouldBe statusCode
            result.message().shouldBeEmpty()

            val data = result.data ?: throw Exception("test failed")
            data.sample shouldBe sample.sample
            data.example shouldBe sample.example
        }

        then("failed") {
            val enumCode = SampleResponseFailed.EmailTaken
            val result = ExampleDataApiResponse().failed(enumCode)

            result.code shouldBe enumCode
            result.statusCode() shouldBe SampleResponseFailed.getStatusCode(enumCode)
            result.message() shouldBe SampleResponseFailed.getMsg(enumCode)

            result.data shouldBe null
        }
    }
})
