package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiDataResponse
import dtos.auth.TokensResponseFailed
import dtos.responseData.ITokenResponseData
import io.ktor.http.*

class TokensResponse : ApiDataResponse<ITokenResponseData, TokensResponseFailed, TokensResponse>(TokensResponseFailed)


