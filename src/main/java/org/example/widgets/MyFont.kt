package org.example

import java.awt.Font  // Font 클래스 임포트
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.io.IOException

object MyFont {
    private var blackFont: Font? = null
    private var boldFont: Font? = null
    private var extraBoldFont: Font? = null
    private var extraLightFont: Font? = null
    private var lightFont: Font? = null
    private var semiBoldFont: Font? = null
    private var thinFont: Font? = null
    private var regularFont: Font? = null
    private var mediumFont: Font? = null

    private var SCDreamBoldFont: Font? = null
    private var SCDreamBoldFontTest: Font? = null

    init {
        blackFont = loadFont("/fonts/Pretendard-Black.ttf")
        boldFont = loadFont("/fonts/Pretendard-Bold.ttf")
        extraBoldFont = loadFont("/fonts/Pretendard-ExtraBold.ttf")
        extraLightFont = loadFont("/fonts/Pretendard-ExtraLight.ttf")
        lightFont = loadFont("/fonts/Pretendard-Light.ttf")
        mediumFont = loadFont("/fonts/Pretendard-Medium.ttf")
        regularFont = loadFont("/fonts/Pretendard-Regular.ttf")
        semiBoldFont = loadFont("/fonts/Pretendard-SemiBold.ttf")
        thinFont = loadFont("/fonts/Pretendard-Thin.ttf")
        // 운영체제에 따른 SCDream 폰트 선택
        val osName = System.getProperty("os.name").lowercase()
        SCDreamBoldFont = when {
            osName.contains("win") -> loadFont("/fonts/SCDream6.ttf") // 윈도우는 SCDream6.ttf
            osName.contains("mac") -> loadFont("/fonts/SCDream7.ttf") // 맥은 SCDream7.ttf
            else -> loadFont("/fonts/SCDream7.ttf") // 기본값으로 SCDream7.ttf
        }

        SCDreamBoldFontTest = loadFont("/fonts/SCDream5.otf")
    }

    private fun loadFont(fontPath: String): Font? {
        return try {
            val inputStream = javaClass.getResourceAsStream(fontPath)
            if (inputStream != null) {
                val font = Font.createFont(Font.TRUETYPE_FONT, inputStream)
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)
//                println("폰트가 성공적으로 로드되었습니다: $fontPath")
                font
            } else {
                println("폰트 파일을 찾을 수 없습니다: $fontPath")
                null
            }
        } catch (e: FontFormatException) {
            println("폰트 형식이 잘못되었습니다: $fontPath, 에러 메시지: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: IOException) {
            println("폰트 로드 중 IO 오류가 발생했습니다: $fontPath, 에러 메시지: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            println("예상치 못한 에러가 발생했습니다: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // 기본 운영체제에 따른 기본 폰트 반환 메서드
    private fun getDefaultSystemFont(size: Float): Font {
        val osName = System.getProperty("os.name").lowercase()
        return when {
            osName.contains("mac") -> Font.getFont("San Francisco").deriveFont(size)
            osName.contains("win") -> Font.getFont("Segoe UI").deriveFont(size) // Windows 기본 폰트
            else -> Font.getFont("SSansSerif").deriveFont(size) // 기타 OS의 기본 폰트
        }
    }

    // 각 폰트 스타일 메서드
    fun Black(size: Float): Font {
        return blackFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun Bold(size: Float): Font {
        return boldFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun ExtraBold(size: Float): Font {
        return extraBoldFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun ExtraLight(size: Float): Font {
        return extraLightFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun Light(size: Float): Font {
        return lightFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun Medium(size: Float): Font {
        return mediumFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun Regular(size: Float): Font {
        return regularFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun SemiBold(size: Float): Font {
        return semiBoldFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun Thin(size: Float): Font {
        return thinFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun SCDreamBold(size: Float): Font {
        return SCDreamBoldFont?.deriveFont(size) ?: getDefaultSystemFont(size)
    }

    fun SCDreamBoldTest(size: Float): Font {
        return SCDreamBoldFontTest?.deriveFont(size) ?: getDefaultSystemFont(size)
    }
}
