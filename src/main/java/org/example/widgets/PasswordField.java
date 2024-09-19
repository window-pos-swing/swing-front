package org.example.widgets;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import org.example.MyFont;
import org.example.style.MyColor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PasswordField extends JPasswordField {

    private String labelText = "비밀번호";
    private Color lineColor = Color.GRAY;  // 기본 테두리 색상은 그레이로 설정
    private boolean hide = true;
    private boolean showAndHide;

    public PasswordField(String label, Color backgroundColor) {
        this.labelText = label;
        setBackground(backgroundColor);
        setOpaque(false);  // 배경을 투명하게 설정
        setBorder(new EmptyBorder(20, 3, 10, 30));  // 여백 설정
        setSelectionColor(MyColor.INSTANCE.getDARK_RED());  // 선택 색상 설정

        // 드래그 시 텍스트 배경 색상을 연한 회색으로 설정
        setSelectionColor(new Color(220, 220, 220)); // 연한 회색으로 변경

        // 포커스 리스너 설정
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                lineColor = MyColor.INSTANCE.getDARK_RED();  // 포커스가 되었을 때 색상 변경
                repaint();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                lineColor = Color.GRAY;  // 포커스가 없을 때 기본 색상으로 변경
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

            private void checkText() {
                if (getPassword().length == 0) {
                    labelText = "가맹점 웹 비밀번호";  // 입력이 없으면 라벨 표시
                } else {
                    labelText = "";  // 입력이 있으면 라벨 숨김
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

        int borderThickness = 2;
        int arcSize = 10;
        int x = borderThickness / 2;
        int y = borderThickness / 2;
        int width = getWidth() - borderThickness;
        int height = getHeight() - borderThickness;

        // 테두리 안쪽에 맞춘 둥근 배경 그리기
        g2.fill(new RoundRectangle2D.Double(x, y, width, height, arcSize, arcSize));

        // 입력 텍스트의 폰트 크기 변경 (20px)
        setFont(MyFont.INSTANCE.Bold(30f));

        super.paintComponent(grphcs);

        // 힌트 텍스트와 스타일 처리
        createHintText(g2);
    }

    @Override
    protected void paintBorder(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(lineColor);

        int borderThickness = 2;
        int arcSize = 10;
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
        g2.setFont(MyFont.INSTANCE.Regular(30));  // regularFont에 20 크기 적용

        FontMetrics fm = g2.getFontMetrics();
        double textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2.0;

        int textX = 20;  // 고정된 X 좌표로 설정하여 오른쪽으로 이동

        g2.drawString(labelText, textX, (int) textY);  // 수직 중앙에 라벨 텍스트 배치
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 20, 0, 0);  // 텍스트의 위치를 20만큼 오른쪽으로 이동시키기 위해 여백 설정
    }
}
