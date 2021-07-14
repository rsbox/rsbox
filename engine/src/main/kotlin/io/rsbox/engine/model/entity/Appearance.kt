package io.rsbox.engine.model.entity

data class Appearance(val models: IntArray, val colors: IntArray, val gender: Gender) {

    companion object {

        private val DEFAULT_MODELS = intArrayOf(9, 14, 109, 26, 33, 36, 42)

        private val DEFAULT_COLORS = intArrayOf(0, 3, 2, 0, 0)

        val DEFAULT = Appearance(DEFAULT_MODELS, DEFAULT_COLORS, Gender.MALE)

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Appearance

        if (!models.contentEquals(other.models)) return false
        if (!colors.contentEquals(other.colors)) return false
        if (gender != other.gender) return false

        return true
    }

    override fun hashCode(): Int {
        var result = models.contentHashCode()
        result = 31 * result + colors.contentHashCode()
        result = 31 * result + gender.hashCode()
        return result
    }
}