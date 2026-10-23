package com.chitieu.ui;

import com.chitieu.dao.LuuTruGiaoDich;
import com.chitieu.model.GiaoDich;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.YearMonth;
import java.util.function.Consumer;

/**
 * Lịch chọn tháng (chỉ có 12 tháng, không có ngày).
 *
 *        ◀    Năm 2026    ▶
 *     [T1]  [T2]  [T3]  [T4]
 *     [T5]  [T6]  [T7]  [T8]
 *     [T9]  [T10] [T11] [T12]
 *        [ Tất cả các tháng ]
 *
 * - Tháng có dữ liệu thu chi: chữ đậm.
 * - Tháng đang chọn: nền xanh, chữ trắng.
 * - Bấm vào 1 tháng -> báo cho màn hình chính lọc theo tháng đó.
 */
public class ChonThang extends JPopupMenu {
    private final LuuTruGiaoDich dao;
    private final YearMonth thangDangChon;          // null = đang xem tất cả các tháng
    private final Consumer<YearMonth> khiChonThang; // việc cần làm khi chọn xong (null = tất cả)
    private int namDangXem;

    private final JButton btnNam = new JButton();
    private final JPanel luoiThang = new JPanel(new GridLayout(3, 4, 6, 6));   // 3 hàng x 4 cột = 12 tháng

    public ChonThang(LuuTruGiaoDich dao, YearMonth thangDangChon, Consumer<YearMonth> khiChonThang) {
        this.dao = dao;
        this.thangDangChon = thangDangChon;
        this.khiChonThang = khiChonThang;
        this.namDangXem = thangDangChon != null ? thangDangChon.getYear() : YearMonth.now().getYear();

        // ----- hàng trên: ◀ Năm 2026 ▶ -----
        JButton btnTruoc = GiaoDien.button("◀");
        JButton btnSau = GiaoDien.button("▶");
        btnTruoc.addActionListener(e -> { namDangXem--; veLai(); });
        btnSau.addActionListener(e -> { namDangXem++; veLai(); });
        btnNam.setFont(GiaoDien.bold(15));
        btnNam.setBorderPainted(false);
        btnNam.setContentAreaFilled(false);
        btnNam.setFocusable(false);

        JPanel hangTren = new JPanel(new BorderLayout(8, 0));
        hangTren.setOpaque(false);
        hangTren.add(btnTruoc, BorderLayout.WEST);
        hangTren.add(btnNam, BorderLayout.CENTER);
        hangTren.add(btnSau, BorderLayout.EAST);

        // ----- hàng dưới: Tất cả các tháng -----
        JButton btnTatCa = thangDangChon == null
                ? GiaoDien.primaryButton("Tất cả các tháng")
                : GiaoDien.button("Tất cả các tháng");
        btnTatCa.addActionListener(e -> chon(null));

        luoiThang.setOpaque(false);

        JPanel noiDung = new JPanel(new BorderLayout(0, 10));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        noiDung.add(hangTren, BorderLayout.NORTH);
        noiDung.add(luoiThang, BorderLayout.CENTER);
        noiDung.add(btnTatCa, BorderLayout.SOUTH);
        noiDung.setPreferredSize(new Dimension(320, 240));
        add(noiDung);

        veLai();
    }

    /** Vẽ lại 12 nút tháng của năm đang xem */
    private void veLai() {
        btnNam.setText("Năm " + namDangXem);
        luoiThang.removeAll();
        for (int thang = 1; thang <= 12; thang++) {
            YearMonth ym = YearMonth.of(namDangXem, thang);
            JButton btn = ym.equals(thangDangChon)
                    ? GiaoDien.primaryButton("T" + thang)
                    : GiaoDien.button("T" + thang);
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            // tháng có dữ liệu thì chữ đậm, tháng trống thì chữ thường màu xám
            if (!ym.equals(thangDangChon)) {
                boolean coDuLieu = coGiaoDich(ym);
                btn.setFont(coDuLieu ? GiaoDien.bold(14) : btn.getFont().deriveFont(Font.PLAIN));
                btn.setForeground(coDuLieu ? GiaoDien.TEXT : GiaoDien.MUTED);
            }
            btn.addActionListener(e -> chon(ym));
            luoiThang.add(btn);
        }
        luoiThang.revalidate();
        luoiThang.repaint();
    }

    /** Tháng này có khoản thu chi nào không */
    private boolean coGiaoDich(YearMonth ym) {
        for (GiaoDich gd : dao.getAll()) {
            if (YearMonth.from(gd.getDate()).equals(ym)) return true;
        }
        return false;
    }

    private void chon(YearMonth ym) {
        setVisible(false);
        khiChonThang.accept(ym);
    }
}
