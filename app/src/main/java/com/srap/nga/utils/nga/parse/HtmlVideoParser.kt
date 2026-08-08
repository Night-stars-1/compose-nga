package com.srap.nga.utils.nga.parse

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

internal object HtmlVideoParser {
    private val gifDerivativePattern = Regex(
        """\.gif\.medium\.(?:jpe?g|png|webp|avif)(?=$|[?#&])""",
        RegexOption.IGNORE_CASE,
    )
    private val gifReferencePattern = Regex(
        """\.gif(?=$|[?#&/])""",
        RegexOption.IGNORE_CASE,
    )

    fun isGif(video: NgaVideo): Boolean = gifImageUrl(video) != null

    fun gifImageUrl(video: NgaVideo): String? {
        video.animationUrl?.let { return normalizeGifUrl(it) ?: it }
        normalizeGifUrl(video.url)?.let { return it }
        normalizeGifUrl(video.posterUrl)?.let { return it }
        return if (video.mimeType.equals("image/gif", ignoreCase = true)) {
            video.url
        } else {
            null
        }
    }

    fun parse(html: String): NgaVideo? =
        Jsoup.parseBodyFragment(html).selectFirst("video")?.let(::parseVideoElement)

    fun parse(container: Element): NgaVideo? {
        val video = if (container.normalName() == "video") {
            container
        } else {
            container.selectFirst("video")
        } ?: return null
        return parseVideoElement(video)
    }

    private fun parseVideoElement(video: Element): NgaVideo? {
        val source = video.selectFirst("source[src], source[data-src], source[data-original]")
        val url = firstAttribute(video, "src", "data-src", "data-original")
            ?: firstAttribute(source, "src", "data-src", "data-original")
            ?: return null
        val posterUrl = firstAttribute(video, "poster", "data-poster")
        val mimeType = firstAttribute(video, "type")
            ?: firstAttribute(source, "type")
        val explicitAnimationUrl = firstAttribute(
            video,
            "data-gif",
            "data-gif-src",
            "data-animation",
        ) ?: firstAttribute(
            source,
            "data-gif",
            "data-gif-src",
            "data-animation",
        )
        val inferredAnimationUrl = sequenceOf(
            explicitAnimationUrl,
            url,
            posterUrl,
        ).filterNotNull().firstNotNullOfOrNull(::normalizeGifUrl)

        return NgaVideo(
            url = url,
            posterUrl = posterUrl,
            mimeType = mimeType,
            animationUrl = inferredAnimationUrl
                ?: explicitAnimationUrl
                ?: url.takeIf { mimeType.equals("image/gif", ignoreCase = true) },
        )
    }

    private fun firstAttribute(element: Element?, vararg names: String): String? =
        names.firstNotNullOfOrNull { name ->
            element?.attr(name)?.trim()?.takeIf { it.isNotEmpty() }
        }

    private fun normalizeGifUrl(url: String?): String? {
        val value = url?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (gifDerivativePattern.containsMatchIn(value)) {
            return value.replace(gifDerivativePattern, ".gif")
        }
        if (gifReferencePattern.containsMatchIn(value)) return value

        val decodedValue = runCatching {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        }.getOrDefault(value)
        return value.takeIf { gifReferencePattern.containsMatchIn(decodedValue) }
    }
}
