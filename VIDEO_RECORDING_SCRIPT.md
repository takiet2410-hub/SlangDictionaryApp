# KỊCH BẢN THOẠI & HƯỚNG DẪN QUAY VIDEO DEMO ĐỒ ÁN (DAMH#1)
> **Môn học:** CSC13002 – Lập trình ứng dụng Java (FIT - HCMUS)  
> **Đề tài:** Đồ án #1 – Slang Word Dictionary  
> **Sinh viên:** Trần Anh Kiệt (takiet2410@clc.fitus.edu.vn)  
> **Thời lượng khuyến nghị:** 4 - 6 phút (ngắn gọn, cô đọng, đúng trọng tâm)  
> **Tiêu chuẩn video:** 1080p (Full HD), âm thanh rõ ràng, không lẫn tạp âm.  

---

## I. CHECKLIST CHUẨN BỊ TRƯỚC KHI QUAY

1. **Công cụ quay màn hình:**
   - Sử dụng **OBS Studio** (khuyên dùng để nét nhất) hoặc **Windows Game Bar** (`Win + G`).
   - Cài đặt độ phân giải màn hình 1920x1080, tốc độ khung hình 30fps hoặc 60fps.
   - Bật micro, test thu âm thử 10 giây để đảm bảo âm lượng to, rõ, không bị rè.

2. **Chuẩn bị các cửa sổ trên màn hình:**
   - **Cửa sổ 1:** Trình soạn thảo IDE (IntelliJ IDEA) mở sẵn các file:
     * `src/vn/edu/hcmus/fit/dictionary/model/index/SlangIndex.java`
     * `src/vn/edu/hcmus/fit/dictionary/model/index/InvertedDefinitionIndex.java`
     * `src/vn/edu/hcmus/fit/dictionary/model/index/AutocompleteTrie.java`
     * `src/vn/edu/hcmus/fit/dictionary/model/repository/FileSlangRepository.java`
     * `src/test/Milestone5Test.java`
   - **Cửa sổ 2:** Cửa sổ ứng dụng **Slang Dictionary** đang chạy (click đúp `run.bat`), sẵn sàng thao tác.
   - **Cửa sổ 3:** Terminal đang mở sẵn lệnh chạy test `test.bat` để chiếu kết quả benchmark.
   - Đóng toàn bộ các tab trình duyệt cá nhân, ứng dụng chat (Zalo, Messenger, Discord) để tránh bị phân tâm hoặc dính thông báo khi đang quay.

3. **Chức năng được chọn để demo trọn vẹn:**
   - **Chức năng F04: Thêm mới Slang Word kết hợp cơ chế xử lý trùng lặp (Overwrite / Duplicate)** và kiểm chứng tự động trên chức năng Tìm kiếm (F01) & Reset dữ liệu gốc (F07).
   - *Lý do chọn:* Đây là chức năng phức tạp, thể hiện đầy đủ nhất logic nghiệp vụ của đề bài: validate dữ liệu, kiểm tra trùng lặp trên cấu trúc dữ liệu, hiển thị dialog xác nhận người dùng, cập nhật đa cấu trúc index (Map, Trie, Inverted Index), ghi file an toàn xuống đĩa và thông báo cập nhật giao diện theo mô hình MVC.

---

## II. PHÂN BỔ THỜI LƯỢNG TỔNG QUAN

| Phần | Nội dung chính | Thời lượng dự kiến | Yêu cầu theo đề bài |
|---|---|---|---|
| **Phần 1** | Giới thiệu bản thân, đề tài và **Tỷ lệ tham khảo (% tham khảo)** | 0:00 - 0:40 (~40s) | Bắt buộc theo đề mục IV.6 & IV.7 |
| **Phần 2** | **Kiến trúc hệ thống (MVC)** và Logic luồng dữ liệu & Lưu trữ an toàn | 0:40 - 1:30 (~50s) | Bổ sung chuyên sâu theo yêu cầu đồ án |
| **Phần 3** | **Chi tiết Cấu trúc Dữ liệu, Bản chất Index & Logic Benchmark Milestone5Test** | 1:30 - 3:15 (~105s)| Bắt buộc theo đề mục IV.7 & Yêu cầu bổ sung |
| **Phần 4** | **Demo thực tế toàn bộ 1 chức năng** trên giao diện ứng dụng (Thêm Overwrite/Duplicate) | 3:15 - 4:45 (~90s) | Bắt buộc theo đề mục IV.7 |
| **Phần 5** | Thống kê hiệu năng benchmark 100k từ, lời kết & cảm ơn | 4:45 - 5:15 (~30s) | Tổng kết, đối chiếu đề bài |

---

## III. KỊCH BẢN CHI TIẾT THEO TỪNG PHÂN CẢNH (SCRIPT THOẠI & THAO TÁC)

---

### PHẦN 1: GIỚI THIỆU & CÔNG BỐ TỶ LỆ THAM KHẢO (0:00 – 0:40)

* **Thao tác màn hình:**
  - Mở toàn màn hình cửa sổ IDE hiển thị file `README.md` hoặc slide tiêu đề có ghi rõ: Tên sinh viên, MSSV, Tên môn học, Mã đồ án DAMH#1.
* **Lời thoại (Voice-over):**
  > *"Kính chào Thầy/Cô và các bạn! Em tên là **Trần Anh Kiệt**, sinh viên lớp Kỹ sư Tài năng, Khoa Công nghệ Thông tin, Trường Đại học Khoa học Tự nhiên – ĐHQG-HCM.*
  >
  > *Hôm nay, em xin phép trình bày video báo cáo nghiệm thu đồ án môn học số 1 môn Lập trình ứng dụng Java: **Xây dựng ứng dụng Slang Word Dictionary** sử dụng Java Swing và Java 21.*
  >
  > *Trước tiên, em xin công bố rõ ràng về **tỷ lệ tham khảo** của đồ án theo quy định của môn học: Toàn bộ đồ án được em **tự thiết kế kiến trúc và tự cài đặt mã nguồn đến 95%**. Khoảng **5% tham khảo** là từ tài liệu Java Swing chính thức của Oracle về cách tổ chức Layout và lý thuyết cấu trúc dữ liệu Prefix Tree trong giáo trình Thuật toán. Đồ án tuyệt đối không sao chép mã nguồn của bất kỳ ai và không sử dụng thư viện bên ngoài."*

---

### PHẦN 2: KIẾN TRÚC HỆ THỐNG (MVC) & LOGIC DỮ LIỆU (0:40 – 1:30)

* **Thao tác màn hình:**
  - Thu nhỏ slide, chuyển sang cây thư mục mã nguồn trong IDE (`src/vn/edu/hcmus/fit/dictionary`).
  - Lần lượt rê chuột hoặc mở các package: `model`, `view`, `controller`, `util`.
* **Lời thoại (Voice-over):**
  > *"Về **kiến trúc hệ thống**, đồ án được thiết kế tuân thủ nghiêm ngặt theo mô hình **Model - View - Controller (MVC) chuẩn** kết hợp với **Observer Pattern**:*
  >
  > 1. *Thứ nhất là **tầng View**: Bao gồm các thành phần giao diện Java Swing như `MainFrame`, `SearchPanel`, `ManagementPanel`. Tầng View hoàn toàn độc lập, không chứa thuật toán tìm kiếm và không đọc ghi file; View chỉ bắt tương tác của người dùng và chuyển giao sự kiện cho Controller.*
  >
  > 2. *Thứ hai là **tầng Controller**: Gồm `DictionaryController`, `QuizController` và `AutocompleteController`. Controller đóng vai trò cầu nối, kiểm tra tính hợp lệ dữ liệu (validation), sau đó gọi Model để thực thi nghiệp vụ và điều phối View hiển thị.*
  >
  > 3. *Thứ ba là **tầng Model & Repository**: Quản lý toàn bộ dữ liệu từ điển tập trung trong RAM (Single Source of Truth). Khi Model có sự thay đổi, nó sẽ phát thông báo thông qua `DictionaryModelListener` để View tự động vẽ lại bảng.*
  >
  > *Về **logic lưu trữ và độ bền dữ liệu**: Hệ thống áp dụng cơ chế **Safe Atomic Write** — ghi dữ liệu ra tệp tạm `.tmp` trước rồi mới thay thế nguyên tử tệp `slang_user.txt`, tránh hoàn toàn nguy cơ mất dữ liệu khi bị tắt đột ngột."*

---

### PHẦN 3: CHI TIẾT CẤU TRÚC DỮ LIỆU, BẢN CHẤT INDEX & LOGIC BENCHMARK (1:30 – 3:15)

Đây là phần kỹ thuật cốt lõi giải đáp trọn vẹn yêu cầu về Cấu trúc Dữ liệu, Bản chất Index và Logic Kiểm thử 100k từ.

#### 3.1. Các Cấu trúc Dữ liệu chính & Vị trí Mã nguồn cụ thể (1:30 – 2:10)
* **Thao tác màn hình:**
  - Mở file `src/vn/edu/hcmus/fit/dictionary/model/index/SlangIndex.java` $\to$ bôi đen dòng khai báo `HashMap<String, SlangEntry>`.
  - Mở file `src/vn/edu/hcmus/fit/dictionary/model/index/InvertedDefinitionIndex.java` $\to$ bôi đen phương thức `search()` và các thao tác `retainAll()`.
  - Mở file `src/vn/edu/hcmus/fit/dictionary/model/index/AutocompleteTrie.java` $\to$ bôi đen lớp `TrieNode` và hàm `suggest()`.
* **Lời thoại (Voice-over):**
  > *"Tiếp theo, em xin đi sâu vào **chi tiết các cấu trúc dữ liệu và vị trí code cụ thể** trong dự án:*
  >
  > *1. **Tra cứu theo Slang Word ($O(1)$)**: Trong file `SlangIndex.java` (dòng 18), em sử dụng cấu trúc `HashMap<String, SlangEntry>`. Khóa (key) là từ slang đã được chuẩn hóa viết hoa thông qua `TextNormalizer.normalizeWord(word)`. Bảng băm giúp việc tìm kiếm, thêm, xóa đạt độ phức tạp lý thuyết là **$O(1)$**, không bị phụ thuộc vào số lượng từ trong từ điển.*
  >
  > *2. **Tra cứu theo Definition (Inverted Index)**: Trong file `InvertedDefinitionIndex.java` (dòng 18), em sử dụng cấu trúc `Map<String, Set<String>>` ánh xạ từ mỗi từ đơn (token) trong định nghĩa tới tập các Slang Word chứa nó. Trong phương thức `search()` (từ dòng 71), thay vì duyệt tuyến tính $O(N)$ toàn bộ từ điển rất chậm, thuật toán tách từ khóa thành các token, lấy ra các danh sách `Set<String>` tương ứng, sắp xếp theo kích thước tăng dần rồi thực hiện phép giao tập hợp bằng lệnh `candidateKeys.retainAll()`. Sau khi thu hẹp không gian tìm kiếm từ 100.000 từ xuống chỉ còn vài chục từ ứng viên, thuật toán mới kiểm chứng chuỗi con chính xác.*
  >
  > *3. **Gợi ý tự động Tiền tố (Autocomplete Trie)**: Trong file `AutocompleteTrie.java` và `TrieNode.java`, em cài đặt cấu trúc Cây tiền tố (Prefix Tree). Mỗi node chứa một `HashMap<Character, TrieNode> children`. Khi người dùng gõ chuỗi tiền tố độ dài $L$, hàm `suggest()` duyệt theo các nhánh cây đúng $L$ bước trong thời gian **$O(L)$** để tìm tới node tiền tố, sau đó duyệt DFS thu thập tối đa 10 gợi ý trong thời gian dưới 0.3 mili-giây, hoàn toàn độc lập với kích thước từ điển."*

#### 3.2. Bản chất Cấu trúc Index là gì? Được lưu ở đâu? Có và Không có nó khác nhau thế nào? (2:10 – 2:40)
* **Thao tác màn hình:**
  - Mở cây thư mục trỏ vào `data/index/` và mở file `FileSlangRepository.java` (từ dòng 90 - 130).
* **Lời thoại (Voice-over):**
  > *"Vậy **Cấu trúc Index là gì và có tác dụng gì?***
  > *Trong khoa học máy tính, **Index (chỉ mục)** là cấu trúc dữ liệu phụ trợ được lập sẵn trong bộ nhớ — giống như phần **mục lục tra cứu ở cuối một cuốn sách dày**. Thay vì phải lật đọc từng trang từ đầu đến cuối ($O(N)$), ta chỉ cần tra mục lục để nhảy cóc ngay tới trang chứa từ khóa.*
  >
  > *Cấu trúc Index này **được lưu ở đâu?***
  > *Nó được lưu tại thư mục `data/index/` dưới 2 tệp tin:*
  > *1. Tệp `index_cache.ser`: chứa toàn bộ 3 cấu trúc Index (`SlangIndex`, `InvertedDefinitionIndex`, `AutocompleteTrie`) được chuyển đổi thành nhị phân bằng cơ chế **Java Object Serialization**.*
  > *2. Tệp `metadata.properties`: lưu mã băm **SHA-256** của tệp `slang_user.txt` để kiểm tra tính toàn vẹn.*
  >
  > *Vậy **Có Index Cache và Không Có Index Cache khác nhau như thế nào?***
  > *- **Khi CÓ Index Cache**: Ứng dụng chỉ cần đọc file `.ser` và deserialize thẳng vào RAM. Hệ thống **không cần đọc tệp text thô**, **không cần cắt chuỗi từng dòng** và **không cần chạy bộ băm từ (Tokenizer)**. Thời gian nạp ứng dụng khởi động cực nhanh: chỉ mất vỏn vẹn **~110 đến 130 mili-giây**.*
  > *- **Khi KHÔNG CÓ Index Cache** (như lần đầu chạy hoặc khi xóa thư mục `index`): Ứng dụng bắt buộc phải mở file văn bản `slang_user.txt`, đọc từng dòng, băm từng từ vào Map, Inverted Index và chèn vào Trie. Với 7.638 từ, thời gian nạp mất **~350 đến 450 ms**; và nếu là 100.000 từ sẽ mất gần **1 giây**.*
  > *Đặc biệt, hệ thống có cơ chế **Tự phục hồi (Self-Recovery)**: Khi phát hiện thiếu cache hoặc mã SHA-256 không khớp, Model sẽ tự động rebuild lại từ file text và tự tạo mới file `.ser` mà không gây bất kỳ lỗi nào cho người dùng."*

#### 3.3. Phân tích Logic Kiểm thử Benchmark trong `Milestone5Test.java` (2:40 – 3:15)
* **Thao tác màn hình:**
  - Mở file `src/test/Milestone5Test.java`.
  - Cuộn chuột lướt qua 5 Test Case và dừng lại ở đoạn code của Test 2 và Test 3.
* **Lời thoại (Voice-over):**
  > *"Để chứng minh một cách khoa học rằng ứng dụng đáp ứng yêu cầu dưới 1 giây cho 100.000 từ, em đã thiết kế bộ kiểm thử tự động toàn diện trong file **`Milestone5Test.java`** với logic gồm 5 bài test nghiêm ngặt:*
  >
  > *- **Test 1 - Xây dựng Dataset 100.000 từ**: Vòng lặp sinh tự động 100.000 đối tượng `SlangEntry` phân biệt kèm các định nghĩa đa dạng chứa các từ khóa mẫu, sau đó nạp đồng thời vào cả 3 cấu trúc index. Tổng thời gian index 100.000 từ chỉ mất khoảng **850 mili-giây**.*
  >
  > *- **Test 2 - Benchmark Slang Word $O(1)$ qua 10.000 queries**: Sử dụng hàm `Random` bốc ngẫu nhiên 10.000 từ trong tập 100.000 từ và đo chính xác bằng `System.nanoTime()`. Kết quả thời gian tra cứu trung bình chỉ mất **~0.39 micro-giây/lần** — tức là nhanh hơn yêu cầu 1 giây của đề bài tới **hơn 2,5 triệu lần**! Tra cứu tiền tố Autocomplete Trie cũng chỉ mất **~200 micro-giây**.*
  >
  > *- **Test 3 - Benchmark Inverted Index Definition**: Tra cứu các từ khóa phổ biến có tới 10.000 kết quả khớp mỗi từ. Thuật toán giao tập hợp và xác thực substring chỉ mất trung bình **~18 đến 20 mili-giây**, nhanh hơn 50 lần so với giới hạn 1 giây của đề bài.*
  >
  > *- **Test 4 - Kiểm thử Độ bền (Data Persistence)**: Thêm một từ mới vào Model, sau đó hủy hoàn toàn instance trong bộ nhớ và khởi tạo lại Model mới từ đĩa (mô phỏng tắt app rồi bật lại). Bài test khẳng định từ mới vẫn tồn tại nguyên vẹn 100% trong RAM.*
  >
  > *- **Test 5 - Kiểm thử Tự phục hồi Index**: Chủ động xóa sạch 2 file cache `index_cache.ser` và `metadata.properties`. Khi Model khởi động lại, hệ thống tự phát hiện thiếu cache, tự đọc lại file text và tự tạo lại bộ cache mới chỉ trong **~136 ms** mà không hề gặp lỗi."*

---

### PHẦN 4: DEMO TRỌN VẸN 1 CHỨC NĂNG – THÊM SLANG WORD (3:15 – 4:45)

* **Thao tác màn hình:**
  - Chuyển sang màn hình ứng dụng đang chạy.
  - Phóng to cửa sổ ứng dụng ở vị trí trung tâm rõ ràng.
* **Lời thoại (Voice-over):**
  > *"Sau đây, em xin phép **demo toàn bộ một chức năng hoàn chỉnh**: Đó là **Chức năng Quản lý Slang Word – Thao tác Thêm mới với cơ chế xử lý trùng lặp (Overwrite / Duplicate)** kết hợp kiểm tra độ bền dữ liệu và khôi phục gốc.*
  >
  > *Trước hết, em vào tab **'Quản lý Từ điển'** trên thanh menu điều hướng."*

#### Bước 4.1: Thêm một từ hoàn toàn mới (Không trùng)
* **Thao tác trên app:**
  - Nhập vào ô Slang Word: `HCMUS_PRO`
  - Nhập vào ô Định nghĩa: `Sinh vien Dai hoc Khoa hoc Tu nhien xuat sac`
  - Bấm nút **"Thêm mới Slang Word"**.
* **Lời thoại:**
  > *"Đầu tiên, em thêm một từ chưa từng có trong từ điển: `HCMUS_PRO` với định nghĩa là `Sinh vien Dai hoc Khoa hoc Tu nhien xuat sac`. Em bấm nút **'Thêm mới Slang Word'**.*
  >
  > *Hệ thống thông báo thêm thành công! Bảng danh sách từ bên phải được cập nhật ngay lập tức. Bây giờ em chuyển sang tab **'Tra cứu Slang Word'**, gõ `HCMUS_PRO` — kết quả được tìm thấy ngay lập tức trong 0.2 mili-giây!"*

#### Bước 4.2: Thêm từ đã tồn tại – Chọn DUPLICATE (Nhân bản / Nối nghĩa)
* **Thao tác trên app:**
  - Quay lại tab **"Quản lý Từ điển"**.
  - Nhập từ đã có sẵn trong từ điển: `LOL`
  - Nhập định nghĩa mới: `Lots Of Love`
  - Bấm nút **"Thêm mới Slang Word"**.
  - Hộp thoại Dialog cảnh báo hiện ra: *"Từ slang 'LOL' đã tồn tại trong từ điển! Bạn muốn xử lý như thế nào?"* với 3 nút: **Ghi đè (Overwrite)**, **Nhân bản (Duplicate)**, **Hủy (Cancel)**.
  - Click chọn nút **"Duplicate (Thêm nghĩa mới)"**.
* **Lời thoại:**
  > *"Bây giờ, em sẽ thử nghiệm trường hợp từ bị trùng theo đúng yêu cầu đề bài: Em nhập từ `LOL` vốn đã có nghĩa là 'Laugh out loud', và nhập thêm định nghĩa mới là `Lots Of Love`. Em bấm 'Thêm mới'.*
  >
  > *Một hộp thoại xuất hiện thông báo từ đã tồn tại, và hỏi người dùng muốn **Ghi đè** hay **Nhân bản**. Em bấm chọn **'Duplicate'**.*
  >
  > *Thông báo cập nhật thành công! Em qua tab tra cứu tìm lại từ `LOL`: Như Thầy/Cô thấy, từ `LOL` bây giờ đã có **cả hai định nghĩa**: định nghĩa cũ 'Laugh out loud' và định nghĩa mới 'Lots Of Love' được nối thêm vào bằng dấu gạch đứng!"*

#### Bước 4.3: Thêm từ đã tồn tại – Chọn OVERWRITE (Ghi đè)
* **Thao tác trên app:**
  - Quay lại tab **"Quản lý Từ điển"**.
  - Nhập lại từ `HCMUS_PRO`.
  - Nhập định nghĩa thay thế: `Nhom lap trinh Java dinh cao`
  - Bấm nút **"Thêm mới Slang Word"**.
  - Hộp thoại hỏi, click chọn **"Overwrite (Ghi đè)"**.
* **Lời thoại:**
  > *"Tiếp theo là trường hợp **Ghi đè (Overwrite)**: Em nhập lại từ `HCMUS_PRO`, đổi định nghĩa thành `Nhom lap trinh Java dinh cao` và bấm Thêm mới. Khi hộp thoại hỏi, em chọn **'Overwrite'**.*
  >
  > *Tra cứu lại từ `HCMUS_PRO`, toàn bộ định nghĩa cũ đã được thay thế hoàn toàn bằng định nghĩa mới!"*

#### Bước 4.4: Kiểm chứng tính bền vững & Chức năng Reset về danh sách gốc
* **Thao tác trên app:**
  - Bấm nút **"Khôi phục Dữ liệu Gốc (Reset)"** ở góc dưới form quản lý.
  - Dialog xác nhận hiện lên: *"Bạn có chắc chắn muốn khôi phục danh sách slang words về trạng thái gốc ban đầu không?"*
  - Bấm chọn **Yes**.
  - Tra cứu lại từ `HCMUS_PRO` $\to$ Báo *"Không tìm thấy slang word này"*.
* **Lời thoại:**
  > *"Cuối cùng, em kiểm chứng tính năng **Reset danh sách gốc**: Em bấm nút **'Khôi phục Dữ liệu Gốc (Reset)'**, hộp thoại xác nhận hiện lên và em bấm 'Yes'.*
  >
  > *Ứng dụng lập tức sao chép lại từ file gốc `slang_base.txt` và cập nhật lại Model trong RAM. Bây giờ khi tra cứu từ `HCMUS_PRO`, hệ thống báo không tìm thấy vì dữ liệu đã trở về nguyên bản 100%!"*

---

### PHẦN 5: TỔNG KẾT & KẾT THÚC (4:45 – 5:15)

* **Thao tác màn hình:**
  - Chuyển sang cửa sổ Terminal hiển thị kết quả chạy `test.bat` với toàn bộ kết quả Pass rõ nét bằng tiếng Việt.
* **Lời thoại (Voice-over):**
  > *"Như Thầy/Cô có thể thấy trên màn hình Terminal, toàn bộ 4 bộ kiểm thử tự động từ Milestone 2 đến Milestone 5 đều vượt qua 100% với thời gian tra cứu chỉ vài micro-giây cho 100.000 từ. Ứng dụng đáp ứng trọn vẹn cả 10 chức năng theo yêu cầu đề bài, đảm bảo an toàn tệp và có giao diện Swing thân thiện, mượt mà.*
  >
  > *Em xin chân thành cảm ơn Thầy/Cô đã theo dõi video báo cáo đồ án của em. Em xin kính chúc Thầy/Cô thật nhiều sức khỏe và công tác tốt!"*

---

## IV. HƯỚNG DẪN RENDER & ĐĂNG TẢI LÊN YOUTUBE

1. **Sau khi quay xong:**
   - Xem lại video từ đầu đến cuối để đảm bảo âm thanh khớp với hình ảnh và không bị ngắt quãng.
   - Nếu cần cắt đầu/đuôi cho gọn, có thể dùng công cụ cắt video tích hợp sẵn trên Windows (Photos / Clipchamp).

2. **Tải lên YouTube:**
   - Truy cập: `https://studio.youtube.com`.
   - Bấm nút **TẠO** (Create) $\to$ **Tải video lên** (Upload videos).
   - Đặt tiêu đề video:
     `[CSC13002] Đồ án #1: Slang Word Dictionary - [MSSV] - [Họ và tên]`
     *(Ví dụ: `[CSC13002] Đồ án #1: Slang Word Dictionary - 22120000 - Trần Anh Kiệt`)*
   - Đặt chế độ hiển thị: Chọn **Không công khai (Unlisted)** (ai có link mới xem được, an toàn và riêng tư) hoặc **Công khai (Public)**.
   - Sao chép đường link video YouTube (VD: `https://youtu.be/xxxxxx`).

3. **Cập nhật link vào file nộp bài:**
   - Mở file `DanhGia_LinkVideo.txt` trong thư mục dự án.
   - Điền MSSV của bạn vào dòng: `Mã số sinh viên (MSSV): [Điền MSSV]`
   - Dán link YouTube vừa copy vào dòng: `Link Video Youtube Demo: [Dán link vào đây]`
   - Chạy file `package_submission.bat` để tự động tạo file zip nộp bài `MSSV_100.zip`.
