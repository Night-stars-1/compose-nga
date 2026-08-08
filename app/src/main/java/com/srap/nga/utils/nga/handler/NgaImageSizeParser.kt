package com.srap.nga.utils.nga.handler

internal data class NgaImageSize(
    val width: Int,
    val height: Int,
)

/** NGA image names encode dimensions as base-36 values, for example Szk-k0 = 1280x720. */
internal object NgaImageSizeParser {
    private val encodedSize = Regex(
        """S([0-9a-z]+)-([0-9a-z]+)(?:[._?]|$)""",
        RegexOption.IGNORE_CASE,
    )

    fun parse(source: String): NgaImageSize? {
        val match = encodedSize.findAll(source).lastOrNull() ?: return null
        val width = match.groupValues[1].toIntOrNull(36) ?: return null
        val height = match.groupValues[2].toIntOrNull(36) ?: return null
        if (width !in 1..MAX_IMAGE_DIMENSION || height !in 1..MAX_IMAGE_DIMENSION) return null
        return NgaImageSize(width, height)
    }

    private const val MAX_IMAGE_DIMENSION = 16_384
}
