package com.chitieu.dao;

import com.chitieu.model.GiaoDich;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Đọc / ghi danh sách thu chi vào file data/giaodich.txt
 * (mỗi giao dịch 1 dòng, các cột cách nhau bởi dấu |).
 *
 * Giao diện KHÔNG đọc file trực tiếp mà luôn gọi qua lớp này.
 * NÂNG CẤP lên database: chỉ cần viết lại lớp này, giao diện giữ nguyên.
 */
public class LuuTruGiaoDich {
    private static final Path FILE = Paths.get("data", "giaodich.txt");
    private final List<GiaoDich> list = new ArrayList<>();

    public LuuTruGiaoDich() {
        load();
        if (list.isEmpty()) {   // lần chạy đầu tiên: tạo dữ liệu mẫu để demo
            createSampleData();
            save();
        }
    }

    // ================== Thêm / sửa / xóa ==================

    public List<GiaoDich> getAll() {
        return list;
    }

    public void add(GiaoDich t) {
        int maxId = 0;
        for (GiaoDich x : list) maxId = Math.max(maxId, x.getId());
        t.setId(maxId + 1);
        list.add(t);
        save();
    }

    public void update(GiaoDich t) {
        save();   // đã sửa trực tiếp trên object, chỉ cần lưu lại file
    }

    public void delete(GiaoDich t) {
        list.remove(t);
        save();
    }

    // ================== Đọc / ghi file ==================

    private void load() {
        if (!Files.exists(FILE)) return;
        try {
            for (String line : Files.readAllLines(FILE)) {
                if (line.isBlank()) continue;
                String[] f = line.split("\\|", -1);
                GiaoDich t = new GiaoDich();
                t.setId(Integer.parseInt(f[0]));
                t.setDate(LocalDate.parse(f[1]));
                t.setIncome(Boolean.parseBoolean(f[2]));
                t.setCategory(f[3]);
                t.setAmount(Long.parseLong(f[4]));
                t.setNote(f[5]);
                list.add(t);
            }
        } catch (Exception e) {
            System.out.println("Lỗi đọc file dữ liệu: " + e.getMessage());
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (GiaoDich t : list) {
            lines.add(t.getId() + "|" + t.getDate() + "|" + t.isIncome() + "|"
                    + clean(t.getCategory()) + "|" + t.getAmount() + "|" + clean(t.getNote()));
        }
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.out.println("Lỗi lưu file dữ liệu: " + e.getMessage());
        }
    }

    /** Bỏ dấu | và xuống dòng để không làm hỏng file */
    private String clean(String text) {
        return text == null ? "" : text.replace("|", "/").replace("\n", " ");
    }

    // ================== Dữ liệu mẫu ==================

    private void createSampleData() {
        LocalDate today = LocalDate.now();
        LocalDate month = today.withDayOfMonth(1);          // ngày 1 tháng này
        LocalDate lastMonth = month.minusMonths(1);         // ngày 1 tháng trước

        sample(lastMonth.plusDays(4), true, "Lương / trợ cấp", 4000000, "Tiền gia đình gửi");
        sample(lastMonth.plusDays(6), false, "Nhà ở", 1500000, "Tiền phòng trọ");
        sample(lastMonth.plusDays(10), false, "Ăn uống", 900000, "Đi chợ cả tháng");
        sample(lastMonth.plusDays(20), false, "Mua sắm", 450000, "Áo khoác");

        sample(month, true, "Lương / trợ cấp", 4000000, "Tiền gia đình gửi");
        sample(month.plusDays(1), true, "Làm thêm", 1200000, "Gia sư");
        sample(month.plusDays(2), false, "Nhà ở", 1500000, "Tiền phòng trọ");
        sample(month.plusDays(3), false, "Ăn uống", 85000, "Ăn trưa với bạn");
        sample(month.plusDays(5), false, "Đi lại", 120000, "Đổ xăng");
        sample(month.plusDays(7), false, "Học tập", 250000, "Mua giáo trình");
        sample(month.plusDays(9), false, "Ăn uống", 60000, "Trà sữa");
        sample(month.plusDays(12), false, "Giải trí", 180000, "Xem phim");
        sample(today, false, "Ăn uống", 45000, "Bún chả");
    }

    private void sample(LocalDate date, boolean income, String category, long amount, String note) {
        if (date.isAfter(LocalDate.now())) date = LocalDate.now();   // không tạo giao dịch ở tương lai
        GiaoDich t = new GiaoDich();
        t.setId(list.size() + 1);
        t.setDate(date);
        t.setIncome(income);
        t.setCategory(category);
        t.setAmount(amount);
        t.setNote(note);
        list.add(t);
    }
}
