package com.chitieu.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.chitieu.model.GiaoDich;
import com.chitieu.util.DinhDang;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Hộp thoại thêm mới / sửa một khoản thu chi.
 * Dữ liệu chỉ được ghi vào giao dịch khi bấm "Lưu" và mọi ô đều hợp lệ.
 */
public class FormThemSua extends JDialog {

    // Danh mục gợi ý. NÂNG CẤP: cho người dùng tự thêm danh mục.
    private static final String[] EXPENSE_CATEGORIES = {
            "Ăn uống", "Đi lại", "Nhà ở", "Học tập", "Mua sắm", "Giải trí", "Sức khỏe", "Khác"};
    private static final String[] INCOME_CATEGORIES = {
            "Lương / trợ cấp", "Làm thêm", "Học bổng", "Được tặng", "Khác"};

    private final GiaoDich t;
    private boolean saved = false;

    // ----- các ô nhập -----
    private final JRadioButton rbExpense = new JRadioButton("Khoản chi");
    private final JRadioButton rbIncome = new JRadioButton("Khoản thu");
    private final JComboBox<String> cbCategory = new JComboBox<>();
    private final JTextField tfAmount = new JTextField(20);
    private final JTextField tfDate = new JTextField();
    private final JTextField tfNote = new JTextField();
    private final JLabel lbError = GiaoDien.label(" ", Font.PLAIN, 13, GiaoDien.DANGER);

    public FormThemSua(JFrame owner, GiaoDich t, boolean isNew) {
        super(owner, isNew ? "Thêm khoản thu chi" : "Sửa khoản thu chi", true);   // true = chặn cửa sổ chính
        this.t = t;

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(BorderFactory.createEmptyBorder(20, 26, 18, 26));
        root.add(GiaoDien.label(isNew ? "Thêm khoản thu chi" : "Sửa khoản thu chi", Font.BOLD, 20, GiaoDien.TEXT),
                BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(root);

        if (isNew) {
            rbExpense.setSelected(true);
            loadCategories();
            tfDate.setText(LocalDate.now().format(DinhDang.DATE));   // mặc định là hôm nay
        } else {
            hienThongTinCu();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() {
        return saved;
    }

    // ===================================================================
    //  Giao diện: mỗi dòng gồm nhãn bên trái, ô nhập bên phải
    // ===================================================================

    private JPanel buildForm() {
        // chọn Thu hoặc Chi -> đổi danh sách danh mục tương ứng
        ButtonGroup group = new ButtonGroup();
        group.add(rbExpense);
        group.add(rbIncome);
        rbExpense.addActionListener(e -> loadCategories());
        rbIncome.addActionListener(e -> loadCategories());
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typePanel.add(rbExpense);
        typePanel.add(javax.swing.Box.createHorizontalStrut(20));
        typePanel.add(rbIncome);

        cbCategory.setEditable(true);   // được chọn danh mục có sẵn hoặc tự gõ
        tfAmount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: 50000");
        tfDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        tfNote.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: Ăn trưa với bạn");

        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, 0, "Loại", typePanel);
        addRow(form, 1, "Danh mục *", cbCategory);
        addRow(form, 2, "Số tiền (VNĐ) *", tfAmount);
        addRow(form, 3, "Ngày *", tfDate);
        addRow(form, 4, "Ghi chú", tfNote);
        return form;
    }

    private void addRow(JPanel form, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 0, 6, 14);
        c.gridx = 0;
        form.add(GiaoDien.label(label, Font.BOLD, 13.5f, GiaoDien.TEXT), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        form.add(field, c);
    }

    private JPanel buildButtons() {
        JButton btnCancel = GiaoDien.button("Hủy");
        JButton btnSave = GiaoDien.primaryButton("Lưu");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        getRootPane().setDefaultButton(btnSave);   // nhấn Enter = Lưu

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(btnCancel);
        buttons.add(btnSave);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(lbError, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.EAST);
        return panel;
    }

    // ===================================================================
    //  Xử lý dữ liệu
    // ===================================================================

    /** Đổ danh mục Thu hoặc Chi vào ô chọn */
    private void loadCategories() {
        cbCategory.removeAllItems();
        String[] items = rbIncome.isSelected() ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;
        for (String item : items) cbCategory.addItem(item);
    }

    /** Hiện thông tin cũ lên form (khi sửa) */
    private void hienThongTinCu() {
        rbIncome.setSelected(t.isIncome());
        rbExpense.setSelected(!t.isIncome());
        loadCategories();
        cbCategory.setSelectedItem(t.getCategory());
        tfAmount.setText(String.valueOf(t.getAmount()));
        tfDate.setText(t.getDate().format(DinhDang.DATE));
        tfNote.setText(t.getNote());
    }

    /** Kiểm tra dữ liệu, hợp lệ thì ghi vào giao dịch và đóng hộp thoại */
    private void save() {
        String category = String.valueOf(cbCategory.getEditor().getItem()).trim();
        long amount = parseMoney(tfAmount.getText());

        if (category.isEmpty()) {
            lbError.setText("Vui lòng chọn hoặc nhập danh mục.");
            return;
        }
        if (amount <= 0) {
            lbError.setText("Số tiền phải là số lớn hơn 0.");
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim(), DinhDang.DATE);
        } catch (DateTimeParseException ex) {
            lbError.setText("Ngày phải có dạng dd/MM/yyyy, VD 20/10/2026.");
            return;
        }

        // Mọi thứ hợp lệ -> ghi vào giao dịch
        t.setIncome(rbIncome.isSelected());
        t.setCategory(category);
        t.setAmount(amount);
        t.setDate(date);
        t.setNote(tfNote.getText().trim());

        saved = true;
        dispose();
    }

    /** "50.000" hoặc "50,000" hoặc "50000" -> 50000. Gõ sai thì trả về -1 */
    private long parseMoney(String text) {
        try {
            return Long.parseLong(text.replace(".", "").replace(",", "").replace(" ", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
