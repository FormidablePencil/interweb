package com.idealIntent.dtos.compositions.grids

data class GridOneOff(
    val columns: List<GridOneOffColumn>,
)

data class GridOneOffColumn(
    val title: String,
    val items: List<GridOffItem>,
)

data class GridOffItem(
    val title: String,
    val image: String,
    val navTo: String,
)