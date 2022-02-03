package com.idealIntent

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionExceptionReport
import io.kotest.assertions.failure
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * Exceptions text
 *
 * When catching an exception, the order of which catch statements are in matters.
 *
 * @constructor Create empty Exceptions text
 */
class ExceptionsText : BehaviorSpec({
    given("CompositionExceptionReport") {
        try {
            throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)
        } catch (ex: CompositionExceptionReport) {
            ex.clientMsg shouldBe CompositionCode.getClientMsg(CompositionCode.FailedToCompose)
            ex.message shouldBe null
            ex.clientMsg
        }
    }

    given("When catching an exception, the order of which catch statements are in matters") {
        then("CompositionExceptionReport then Exception") {
            try {
                throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)
            } catch (ex: CompositionExceptionReport) {
                ex.clientMsg shouldBe CompositionCode.getClientMsg(CompositionCode.FailedToCompose)
            } catch (ex: Exception) {
                throw failure("Should have been already caught by CompositionExceptionReport")
            }
        }
        then("Exception then CompositionExceptionReport") {
            try {
                throw CompositionExceptionReport(CompositionCode.FailedToCompose, this::class.java)
            } catch (ex: Exception) {
                ex.message shouldBe null
            } catch (ex: CompositionExceptionReport) {
                ex.clientMsg shouldBe CompositionCode.getClientMsg(CompositionCode.FailedToCompose)
                throw failure("Should have been already caught by Exception")
            }
        }
    }
})