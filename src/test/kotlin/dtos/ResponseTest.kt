package dtos

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SampleResponse(
    val sample: String? = null, val example: Boolean? = null
) : Response<SampleResponseFailed, SampleResponse>()

class ResponseTest : BehaviorSpec({

    given("succeeded") {
        val sample = "sample"
        val example = true

        fun validatePositive(res: SampleResponse) {
            res.success shouldBe true
            res.example shouldBe example
            res.sample shouldBe sample
        }

        When("succeeded() appended") {
            val res = SampleResponse(sample, example).succeeded()
            validatePositive(res)
        }

        When("succeeded() not appended") {
            val res = SampleResponse(sample, example)
            validatePositive(res)
        }
    }

    given("failed") {
        val res = SampleResponse().failed(SampleResponseFailed.InvalidEmailFormat)

        res.success shouldBe false
        res.sample shouldBe null
        res.example shouldBe null
    }
})
