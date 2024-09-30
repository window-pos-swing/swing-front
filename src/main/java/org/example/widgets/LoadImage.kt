import javax.imageio.ImageIO
import javax.swing.ImageIcon
import java.awt.Image
import java.io.IOException

object LoadImage {
    // 이미지 로드 및 크기 조정 함수
    fun loadImage(resourcePath: String, width: Int, height: Int): ImageIcon {
        return try {
            val imageStream = javaClass.getResourceAsStream(resourcePath)
            if (imageStream != null) {
                val image = ImageIO.read(imageStream).getScaledInstance(width, height, Image.SCALE_SMOOTH)
//                println("이미지를 성공적으로 로드했습니다: $resourcePath")
                ImageIcon(image)
            } else {
                throw IOException("이미지를 찾을 수 없습니다: $resourcePath")
            }
        } catch (e: Exception) {
            println("이미지 로드 실패: ${e.message}")
            e.printStackTrace()
            // 로드 실패 시 기본 또는 대체 이미지 반환
            ImageIcon(javaClass.getResource("/fallback_logo.png").getPath()) // 대체 이미지 경로
        }
    }
}