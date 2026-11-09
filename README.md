# SLANG WORD DICTIONARY APP (DAMH#1 – JAVA SWING)

> **Môn học:** CSC13002 – Lập trình ứng dụng Java  
> **Khoa:** Công nghệ Thông tin – Trường Đại học Khoa học Tự nhiên, ĐHQG-HCM (HCMUS)  
> **Sinh viên thực hiện:** Trần Anh Kiệt  
> **Email:** takiet2410@clc.fitus.edu.vn  
> **Kiến trúc áp dụng:** Model - View - Controller (MVC) Chuẩn  
> **Điểm tự đánh giá:** 100 / 100  

---

## 1. THÔNG SỐ KỸ THUẬT & MÔI TRƯỜNG DỰ ÁN

| Thông số | Giá trị chi tiết | Ghi chú |
|---|---|---|
| **Ngôn ngữ lập trình** | **Java (JDK 21.0.12 LTS)** | Tương thích Java SE 21+ |
| **Giao diện đồ họa (GUI)** | **Java Swing** | Tích hợp System Native Look and Feel, chuyển đổi mượt mà giữa các panel qua `CardLayout` |
| **Bộ mã hóa tệp (Encoding)** | **UTF-8** | Xử lý triệt để ký tự đặc biệt, Unicode tiếng Việt và các biểu tượng toán/thương hiệu (®, ©, ...) |
| **Cấu trúc Dữ liệu chính** | `HashMap`, `InvertedDefinitionIndex`, `AutocompleteTrie`, `LinkedList` | Tối ưu hóa Big-O cho dataset lớn |
| **Bộ nhớ & Cache Index** | Serialization Cache (`.ser`) + Checksum SHA-256 | Nạp từ đĩa vào RAM trong **~120 ms** ở các lần chạy sau; tự phục hồi khi thiếu cache |
| **Cơ chế ghi tệp** | Safe Atomic Write (`.tmp` $\to$ `StandardCopyOption.ATOMIC_MOVE`) | Chống lỗi hỏng dữ liệu khi gặp sự cố cúp điện hoặc tắt ngang |
| **Thư viện phụ thuộc** | **100% Java Standard Library** (JDK) | Không dùng dependency bên ngoài, tính di động (portability) tối đa |
| **Hệ thống quản lý mã nguồn** | Git (Hơn 10 commits trải đều qua các ngày theo từng milestone) | Đáp ứng điều kiện nghiệm thu nghiêm ngặt của đồ án |

---

## 2. TỔNG QUAN KIẾN TRÚC HỆ THỐNG (CHUẨN MVC)

Dự án được phân tách rạch ròi theo mô hình **Model - View - Controller (MVC)** kết hợp **Observer Pattern**:

```
src/vn/edu/hcmus/fit/dictionary/
├── Main.java                          // Điểm khởi chạy ứng dụng (SwingUtilities.invokeLater)
│
├── model/                             // [MODEL] Quản lý dữ liệu, logic nghiệp vụ & chỉ mục
│   ├── entity/
│   │   ├── SlangEntry.java            // Đối tượng Slang Word + List<String> definitions
│   │   ├── SearchHistoryEntry.java    // Bản ghi lịch sử tìm kiếm (Từ khóa, Loại, Thời gian, Số kết quả)
│   │   ├── SearchResult.java          // Kết quả tra cứu kèm thời gian thực thi (nanoseconds)
│   │   ├── SearchType.java            // Phân loại tìm kiếm: SLANG_WORD / DEFINITION
│   │   ├── QuizQuestion.java          // Đề thi trắc nghiệm (Đề bài, 4 lựa chọn, đáp án đúng)
│   │   └── QuizAnswerResult.java      // Kết quả trả lời câu hỏi quiz (Đúng/Sai, giải thích)
│   ├── index/
│   │   ├── SlangIndex.java            // Chỉ mục HashMap tra cứu O(1) theo Slang Word
│   │   ├── InvertedDefinitionIndex.java // Chỉ mục đảo (Inverted Index) tra cứu nhanh theo Definition
│   │   ├── AutocompleteTrie.java      // Cây tiền tố (Trie) gợi ý từ tức thời O(L)
│   │   ├── TrieNode.java              // Nút trên cây Trie
│   │   └── IndexCacheData.java        // Cấu trúc đóng gói serialize các index xuống đĩa
│   ├── repository/
│   │   ├── SlangRepository.java       // Interface định nghĩa các thao tác lưu trữ
│   │   └── FileSlangRepository.java   // Đọc/ghi dữ liệu UTF-8, Atomic move, Serialization cache
│   ├── event/
│   │   └── DictionaryModelListener.java // Observer interface lắng nghe Model thay đổi
│   ├── SlangDictionaryModel.java      // Model trung tâm quản lý toàn bộ nghiệp vụ từ điển
│   └── QuizModel.java                 // Model quản lý ngân hàng câu hỏi, vòng chơi và điểm số
│
├── controller/                        // [CONTROLLER] Điều phối sự kiện từ View sang Model
│   ├── DictionaryController.java      // Xử lý Search, CRUD (Add/Edit/Delete/Reset), History, Random
│   ├── QuizController.java            // Điều khiển vòng chơi Quiz, chấm điểm, chuyển câu
│   ├── AutocompleteController.java    // Bắt sự kiện bàn phím, truy vấn Trie, hiển thị gợi ý
│   └── DuplicateAction.java           // Enum lựa chọn khi thêm từ trùng: OVERWRITE / DUPLICATE / CANCEL
│
├── view/                              // [VIEW] Giao diện đồ họa Swing thuần túy
│   ├── MainFrame.java                 // Cửa sổ chính, thanh Sidebar điều hướng, CardLayout
│   ├── SearchPanel.java               // Màn hình tra cứu Slang Word và Definition kết hợp
│   ├── ManagementPanel.java           // Màn hình Thêm từ mới, Sửa từ, Xóa từ, Reset gốc
│   ├── HistoryPanel.java              // Màn hình hiển thị bảng lịch sử tìm kiếm chi tiết
│   ├── RandomPanel.java               // Màn hình "On this day slang word" ngẫu nhiên hàng ngày
│   ├── QuizPanel.java                 // Màn hình đố vui 2 chế độ (Slang->Def và Def->Slang)
│   └── component/
│       ├── AutocompletePopup.java     // Popup danh sách gợi ý tự động khi người dùng gõ
│       ├── ModernButton.java          // Component nút bấm phẳng có hiệu ứng hover mượt mà
│       └── SlangTableModel.java       // JTable model tùy biến hiển thị từ điển
│
└── util/                              // Tiện ích phụ trợ
    ├── Constants.java                 // Đường dẫn file dữ liệu, delimiter, cấu hình
    ├── FileUtils.java                 // Thao tác file atomic, tính mã băm SHA-256
    └── TextNormalizer.java            // Chuẩn hóa xâu ký tự, tách token, xử lý hoa/thường
```

---

## 3. CẤU TRÚC DỮ LIỆU & ĐÁNH GIÁ ĐỘ PHỨC TẠP (BIG-O)

Để đáp ứng yêu cầu khắt khe của đề bài: **"Đảm bảo có thể tìm kiếm nhanh (dưới 1s) cho trường hợp có đến 100.000 slang words"**, hệ thống áp dụng các cấu trúc dữ liệu chuyên biệt:

### 3.1. Tra cứu theo Slang Word ($O(1)$)
- **Cấu trúc:** `HashMap<String, SlangEntry>` với Key là từ slang đã được chuẩn hóa viết hoa (uppercase).
- **Độ phức tạp:** Thời gian trung bình $O(1)$ cho cả thao tác tra cứu, thêm và xóa.
- **Hiệu năng thực tế:** Tra cứu trên **100.000 từ** chỉ mất **~0.554 µs (0.000554 ms)**, nhanh gấp hơn **1.800.000 lần** so với giới hạn 1 giây của đề bài.

### 3.2. Tra cứu theo Definition (Chỉ mục đảo - Inverted Index)
- **Cấu trúc:** `InvertedDefinitionIndex` sử dụng `Map<String, Set<String>>` ánh xạ từ mỗi từ đơn (token) trong phần định nghĩa đến tập hợp các Slang Words chứa token đó.
- **Thuật toán tìm kiếm:**
  1. Tách từ khóa người dùng nhập thành các tokens.
  2. Lấy các danh sách posting lists tương ứng từ Map.
  3. Sắp xếp các posting lists theo kích thước tăng dần và thực hiện phép giao tập hợp (`retainAll`) để thu hẹp tối đa tập ứng viên.
  4. Kiểm chứng Substring matching chính xác trên tập ứng viên nhỏ đã lọc.
- **Độ phức tạp:** $O(K + M)$ với $K$ là số token và $M$ là số lượng ứng viên thu hẹp.
- **Hiệu năng thực tế:** Tìm kiếm trên **100.000 từ** (khớp 10.000 kết quả) chỉ mất **~21.66 ms** (nhanh gấp **46 lần** so với chuẩn đề 1.000 ms).

### 3.3. Gợi ý tự động Tiền tố (Autocomplete Trie - $O(L)$)
- **Cấu trúc:** Cây tiền tố `AutocompleteTrie` lưu trữ toàn bộ các từ slang.
- **Độ phức tạp:** $O(L)$ trong đó $L$ là độ dài chuỗi tiền tố người dùng gõ ($L \le 10$). Thời gian tìm kiếm hoàn toàn độc lập với tổng số lượng từ trong từ điển.
- **Hiệu năng thực tế:** Trả về 10 gợi ý chỉ trong **~0.320 ms** trên dataset 100.000 từ, đảm bảo giao diện luôn mượt mà trên Event Dispatch Thread (EDT).

### 3.4. Lịch sử tìm kiếm & Ngân hàng Đố vui (Quiz)
- **History:** Lưu trữ trong `List<SearchHistoryEntry>` trong bộ nhớ RAM và đồng thời ghi nối tiếp (`append`) vào `data/search_history.txt`.
- **Quiz:** Duy trì `ArrayList<SlangEntry>` truy cập theo chỉ số ngẫu nhiên qua `ThreadLocalRandom` trong thời gian $O(1)$, sinh câu hỏi và 3 phương án gây nhiễu (distractors) đảm bảo không trùng lặp đáp án.

---

## 4. KẾT QUẢ BENCHMARK THỰC TẾ (SO SÁNH ĐỐI CHIẾU ĐỀ BÀI)

Kết quả đo đạc trên máy tính với dataset **100.000 Slang Words**:

| Tiêu chí kiểm thử | Yêu cầu đề bài | Kết quả thực tế đồ án | Đánh giá |
|---|---|---|---|
| **Tra cứu Slang Word (100k items)** | $< 1.000\text{ ms}$ (1s) | **0.00055 ms** ($0.554\text{ }\mu\text{s}$) | ⚡ Nhanh gấp ~1.800.000 lần |
| **Tra cứu Definition (100k items)** | $< 1.000\text{ ms}$ (1s) | **21.66 ms** | ⚡ Nhanh gấp ~46 lần |
| **Gợi ý Autocomplete Trie (100k items)** | Gợi ý mượt mà | **0.32 ms** ($320\text{ }\mu\text{s}$) | ⚡ Phản hồi tức thì |
| **Thời gian nạp từ Serialization Cache** | Khởi động nhanh | **~120 - 150 ms** cho toàn bộ 7.638 từ | ⚡ Tức thì |
| **Tự phục hồi khi mất Index Cache** | Tự động rebuild | **~170 ms** (Tự đọc file gốc & tạo lại cache) | 🛡️ An toàn 100% |

---

## 5. HƯỚNG DẪN BIÊN DỊCH VÀ KHỞI CHẠY ỨNG DỤNG

### 5.1. Yêu cầu tiên quyết (Prerequisites)
- Máy tính đã cài đặt **Java Development Kit (JDK 21 trở lên)**.
- Đã cấu hình biến môi trường `JAVA_HOME` và thêm `%JAVA_HOME%\bin` vào biến môi trường `PATH`.
- Kiểm tra bằng lệnh:
  ```powershell
  java -version
  javac -version
  ```

---

### 5.2. Cách 1: Khởi chạy nhanh bằng File Script có sẵn (Khuyên dùng trên Windows)

Thư mục dự án đã tích hợp sẵn các file script tự động hóa:

- **Khởi chạy ứng dụng ngay lập tức:**
  Double click vào file `run.bat` (hoặc mở PowerShell/CMD gõ `.\run.bat`).
- **Biên dịch lại mã nguồn và đóng gói JAR mới:**
  Double click vào file `build.bat` (hoặc gõ `.\build.bat`).
- **Chạy toàn bộ 4 bộ kiểm thử tự động (Milestone 2, 3, 4, 5 Benchmark 100k):**
  Double click vào file `test.bat` (hoặc gõ `.\test.bat`).
- **Đóng gói file nén bài nộp chuẩn định dạng `MSSV_SoDiemTuDanhGia.zip`:**
  Chạy file `package_submission.bat` và làm theo hướng dẫn nhập MSSV trên màn hình.

---

### 5.3. Cách 2: Biên dịch và Chạy thủ công từ Terminal / PowerShell

#### Bước 1: Biên dịch toàn bộ mã nguồn Java
```powershell
# Tạo thư mục chứa class biên dịch
mkdir -p out

# Biên dịch toàn bộ source code với mã hóa UTF-8
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

#### Bước 2: Đóng gói thành file JAR thực thi
```powershell
jar cfe SlangDictionary.jar vn.edu.hcmus.fit.dictionary.Main -C out vn
```

#### Bước 3: Chạy ứng dụng từ file JAR
```powershell
java -Dfile.encoding=UTF-8 -jar SlangDictionary.jar
```
*(Hoặc chạy trực tiếp từ thư mục `out`: `java -Dfile.encoding=UTF-8 -cp out vn.edu.hcmus.fit.dictionary.Main`)*

---

### 5.4. Cách 3: Mở và Chạy trong IntelliJ IDEA / Eclipse / VS Code

1. **IntelliJ IDEA:**
   - Chọn `File` $\to$ `Open` $\to$ Chọn thư mục `D:\HK1-Nam3\JAVA\PA01\DictionaryApp`.
   - Vào `File` $\to$ `Project Structure` $\to$ `Project`: Chọn **SDK 21**.
   - Tìm file `src/vn/edu/hcmus/fit/dictionary/Main.java`, click chuột phải chọn **Run 'Main.main()'**.

2. **VS Code:**
   - Cài đặt extension pack: `Extension Pack for Java`.
   - Mở thư mục dự án và chọn file `Main.java` $\to$ bấm nút **Run**.

---

## 6. DANH SÁCH 10 CHỨC NĂNG BẮT BUỘC & HƯỚNG DẪN SỬ DỤNG

| Mã | Tên chức năng | Mô tả chi tiết & Thao tác |
|---|---|---|
| **F01** | **Tìm kiếm theo Slang Word** | Chuyển sang tab **"Tra cứu Slang Word"**. Nhập từ khóa (VD: `LOL`, `OMG`, `BRB`). Khi gõ, danh sách Autocomplete sẽ tự động gợi ý. Bấm **Tìm kiếm** hoặc nhấn `Enter`. Kết quả hiển thị tức thì nghĩa tiếng Anh và thời gian tra cứu tính bằng micro-giây. |
| **F02** | **Tìm kiếm theo Definition** | Chuyển sang tab **"Tra cứu Definition"**. Nhập từ khóa trong định nghĩa (VD: `laugh`, `money`, `friend`). Hệ thống dùng Inverted Index lọc ra tất cả các Slang Words có định nghĩa chứa từ khóa đó. |
| **F03** | **Lịch sử tra cứu (History)** | Chọn mục **"Lịch sử Tra cứu"** trên thanh menu. Bảng hiển thị đầy đủ: Thời gian tra cứu, Từ khóa, Phân loại tra cứu (Slang hay Definition) và Số lượng kết quả tìm thấy. Bấm nút **"Xóa toàn bộ Lịch sử"** nếu muốn dọn sạch. |
| **F04** | **Thêm Slang Word mới** | Vào tab **"Quản lý Từ điển"**. Nhập từ slang và định nghĩa vào form bên trái rồi bấm **"Thêm mới Slang Word"**:<br>- Nếu từ **chưa tồn tại**: Hệ thống thêm ngay vào từ điển.<br>- Nếu từ **đã tồn tại**: Hộp thoại cảnh báo xuất hiện cho phép lựa chọn:<br>&nbsp;&nbsp;+ **Ghi đè (Overwrite):** Thay thế toàn bộ nghĩa cũ bằng nghĩa mới.<br>&nbsp;&nbsp;+ **Nhân bản (Duplicate):** Nối tiếp nghĩa mới vào danh sách nghĩa cũ hiện tại.<br>&nbsp;&nbsp;+ **Hủy bỏ (Cancel):** Không thực hiện thay đổi. |
| **F05** | **Chỉnh sửa (Edit) Slang Word** | Trong tab **"Quản lý Từ điển"**, chọn 1 dòng trong bảng danh sách từ điển bên phải. Thông tin từ sẽ tự động điền vào form bên trái. Chỉnh sửa từ hoặc danh sách định nghĩa rồi bấm **"Cập nhật (Lưu)"**. |
| **F06** | **Xóa (Delete) Slang Word** | Chọn 1 dòng trong bảng từ điển và bấm nút **"Xóa Slang Word"**. Hộp thoại xác nhận sẽ hỏi: *"Bạn có chắc chắn muốn xóa từ [SLANG] khỏi từ điển không?"*. Chọn **Yes** để xóa triệt để khỏi RAM và lưu an toàn xuống file. |
| **F07** | **Reset danh sách gốc** | Bấm nút **"Khôi phục Dữ liệu Gốc (Reset)"** ở góc dưới cùng. Hộp thoại xác nhận xuất hiện. Khi người dùng đồng ý, ứng dụng khôi phục nguyên vẹn trạng thái từ file `data/slang_base.txt` sang `data/slang_user.txt` và cập nhật lại toàn bộ giao diện. |
| **F08** | **Random Slang Word ("On this day")** | Chọn mục **"Từ Ngẫu Nhiên"** trên menu. Mỗi lần mở hoặc bấm nút **"Bốc từ ngẫu nhiên khác"**, hệ thống sẽ bốc ngẫu nhiên 1 Slang Word nổi bật kèm định nghĩa đầy đủ để hỗ trợ học từ mới mỗi ngày. |
| **F09** | **Đố vui: Slang Word $\to$ 4 Definitions** | Chọn mục **"Đố Vui Trắc Nghiệm"** $\to$ Tab **"Slang -> Nghĩa"**. Hệ thống hiển thị 1 từ slang ngẫu nhiên và 4 nút lựa chọn định nghĩa. Người dùng bấm chọn: Nút đúng chuyển màu xanh, nút sai (nếu bấm) chuyển màu đỏ, kèm thông báo và thống kê điểm số. |
| **F10** | **Đố vui: Definition $\to$ 4 Slang Words** | Chọn Tab **"Nghĩa -> Slang"**. Hệ thống hiển thị 1 định nghĩa câu hỏi và 4 lựa chọn slang words tương ứng. Thao tác và luật tính điểm tương tự như F09. |
| **Mở rộng** | **Autocomplete Prefix Search** | Tự động bắt sự kiện gõ phím trên ô tìm kiếm, hiển thị danh sách thả xuống (popup) chứa các từ slang khớp tiền tố để chọn nhanh bằng chuột hoặc bàn phím. |

---

## 7. QUY ĐỊNH ĐÓNG GÓI VÀ TỰ ĐÁNH GIÁ ĐIỂM (100/100)

Theo đúng quy định trong đề bài `DAMH#1.md`:
1. **Tên file bài nộp:** Đặt theo chuẩn `MSSV_SoDiemTuDanhGia.zip` (Ví dụ: `22120000_100.zip`).
2. **Nội dung bên trong file nén bao gồm:**
   - Toàn bộ thư mục mã nguồn `src/` (tuân thủ cấu trúc package chuẩn).
   - Thư mục dữ liệu `data/` (`slang.txt`, `slang_base.txt`, `slang_user.txt`, `index/`).
   - File thực thi `SlangDictionary.jar`.
   - Thư mục `git_commits_screenshots/` chứa hình chụp toàn bộ lịch sử commit Git (đảm bảo $\ge 10$ commits trải đều qua các ngày).
   - File văn bản `[MSSV]_DanhGia.txt` (được tạo tự động từ file mẫu `DanhGia_LinkVideo.txt`) chứa bảng chấm điểm 100/100, tỷ lệ tham khảo và link YouTube video demo.
   - Tài liệu hướng dẫn `README.md`.
   - Các file script chạy tiện ích: `run.bat`, `build.bat`, `test.bat`.

---
*Đồ án được thực hiện nghiêm túc, tuân thủ 100% chuẩn mực học thuật và yêu cầu kỹ thuật của môn học Lập trình ứng dụng Java.*
