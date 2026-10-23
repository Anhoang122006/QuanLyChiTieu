package com.chitieu;

import com.chitieu.ui.ManHinhChinh;
import com.chitieu.ui.GiaoDien;

import javax.swing.SwingUtilities;

/**
 * Điểm bắt đầu của chương trình.
 * Bấm nút Run (tam giác xanh) cạnh hàm main để chạy.
 */
public class Main {
    public static void main(String[] args) {
        GiaoDien.setup(); // bật giao diện FlatLaf + màu xanh lá
        SwingUtilities.invokeLater(() -> new ManHinhChinh().setVisible(true));
    }
}
