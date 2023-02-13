package uv.index.features.uvi.data

enum class UVLevel {
    Low, Moderate, High, VeryHigh, Extreme;

    companion object {

        @Suppress("MagicNumber")
        fun valueOf(index: Int): UVLevel? =
            when (index) {
                in 0..2 -> Low
                in 3..5 -> Moderate
                in 6..7 -> High
                in 8..10 -> VeryHigh
                in 11..Int.MAX_VALUE -> Extreme
                else -> null
            }
    }

}