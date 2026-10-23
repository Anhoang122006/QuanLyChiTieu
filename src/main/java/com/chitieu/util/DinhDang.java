package com.chitieu.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;

/**
 * Định dạng tiền và ngày kiểu Việt Nam.
 */
public final class DinhDang {
    private DinhDang() {}

    /** Định dạng ngày dd/MM/yyyy, ví dụ 20/10/2026 */
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** 250000 -> "250.000 đ" */
    public static String money(long value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", symbols).format(value) + " đ";
    }
}
