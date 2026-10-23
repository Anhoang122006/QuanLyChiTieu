package com.chitieu.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.chitieu.dao.LuuTruGiaoDich;
import com.chitieu.model.GiaoDich;
import com.chitieu.util.DinhDang;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cửa sổ chính: danh sách thu chi.
 * Gồm: tiêu đề + chọn tháng + ô tìm kiếm + nút thêm, nút lọc Thu/Chi, 3 thẻ thống kê, bảng.
 */
public class ManHinhChinh extends JFrame {
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MM/yyyy");

    private final LuuTruGiaoDich dao = new LuuTruGiaoDich();

    private final BangGiaoDich tableModel = new BangGiaoDich();
    private final JTable table = new JTable(tableModel);
    private final JTextField tfSearch = new JTextField();
    private final JButton btnThang = GiaoDien.button("");     // nút mở lịch chọn tháng
    private YearMonth thangDangChon = YearMonth.now();        // null = xem tất cả các tháng
    private String typeFilter = "Tất cả";   // "Tất cả", "Thu" hoặc "Chi"

    private final JLabel lbIncome = GiaoDien.label("0", Font.BOLD, 22, GiaoDien.PRIMARY);
    private final JLabel lbExpense = GiaoDien.label("0", Font.BOLD, 22, GiaoDien.DANGER);
    private final JLabel lbBalance = GiaoDien.label("0", Font.BOLD, 22, GiaoDien.TEXT);

    public ManHinhChinh() {
        super("Quản lý chi tiêu cá nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.add(buildToolbar(), BorderLayout.NORTH);
        top.add(buildFilters(), BorderLayout.CENTER);
        top.add(buildStats(), BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        body.add(top, BorderLayout.NORTH);
        body.add(buildTable(), BorderLayout.CENTER);
        setContentPane(body);

        refresh();
        setSize(1200, 720);
        setLocationRelativeTo(null);
    }

    // ===================================================================
    //  Các phần của giao diện
    // ===================================================================

    /** Tiêu đề + chọn tháng + ô tìm kiếm + nút thêm */
    private JPanel buildToolbar() {
        JLabel title = GiaoDien.label("Quản lý chi tiêu", Font.BOLD, 24, GiaoDien.TEXT);
        title.setIcon(new Logo(40));
        title.setIconTextGap(12);

        // bấm nút tháng -> hiện lịch 12 tháng ngay bên dưới nút
        btnThang.setPreferredSize(new Dimension(190, 38));
        btnThang.addActionListener(e -> new ChonThang(dao, thangDangChon, thang -> {
            thangDangChon = thang;
            refresh();
        }).show(btnThang, 0, btnThang.getHeight() + 4));

        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm danh mục, ghi chú...");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfSearch.setPreferredSize(new Dimension(240, 38));
        // gõ tới đâu lọc tới đó
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        JButton btnAdd = GiaoDien.primaryButton("+  Thêm khoản");
        btnAdd.addActionListener(e -> themGiaoDich());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(btnThang);
        right.add(tfSearch);
        right.add(btnAdd);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    /** Các nút lọc: Tất cả / Thu / Chi */
    private JPanel buildFilters() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ButtonGroup group = new ButtonGroup();   // chỉ cho chọn 1 nút
        for (String name : new String[]{"Tất cả", "Thu", "Chi"}) {
            JToggleButton b = GiaoDien.pillButton(name);
            b.setSelected(name.equals(typeFilter));
            b.addActionListener(e -> { typeFilter = name; refresh(); });
            group.add(b);
            panel.add(b);
        }
        return panel;
    }

    /** 3 thẻ thống kê */
    private JPanel buildStats() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.add(statCard("TỔNG THU", lbIncome));
        panel.add(statCard("TỔNG CHI", lbExpense));
        panel.add(statCard("CÒN LẠI (SỐ DƯ)", lbBalance));
        return panel;
    }

    private JPanel statCard(String title, JLabel value) {
        KhungBoGoc card = new KhungBoGoc(new BorderLayout(0, 4), 18);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.add(GiaoDien.label(title, Font.BOLD, 12, GiaoDien.MUTED), BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    /** Bảng giao dịch + nút Sửa / Xóa */
    private JPanel buildTable() {
        table.getTableHeader().setFont(GiaoDien.bold(13));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new ColorRenderer());
        int[] widths = {110, 70, 170, 160, 360};   // độ rộng từng cột
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // nhấp đúp vào 1 dòng để sửa
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) suaGiaoDich();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JButton btnEdit = GiaoDien.button("Sửa");
        JButton btnDelete = GiaoDien.button("Xóa");
        btnDelete.setForeground(GiaoDien.DANGER);
        btnEdit.addActionListener(e -> suaGiaoDich());
        btnDelete.addActionListener(e -> xoaGiaoDich());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 4));
        buttons.add(GiaoDien.label("Nhấp đúp vào một dòng để sửa", Font.PLAIN, 13, GiaoDien.MUTED));
        buttons.add(btnEdit);
        buttons.add(btnDelete);

        KhungBoGoc card = new KhungBoGoc(new BorderLayout(), 18);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 12, 8));
        card.add(scroll, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    // ===================================================================
    //  Xử lý
    // ===================================================================

    /** Lọc theo tháng + loại + từ khóa, rồi cập nhật bảng và thống kê */
    private void refresh() {
        btnThang.setText((thangDangChon == null ? "Tất cả các tháng" : "Tháng " + thangDangChon.format(MONTH_FMT)) + "  ▾");
        String keyword = tfSearch.getText().trim().toLowerCase();
        List<GiaoDich> shown = new ArrayList<>();
        long income = 0, expense = 0;

        for (GiaoDich t : dao.getAll()) {
            if (thangDangChon != null && !YearMonth.from(t.getDate()).equals(thangDangChon)) continue;

            String text = (t.getCategory() + " " + t.getNote()).toLowerCase();
            if (!text.contains(keyword)) continue;

            // tổng thu/chi tính theo tháng đang chọn (chưa lọc Thu/Chi)
            if (t.isIncome()) income += t.getAmount();
            else expense += t.getAmount();

            if (typeFilter.equals("Thu") && !t.isIncome()) continue;
            if (typeFilter.equals("Chi") && t.isIncome()) continue;
            shown.add(t);
        }

        shown.sort((a, b) -> b.getDate().compareTo(a.getDate()));   // mới nhất lên đầu
        tableModel.setData(shown);

        long balance = income - expense;
        lbIncome.setText(DinhDang.money(income));
        lbExpense.setText(DinhDang.money(expense));
        lbBalance.setText(DinhDang.money(balance));
        lbBalance.setForeground(balance < 0 ? GiaoDien.DANGER : GiaoDien.TEXT);   // âm tiền thì tô đỏ
    }

    /** Lấy giao dịch đang chọn trong bảng (chưa chọn thì báo và trả về null) */
    private GiaoDich getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước.");
            return null;
        }
        return tableModel.getAt(row);
    }

    private void themGiaoDich() {
        GiaoDich t = new GiaoDich();
        FormThemSua dialog = new FormThemSua(this, t, true);
        dialog.setVisible(true);   // chương trình dừng ở đây tới khi đóng hộp thoại
        if (dialog.isSaved()) {
            dao.add(t);
            refresh();
        }
    }

    private void suaGiaoDich() {
        GiaoDich t = getSelected();
        if (t == null) return;
        FormThemSua dialog = new FormThemSua(this, t, false);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            dao.update(t);
            refresh();
        }
    }

    private void xoaGiaoDich() {
        GiaoDich t = getSelected();
        if (t == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Xóa khoản \"" + t.getCategory() + "\" " + DinhDang.money(t.getAmount()) + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dao.delete(t);
            refresh();
        }
    }

    /** Tô màu: khoản thu chữ xanh, khoản chi chữ đỏ (ở cột Loại và Số tiền) */
    private static class ColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setForeground(GiaoDien.TEXT);
            setFont(table.getFont());

            if (column == 1 || column == 3) {
                boolean income = "Thu".equals(table.getValueAt(row, 1));
                setForeground(income ? GiaoDien.PRIMARY : GiaoDien.DANGER);
                setFont(GiaoDien.bold(14));
            }
            return this;
        }
    }
}
