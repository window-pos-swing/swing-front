import org.example.style.MyColor
import java.awt.*
import javax.swing.*
import javax.swing.plaf.basic.BasicButtonUI

class CustomToggleButton : BasicButtonUI() {
    override fun paint(g: Graphics, c: JComponent) {
        val button = c as JToggleButton
        val g2d = g as Graphics2D
        val width = c.width
        val height = c.height

        // Enable anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw the whole background as a light gray rounded rectangle
        g2d.color = MyColor.LIGHT_GREY
        g2d.fillRoundRect(0, 0, width, height, height, height)

        // Clip the selected half and draw the navy color over the appropriate side
        if (button.isSelected) {
            g2d.clipRect(width / 2, 0, width / 2, height)  // Clip to the right half
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)  // Draw full rounded rect
        } else {
            g2d.clipRect(0, 0, width / 2, height)  // Clip to the left half
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)  // Draw full rounded rect
        }

        // Reset clipping
        g2d.clip = null

        // Draw the "ON" text
        g2d.color = if (button.isSelected) Color(137,137,137) else Color.WHITE
        g2d.drawString("ON", width / 4 - g2d.fontMetrics.stringWidth("ON") / 2, height / 2 + g2d.fontMetrics.ascent / 2)

        // Draw the "OFF" text
        g2d.color = if (button.isSelected) Color.WHITE else Color(137,137,137)
        g2d.drawString("OFF", width * 3 / 4 - g2d.fontMetrics.stringWidth("OFF") / 2, height / 2 + g2d.fontMetrics.ascent / 2)
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        return Dimension(120, 40) // Adjust the size to suit your layout
    }
}

fun createCustomToggleButton(): JToggleButton {
    val toggleButton = JToggleButton()
    toggleButton.ui = CustomToggleButton()
    toggleButton.isFocusPainted = false
    toggleButton.isSelected = false // Initially set to "OFF"
    return toggleButton
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Custom Toggle Example")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = FlowLayout()

        val toggleButton = createCustomToggleButton()
        frame.add(toggleButton)

        frame.setSize(300, 200)
        frame.isVisible = true
    }
}
