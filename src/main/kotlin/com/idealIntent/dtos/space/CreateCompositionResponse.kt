package com.idealIntent.dtos.space

import com.idealIntent.dtos.ApiDataResponse
import dtos.space.CreateCompositionResponseFailed

class CreateCompositionResponse : ApiDataResponse<String, CreateCompositionResponseFailed, CreateCompositionResponse>(
    CreateCompositionResponseFailed
)

