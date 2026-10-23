package com.chitieu.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Collections;

/**
 * Toàn bộ màu sắc, font chữ và kiểu nút của ứng dụng nằm ở đây.
 * Muốn đổi màu chủ đạo (ví dụ sang hồng pastel) chỉ cần sửa các màu bên dưới.
 */
public final class GiaoDien {
    private GiaoDien() {}

    public static final Color PRIMARY = new Color(0x3E6B4F);   // xanh lá đậm (màu chính)
    public static final Color BG = new Color(0xF6F4EF);        // nền màu kem
    public static final Color BORDER = new Color(0xE7E3DA);    // màu viền
    public static final Color TEXT = new Color(0x1F2A24);      // chữ chính
    public static final Color MUTED = new Color(0x7A847E);     // chữ phụ màu xám
    public static final Color DANGER = new Color(0xC0392B);    // đỏ (báo lỗi, công nợ)

    /** Gọi 1 lần khi chương trình bắt đầu */
    public static void setup() {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#3E6B4F"));
        FlatLightLaf.setup();

        UIManager.put("defaultFont", UIManager.getFont("defaultFont").deriveFont(14f));
        UIManager.put("Panel.background", BG);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Table.rowHeight", 40);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.gridColor", new Color(0xF0EDE6));
        UIManager.put("Table.selectionBackground", new Color(0xE3EDE6));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TableHeader.height", 38);
        UIManager.put("TableHeader.background", new Color(0xFAF9F6));
    }

    /** Tạo nhãn chữ với cỡ chữ và màu tùy chọn */
    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(UIManager.getFont("defaultFont").deriveFont(style, size));
        l.setForeground(color);
        return l;
    }

    /** Nút chính: nền xanh, chữ trắng */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:#3E6B4F; foreground:#FFFFFF; hoverBackground:#355E45;"
                        + "borderWidth:0; focusWidth:0; font:bold; margin:8,18,8,18");
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Nút phụ: nền trắng, viền xám */
    public static JButton button(String text) {
        JButton b = new JButton(text);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:#FFFFFF; borderColor:#E7E3DA; focusWidth:0; margin:8,16,8,16");
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Nút bật/tắt bo tròn, dùng để lọc trạng thái */
    public static JToggleButton pillButton(String text) {
        JToggleButton b = new JToggleButton(text);
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:999; background:#FFFFFF; borderColor:#E7E3DA;"
                        + "selectedBackground:#3E6B4F; selectedForeground:#FFFFFF;"
                        + "focusWidth:0; margin:5,14,5,14");
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Dùng cho các label chữ đậm */
    public static Font bold(float size) {
        return UIManager.getFont("defaultFont").deriveFont(Font.BOLD, size);
    }
}
