package com.idealIntent.dtos.compositions.headers

import dtos.compositions.headers.IHeaderBasic

data class HeaderBasic(
    override val img: String,
    override val imgAlt: String,
    override val title: String
) : IHeaderBasic