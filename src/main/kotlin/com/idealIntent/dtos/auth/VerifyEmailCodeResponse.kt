package com.idealIntent.dtos.auth

import com.idealIntent.dtos.ApiResponse
import dtos.auth.VerifyEmailCodeResponseFailed

class VerifyEmailCodeResponse :
    ApiResponse<VerifyEmailCodeResponseFailed, VerifyEmailCodeResponse>(VerifyEmailCodeResponseFailed)

