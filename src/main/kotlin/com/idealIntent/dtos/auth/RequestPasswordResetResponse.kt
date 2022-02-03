package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiDataResponse
import dtos.auth.RequestPasswordResetResponseFailed
import dtos.responseData.PasswordResetResponseData
import io.ktor.http.*

class RequestPasswordResetResponse :
    ApiDataResponse<PasswordResetResponseData, RequestPasswordResetResponseFailed, RequestPasswordResetResponse>(
        RequestPasswordResetResponseFailed
    )


