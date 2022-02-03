package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiDataResponse
import dtos.auth.LoginResponseFailed
import dtos.responseData.ITokenResponseData
import io.ktor.http.*

class LoginResponse : ApiDataResponse<ITokenResponseData, LoginResponseFailed, LoginResponse>(LoginResponseFailed)


