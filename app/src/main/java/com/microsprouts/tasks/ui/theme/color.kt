package com.microsprouts.tasks.ui.theme

import androidx.compose.ui.graphics.Color

// --- The MicroSprouts "Chill & Grounded" Palette ---

// Core Branding (Sage Greens)
val SagePrimary = Color(0xFF6C8E75)       // Soft, grounding sage green
val SageDark = Color(0xFF4A6351)          // Forest moss for high-contrast elements
val SageContainer = Color(0xFFE2EFE4)     // Light, milky sage for card backdrops

// Supportive Elements (Warm Earth & Soft Slate)
val WarmSand = Color(0xFFF7F4EF)          // Creamy off-white for main backgrounds (reduces eye strain)
val TerracottaSecondary = Color(0xFFD4A373) // Warm earth tone for accents, icons, and highlights
val SlateText = Color(0xFF2F3E34)         // Dark, deep pine/slate for text (gentler than pure black)
val MutedGray = Color(0xFF8F9A91)         // Soft green-gray for placeholders and borders
val SoftWhite = Color(0xFFFAF9F6)         // Gentle linen white, easy on the eyes

object BrandPalette {
    // Array of brand palette colors directly referencing Color.kt tokens
    val PALETTE: List<Color> = listOf(
        SagePrimary,         // Step 1: Sage Green (Default)
        SageDark,            // Step 2: Forest Moss (Replaces Charcoal Gray)
        TerracottaSecondary, // Step 3: Warm Earth
        SlateText,           // Step 4: Deep Slate
        MutedGray            // Step 5: Soft Green-Gray
    )

    /**
     * Cycles through the 5 brand colors in round-robin order based on index/count.
     */
    fun getColorForIndex(index: Int): Color {
        if (PALETTE.isEmpty()) return SagePrimary
        // Math.floorMod handles negative indices safely (e.g. floorMod(-1, 5) -> 4)
        val safeIndex = Math.floorMod(index, PALETTE.size)
        return PALETTE[safeIndex]
    }}