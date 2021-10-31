package dtos

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SampleDataResponse(
    val sample: String? = null, val example: Boolean? = null
) : Response<SampleResponseFailed, SampleDataResponse>()

class ResultTest : BehaviorSpec({

    given("succeeded") {
        val sample = "sample"
        val example = true

        fun validatePositive(res: SampleDataResponse) {
            res.success shouldBe true
            res.example shouldBe example
            res.sample shouldBe sample
        }

        When("succeeded() appended") {
            val res = SampleDataResponse(sample, example).succeeded()
            validatePositive(res)
        }

        When("succeeded() not appended") {
            val res = SampleDataResponse(sample, example)
            validatePositive(res)
        }
    }

    given("failed") {
        val res = SampleDataResponse().failed(SampleResponseFailed.InvalidEmailFormat)

        res.success shouldBe false
        res.sample shouldBe null
        res.example shouldBe null
    }
})
