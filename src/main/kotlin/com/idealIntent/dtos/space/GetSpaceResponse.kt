package com.idealIntent.dtos.space

import com.idealIntent.dtos.ApiDataResponse
import dtos.responseData.SpaceResponseData
import dtos.space.SpaceResponseFailed

class GetSpaceResponse : ApiDataResponse<SpaceResponseData, SpaceResponseFailed, GetSpaceResponse>(SpaceResponseFailed)
