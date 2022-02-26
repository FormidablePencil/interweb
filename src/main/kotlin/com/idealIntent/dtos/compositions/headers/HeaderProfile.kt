package com.idealIntent.dtos.compositions.headers

import dtos.compositions.headers.IHeaderProfile

data class HeaderProfile(
    override val bgImg: String,
    override val name: String,
    override val profileImg: String
) : IHeaderProfile