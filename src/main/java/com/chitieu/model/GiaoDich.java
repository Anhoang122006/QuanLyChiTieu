package com.chitieu.model;

import java.time.LocalDate;

/**
 * Một khoản thu hoặc chi. Lớp này chỉ chứa dữ liệu.
 */
public class GiaoDich {
    private int id;               // mã giao dịch (tự tăng)
    private LocalDate date;       // ngày
    private boolean income;       // true = khoản THU, false = khoản CHI
    private String category;      // danh mục: Ăn uống, Đi lại, Lương...
    private long amount;          // số tiền
    private String note = "";     // ghi chú

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isIncome() { return income; }
    public void setIncome(boolean income) { this.income = income; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
