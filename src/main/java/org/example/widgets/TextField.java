package org.example.widgets;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.example.style.MyColor;
import org.example.MyFont;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends JTextField {

    private float location = 0f;
    private String labelText = "아이디";
    private Color lineColor = Color.GRAY; // 기본 테두리 색상은 그레이로 설정

    public TextField(String label, Color backgroundColor) {
        this.labelText = label;
        setBackground(backgroundColor);
        setOpaque(false);  // 배경을 투명하게 설정
        setBorder(new EmptyBorder(20, 3, 10, 3));
        setSelectionColor(MyColor.INSTANCE.getDARK_RED());

        // 입력 텍스트를 x + 20만큼 오른쪽으로 이동시키기 위해 여백 설정
        setMargin(new Insets(0, 20, 0, 0));  // 왼쪽 여백을 20으로 설정

        // 포커스 리스너 설정
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                lineColor = MyColor.INSTANCE.getDARK_RED(); // 포커스가 되었을 때 DarkRed로 변경
                repaint();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                lineColor = Color.GRAY; // 포커스가 없을 때 그레이로 변경
                repaint();
            }
        });

        // DocumentListener 추가 - 텍스트 입력 변화 감지
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkText();  // 입력이 추가될 때 확인
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkText();  // 입력이 제거될 때 확인
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkText();  // 스타일 등의 변화가 있을 때 확인
            }

            // 텍스트 필드에 글자가 없으면 labelText를 보이고, 글자가 있으면 숨김
            private void checkText() {
                if (getText().isEmpty()) {
                    labelText = "가맹점 웹 아이디";  // 글자가 없으면 라벨 표시
                } else {
                    labelText = "";  // 글자가 있으면 라벨 숨김
                }
                repaint();
            }
        });


    }


    @Override
    protected void paintComponent(Graphics grphcs) {

        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // 배경 그리기 - 테두리 안쪽에 맞춰 그리기
        g2.setColor(getBackground());

        // 테두리 두께를 고려한 배경 크기 조정
        int borderThickness = 2;
        int arcSize = 10;
        int x = borderThickness / 2;
        int y = borderThickness / 2;
        int width = getWidth() - borderThickness;
        int height = getHeight() - borderThickness;

        // 테두리 안쪽에 맞춘 둥근 배경 그리기
        g2.fill(new RoundRectangle2D.Double(x, y, width, height, arcSize, arcSize));

        // 입력된 텍스트가 수직으로 중앙에 위치하도록 Y 좌표 계산
        int textY = getBaseline(getWidth(), getHeight());

        // 입력 텍스트 수직 중앙에 그리기
        g2.drawString(getText(), 20, textY);

        // 기본 텍스트 필드의 텍스트를 유지하여 그리기
        super.paintComponent(grphcs);  // 텍스트 필드의 기본 텍스트 처리

        // 힌트 텍스트와 스타일 처리
        createHintText(g2);
    }

    @Override
    protected void paintBorder(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 둥근 테두리 그리기
        g2.setColor(lineColor);

        // 테두리 두께와 둥글기 조정
        int borderThickness = 2;
        int arcSize = 10;  // 둥글기를 6으로 설정
        int x = borderThickness / 2;
        int y = borderThickness / 2;
        int width = getWidth() - borderThickness;
        int height = getHeight() - borderThickness;

        // 둥근 테두리 그리기
        g2.setStroke(new BasicStroke(borderThickness));
        g2.draw(new RoundRectangle2D.Double(x, y, width, height, arcSize, arcSize));
    }

    private void createHintText(Graphics2D g2) {
        Insets insets = getInsets();
        g2.setColor(new Color(150, 150, 150));

        // MyFont의 regularFont(20)를 사용하여 폰트 설정
        g2.setFont(MyFont.INSTANCE.Regular(20));  // regularFont에 20 크기 적용

        FontMetrics fm = g2.getFontMetrics();
        double textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2.0;

        // X 좌표를 직접적으로 지정하여 오른쪽으로 이동 (예: 20 픽셀)
        int textX = 20;  // 고정된 X 좌표로 설정하여 오른쪽으로 이동

        g2.drawString(labelText, textX, (int) textY);  // 수직 중앙에 라벨 텍스트 배치
    }

    @Override
    public Insets getInsets() {
        // 텍스트의 위치를 20만큼 오른쪽으로 이동시키기 위해 여백을 설정
        return new Insets(0, 20, 0, 0);
    }
}
