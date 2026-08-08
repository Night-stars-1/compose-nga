package com.srap.nga.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object AppMotion {
    fun <T> fastSpatial() = spring<T>(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMedium,
    )

    fun <T> defaultSpatial() = spring<T>(
        dampingRatio = 0.72f,
        stiffness = Spring.StiffnessMediumLow,
    )
}
