package com.chitieu.ui;

import com.chitieu.model.GiaoDich;
import com.chitieu.util.DinhDang;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * "Cầu nối" giữa danh sách giao dịch và JTable: cho bảng biết có bao nhiêu dòng
 * và mỗi cột hiện gì.
 *
 * NÂNG CẤP: muốn thêm cột thì thêm tên vào COLUMNS và thêm 1 case trong getValueAt.
 */
public class BangGiaoDich extends AbstractTableModel {
    private static final String[] COLUMNS = {"Ngày", "Loại", "Danh mục", "Số tiền", "Ghi chú"};

    private List<GiaoDich> list = new ArrayList<>();

    public void setData(List<GiaoDich> list) {
        this.list = list;
        fireTableDataChanged();   // báo cho bảng vẽ lại
    }

    public GiaoDich getAt(int row) {
        return list.get(row);
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        GiaoDich t = list.get(row);
        switch (column) {
            case 0: return t.getDate().format(DinhDang.DATE);
            case 1: return t.isIncome() ? "Thu" : "Chi";
            case 2: return t.getCategory();
            case 3: return (t.isIncome() ? "+ " : "- ") + DinhDang.money(t.getAmount());
            case 4: return t.getNote();
            default: return "";
        }
    }
}
