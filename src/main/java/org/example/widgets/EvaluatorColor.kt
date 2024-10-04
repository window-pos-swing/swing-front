package org.example.widgets

import java.awt.Color

object EvaluatorColor {

    fun evaluate(color: Color, colorTarget: Color, fraction: Float): Color {
        val r = color.red + ((colorTarget.red - color.red) * fraction + 0.5f).toInt()
        val g = color.green + ((colorTarget.green - color.green) * fraction + 0.5f).toInt()
        val b = color.blue + ((colorTarget.blue - color.blue) * fraction + 0.5f).toInt()
        val a = color.alpha + ((colorTarget.alpha - color.alpha) * fraction + 0.5f).toInt()
        return Color(r, g, b, a)
    }
}