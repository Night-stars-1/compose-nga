package com.srap.nga.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val BaselineTypography = Typography()

val AppTypography = Typography(
    displayLarge = BaselineTypography.displayLarge.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 64.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = BaselineTypography.headlineLarge.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = BaselineTypography.headlineMedium.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = BaselineTypography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = BaselineTypography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = BaselineTypography.titleSmall.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = BaselineTypography.bodyLarge.copy(
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = BaselineTypography.bodyMedium.copy(
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = BaselineTypography.bodySmall.copy(
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = BaselineTypography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = BaselineTypography.labelMedium.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = BaselineTypography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)
