package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiDataResponse
import dtos.responseData.ITokenResponseData
import dtos.signup.SignupResponseFailed
import io.ktor.http.*

class SignupResponse : ApiDataResponse<ITokenResponseData, SignupResponseFailed, SignupResponse>(SignupResponseFailed)