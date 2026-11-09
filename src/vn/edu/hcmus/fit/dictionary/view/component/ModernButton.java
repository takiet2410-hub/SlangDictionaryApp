package vn.edu.hcmus.fit.dictionary.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Nút bấm hiện đại (ModernButton) tự vẽ (custom-painted) với hiệu ứng đổi màu mượt mà:
 * - Khắc phục triệt để lỗi màu chữ bị trùng/chìm của Windows Look and Feel.
 * - Hỗ trợ phân biệt rõ ràng 3 trạng thái: Bình thường (Normal), Rê chuột (Hover), và Đang chọn/Ấn (Active/Pressed).
 * - Tự động kích hoạt cơ chế Composite Font Fallback (Font.DIALOG) hiển thị trọn vẹn icon Unicode/Emoji và tiếng Việt.
 * - Bo tròn góc mượt mà (Anti-aliased Rounded Corners) và tương phản màu chữ cao, sắc nét.
 */
public class ModernButton extends JButton {

    private Color normalBackground;
    private Color hoverBackground;
    private Color activeBackground;
    private Color normalForeground;
    private Color hoverForeground;
    private Color activeForeground;

    private int cornerRadius = 8;
    private boolean isSidebarNav = false;
    private boolean isActive = false;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, new Color(37, 99, 235), Color.WHITE);
    }

    public ModernButton(String text, Color normalBg, Color normalFg) {
        super(text);
        this.normalBackground = normalBg;
        this.normalForeground = normalFg;
        this.hoverBackground = brighten(normalBg, 0.15f);
        this.activeBackground = darken(normalBg, 0.15f);
        this.hoverForeground = Color.WHITE;
        this.activeForeground = normalFg;

        initButton();
    }

    public ModernButton(String text, Color normalBg, Color hoverBg, Color activeBg, Color normalFg, Color activeFg) {
        this(text, normalBg, hoverBg, activeBg, normalFg, Color.WHITE, activeFg);
    }

    public ModernButton(String text, Color normalBg, Color hoverBg, Color activeBg,
                        Color normalFg, Color hoverFg, Color activeFg) {
        super(text);
        this.normalBackground = normalBg;
        this.hoverBackground = hoverBg;
        this.activeBackground = activeBg;
        this.normalForeground = normalFg;
        this.hoverForeground = hoverFg;
        this.activeForeground = activeFg;

        initButton();
    }

    private void initButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font(Font.DIALOG, Font.BOLD, 13));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setSidebarNav(boolean sidebarNav) {
        this.isSidebarNav = sidebarNav;
        if (sidebarNav) {
            setHorizontalAlignment(SwingConstants.LEFT);
            this.cornerRadius = 6;
        }
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setColors(Color normalBg, Color hoverBg, Color activeBg, Color normalFg, Color activeFg) {
        setColors(normalBg, hoverBg, activeBg, normalFg, Color.WHITE, activeFg);
    }

    public void setColors(Color normalBg, Color hoverBg, Color activeBg,
                          Color normalFg, Color hoverFg, Color activeFg) {
        this.normalBackground = normalBg;
        this.hoverBackground = hoverBg;
        this.activeBackground = activeBg;
        this.normalForeground = normalFg;
        this.hoverForeground = hoverFg;
        this.activeForeground = activeFg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Xác định màu nền và màu chữ theo trạng thái
        Color currentBg;
        Color currentFg;

        if (isActive || isPressed) {
            currentBg = activeBackground;
            currentFg = activeForeground;
        } else if (isHovered && isEnabled()) {
            currentBg = hoverBackground;
            currentFg = (hoverForeground != null) ? hoverForeground : activeForeground;
        } else {
            currentBg = normalBackground;
            currentFg = normalForeground;
        }

        if (!isEnabled()) {
            currentBg = new Color(220, 225, 230);
            currentFg = new Color(150, 160, 170);
        }

        // 2. Vẽ nền bo góc
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Nút Sidebar: vẽ viền hoặc dải chỉ báo bên trái
        if (isSidebarNav) {
            if (isActive) {
                // Thanh viền sáng nổi bật bên mép trái khi đang Active
                g2.setColor(new Color(56, 189, 248)); // Xanh Cyan sáng
                g2.fillRoundRect(2, 4, 4, height - 8, 2, 2);
            } else {
                // Đường viền tinh tế lúc chưa ấn để không bao giờ bị chìm/trùng màu nền
                g2.setColor(new Color(42, 60, 86));
                g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
            }
        }

        // 3. Vẽ chữ và icon với Composite Fallback Font tránh lỗi ô vuông []
        Font font = getFont();
        if (font == null || font.getName().equalsIgnoreCase("Segoe UI")) {
            font = new Font(Font.DIALOG, font != null ? font.getStyle() : Font.BOLD, font != null ? font.getSize() : 13);
        }
        g2.setFont(font);
        g2.setColor(currentFg);

        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        if (text != null && !text.isEmpty()) {
            int stringWidth = fm.stringWidth(text);
            int textX;
            if (getHorizontalAlignment() == SwingConstants.LEFT) {
                textX = isSidebarNav ? 18 : 14;
            } else {
                textX = (width - stringWidth) / 2;
            }
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, textX, textY);
        }

        g2.dispose();
    }

    private static Color brighten(Color color, float fraction) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * fraction));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * fraction));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * fraction));
        return new Color(r, g, b, color.getAlpha());
    }

    private static Color darken(Color color, float fraction) {
        int r = Math.max(0, (int) (color.getRed() * (1 - fraction)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - fraction)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - fraction)));
        return new Color(r, g, b, color.getAlpha());
    }
}
