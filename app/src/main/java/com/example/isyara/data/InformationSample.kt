package com.example.isyara.data

data class InformationSample(
    val id: String,
    val title: String,
    val description: String
) {
    companion object {
        fun getDummyData(): List<InformationSample> {
            return listOf(
                InformationSample("1", "Title 1", "Description for Title 1"),
                InformationSample("2", "Title 2", "Description for Title 2"),
                InformationSample("3", "Title 3", "Description for Title 3"),
                InformationSample("4", "Title 4", "Description for Title 4")
            )
        }
    }
}
