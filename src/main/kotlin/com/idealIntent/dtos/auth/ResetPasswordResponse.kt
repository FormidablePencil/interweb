package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiDataResponse
import dtos.IApiResponseEnum
import dtos.auth.ResetPasswordResponseFailed
import dtos.responseData.ITokenResponseData
import io.ktor.http.*


class ResetPasswordResponse :
    ApiDataResponse<ITokenResponseData, ResetPasswordResponseFailed, ResetPasswordResponse>(ResetPasswordResponseFailed)