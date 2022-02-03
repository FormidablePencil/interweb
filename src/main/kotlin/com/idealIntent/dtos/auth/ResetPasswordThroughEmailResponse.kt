package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiResponse
import dtos.auth.ResetPasswordThroughEmailResponseFailed
import io.ktor.http.*

class ResetPasswordThroughEmailResponse : ApiResponse<ResetPasswordThroughEmailResponseFailed, ResetPasswordThroughEmailResponse>(ResetPasswordThroughEmailResponseFailed)

