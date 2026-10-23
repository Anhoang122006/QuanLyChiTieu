# Quản lý chi tiêu cá nhân (Java Swing)

Ghi lại các khoản thu / chi, xem theo tháng, tìm kiếm, tự tính tổng thu, tổng chi và số dư.

## Cách chạy trong IntelliJ
1. File → Open → chọn thư mục `QuanLyChiTieu` (thư mục có file `pom.xml`).
2. Mở `src/main/java/com/chitieu/Main.java` → bấm tam giác xanh cạnh `main` → Run.

Dữ liệu lưu ở `data/giaodich.txt` (tự tạo khi chạy lần đầu, có sẵn dữ liệu mẫu).

## Cấu trúc code
```
com.chitieu
├── Main.java                  Chạy chương trình (hàm main)
├── model
│   └── GiaoDich.java          Một khoản thu / chi (ngày, loại, danh mục, số tiền, ghi chú)
├── dao
│   └── LuuTruGiaoDich.java    Đọc / ghi dữ liệu xuống file
├── ui
│   ├── ManHinhChinh.java      Màn hình chính: bảng + thống kê + nút
│   ├── FormThemSua.java       Hộp thoại thêm / sửa một khoản
│   ├── LichThang.java         Lịch tháng: xem lịch sử thu chi theo từng ngày
│   ├── BangGiaoDich.java      Quy định các cột của bảng
│   ├── GiaoDien.java          Màu sắc, font chữ, kiểu nút
│   ├── KhungBoGoc.java        Khung trắng bo góc
│   └── Logo.java              Logo chữ ₫
└── util
    └── DinhDang.java          Định dạng tiền (250.000 đ) và ngày (dd/MM/yyyy)
```
Ba thư mục `model` – `dao` – `ui` là cách chia chuẩn: dữ liệu, lưu trữ, giao diện tách riêng.

## Gợi ý nâng cấp
| Tính năng | Sửa ở đâu |
|---|---|
| Thêm danh mục mới | `FormThemSua` – mảng `EXPENSE_CATEGORIES` / `INCOME_CATEGORIES` |
| Đặt hạn mức chi mỗi tháng, vượt thì báo đỏ | `ManHinhChinh.refresh()` |
| Biểu đồ tròn chi tiêu theo danh mục | Thêm thư viện JFreeChart |
| Xuất file Excel / CSV | Thêm nút, ghi file từ `dao.getAll()` |
| Đăng nhập nhiều người dùng | Thêm lớp `NguoiDung` + màn hình đăng nhập |
| Lưu bằng database | Chỉ viết lại `LuuTruGiaoDich` |
| Chế độ tối | `FlatDarkLaf.setup()` thay cho `FlatLightLaf` trong `GiaoDien` |
