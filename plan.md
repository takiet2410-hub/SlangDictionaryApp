
# PROJECT PLAN & IMPLEMENTATION PROMPT
## DAMH#1 – Slang Dictionary bằng Java Swing

Bạn là một coding agent có kinh nghiệm xây dựng ứng dụng desktop bằng Java Swing, Java IO, OOP, Generics và Java Collections Framework.

Hãy giúp tôi xây dựng ứng dụng **Slang Dictionary** theo đúng yêu cầu của file `DAMH#1.md`, kết hợp các lưu ý bổ sung từ giảng viên được ghi trong prompt này.

### 0. Nguyên tắc làm việc

Trước khi viết code:

1. Đọc toàn bộ file `DAMH#1.md` và trích xuất chính xác:
    - Yêu cầu chung.
    - Danh sách chức năng.
    - Quy định kỹ thuật.
    - Quy định nộp bài.
    - Tiêu chí tự đánh giá và các điều kiện có thể bị trừ điểm.
2. Nếu có yêu cầu trong prompt này khác với file đề, hãy lập bảng đối chiếu và phân biệt:
    - Yêu cầu chính thức từ đề.
    - Yêu cầu bổ sung từ giảng viên.
    - Quyết định thiết kế đề xuất.
3. Không tự ý bỏ chức năng, không tự thêm thư viện hoặc framework lớn nếu chưa giải thích lý do.
4. Không bắt đầu triển khai toàn bộ ứng dụng ngay. Trước tiên hãy trình bày kế hoạch, kiến trúc, mô hình dữ liệu, cách quản lý file và cách kiểm thử để tôi duyệt.
5. Chia quá trình thành các milestone nhỏ. Mỗi milestone phải có code chạy được, kiểm thử và kết quả nghiệm thu cụ thể.

Tôi sử dụng **Java Swing** để xây dựng giao diện. Ưu tiên Java chuẩn, Java IO, OOP, Generics và Collections được học trong môn.

---

### 1. Quy trình dữ liệu bắt buộc

Đây là yêu cầu đặc biệt quan trọng.

Tôi sẽ thực hiện quy trình:

1. Mở file `slang.txt` để kiểm tra dữ liệu.
2. Quét toàn bộ file để phát hiện các dòng sai định dạng hoặc có vấn đề.
3. Capture/đánh dấu các dòng lỗi, giữ nguyên thông tin dòng và nội dung gốc để tôi xem lại.
4. Tôi sẽ sửa thủ công các dòng lỗi trong file.
5. Sau khi chuẩn hóa, lưu lại thành **file dữ liệu gốc đã chuẩn hóa**.
6. Ứng dụng sẽ nạp file chuẩn hóa này làm dữ liệu gốc ban đầu.

Không được để ứng dụng tự động sửa nội dung slang hoặc definition theo phỏng đoán. Chức năng quét lỗi chỉ phát hiện, đánh dấu và hỗ trợ người dùng sửa thủ công.

Hãy thiết kế rõ các file dữ liệu, ví dụ:

- `data/slang.txt`: file nguồn ban đầu, không được ghi đè.
- `data/slang_base.txt`: dữ liệu gốc đã chuẩn hóa và được dùng làm baseline.
- `data/slang_user.txt`: dữ liệu làm việc, chứa các thay đổi add/edit/delete.
- `data/search_history.txt`: lịch sử tra cứu.
- `data/index/`: nơi lưu index đã tạo để lần chạy sau không phải xây dựng lại từ đầu.

Có thể đề xuất tên file khác, nhưng phải giữ nguyên nguyên tắc phân biệt dữ liệu gốc và dữ liệu làm việc.

#### Quy tắc load và save

- Khi ứng dụng khởi chạy:
    - Nếu chưa có file dữ liệu làm việc, nạp dữ liệu từ `slang_base.txt`.
    - Nếu đã có file dữ liệu làm việc hợp lệ, nạp dữ liệu từ file đó.
    - Đọc dữ liệu vào memory một lần, sau đó mọi thao tác tìm kiếm phải sử dụng cấu trúc dữ liệu trong memory.
- Không đọc lại toàn bộ file mỗi khi người dùng tìm kiếm.
- Khi add/edit/delete thành công, cập nhật cấu trúc dữ liệu trong memory và lưu dữ liệu làm việc xuống file riêng.
- Không bao giờ ghi đè file gốc đã chuẩn hóa.
- Khi đóng ứng dụng và mở lại, những thay đổi đã lưu phải được giữ nguyên.
- Khi Reset:
    - Hiển thị hộp thoại xác nhận.
    - Khôi phục dữ liệu làm việc từ file gốc đã chuẩn hóa.
    - Xóa hoặc tái tạo các index tương ứng.
    - Cập nhật memory và giao diện.
    - Không làm thay đổi file `slang_base.txt`.

Cần có cơ chế ghi file an toàn: ghi ra file tạm, kiểm tra kết quả rồi thay thế file làm việc để tránh mất dữ liệu nếu quá trình ghi bị lỗi.

---

### 2. Phát hiện và sửa lỗi dữ liệu

Xây dựng một bước kiểm tra dữ liệu riêng trước khi sử dụng.

Yêu cầu:

- Quét toàn bộ file, không dừng ngay khi gặp lỗi đầu tiên.
- Báo cáo tổng số dòng đã quét và số dòng có vấn đề.
- Capture các dòng lỗi, gồm:
    - Số dòng.
    - Nội dung gốc.
    - Loại lỗi.
    - Mô tả lỗi.
- Có thể xuất báo cáo lỗi ra file để người dùng sửa thủ công.
- Không tự động xóa hoặc sửa dòng lỗi.
- Sau khi người dùng sửa file, cho phép chạy kiểm tra lại.
- Chỉ cho phép đưa file vào quy trình chuẩn hóa khi người dùng xác nhận dữ liệu đã được sửa.
- Cần xử lý lỗi định dạng, dòng rỗng, slang word rỗng, definition rỗng và các trường hợp không thể parse theo format thực tế của `slang.txt`.

Trước khi thiết kế parser, hãy kiểm tra cấu trúc thực tế của file `slang.txt`. Không được tự giả định delimiter hoặc format nếu chưa xem dữ liệu mẫu.

---

### 3. Danh sách chức năng bắt buộc theo DAMH#1

Hãy tạo checklist và triển khai đầy đủ 10 chức năng sau:

1. Tìm kiếm theo slang word.
2. Tìm kiếm theo definition:
    - Definition chứa keyword cần tìm.
    - Hiển thị tất cả slang word phù hợp.
3. Hiển thị lịch sử tìm kiếm.
4. Thêm slang word mới:
    - Nếu slang word đã tồn tại, thông báo.
    - Cho người dùng chọn overwrite hoặc thêm duplicate theo đúng yêu cầu đề.
    - Cập nhật memory và lưu file dữ liệu làm việc.
5. Edit một slang word.
6. Delete một slang word:
    - Bắt buộc confirm trước khi xóa.
7. Reset danh sách slang word về dữ liệu gốc đã chuẩn hóa.
8. Random một slang word, thể hiện chức năng “On this day slang word”.
9. Quiz:
    - Hiển thị một slang word.
    - Có 4 definition để người dùng chọn.
10. Quiz:
- Hiển thị một definition.
- Có 4 slang word để người dùng chọn.

Không được coi autocomplete là chức năng thay thế cho tìm kiếm. Autocomplete là tính năng hỗ trợ nhập liệu, còn chức năng tìm kiếm phải thực hiện được đầy đủ.

---

### 4. Autocomplete

Bổ sung autocomplete cho ô tìm kiếm slang word.

Khi người dùng gõ một phần của slang word:

- Hiển thị danh sách các slang word có khả năng khớp.
- Cập nhật gợi ý theo từng ký tự nhập.
- Cho phép dùng chuột hoặc bàn phím để chọn gợi ý.
- Khi chọn gợi ý, điền từ đó vào ô tìm kiếm.
- Có thể nhấn Enter để thực hiện tìm kiếm.
- Không được truy vấn file liên tục; autocomplete phải sử dụng dữ liệu/index trong memory.
- Xử lý đúng trường hợp không có gợi ý.
- Không để popup autocomplete che khuất hoặc làm hỏng bố cục Swing.

Hãy khảo sát cách triển khai autocomplete bằng Swing phù hợp, chẳng hạn `JTextField`, `JPopupMenu`, `JList` và `DocumentListener`. Chọn cách đơn giản, dễ hiểu, dễ bảo trì và không cần thư viện ngoài nếu không thật sự cần thiết.

---

### 5. Cấu trúc dữ liệu và hiệu năng

Đề yêu cầu tìm kiếm nhanh dưới 1 giây với tập dữ liệu lên đến 100.000 slang words và lưu lại cấu trúc dữ liệu/index để lần chạy tiếp theo không phải tổ chức lại dữ liệu từ đầu.

Hãy thiết kế ít nhất:

- Index tra cứu slang word → definition.
- Index tìm kiếm theo definition → danh sách slang word.
- Cấu trúc lưu lịch sử.
- Cấu trúc hỗ trợ autocomplete.

Đề xuất ban đầu:

- `HashMap<String, SlangEntry>` cho tra cứu slang word.
- Inverted index cho definition.
- `ArrayList` hoặc cấu trúc phù hợp cho history.
- Cấu trúc prefix phù hợp cho autocomplete.

Không được chỉ tuyên bố đạt O(1) hoặc dưới 1 giây mà không kiểm thử.

Hãy trình bày:

- Độ phức tạp thời gian của từng thao tác.
- Cách cập nhật index khi add/edit/delete.
- Cách loại bỏ dữ liệu cũ khỏi index.
- Cách tránh index bị sai lệch so với dữ liệu chính.
- Cách lưu index xuống file và nạp lại.
- Cách xác định index có còn hợp lệ với file dữ liệu hiện tại hay không.

Nếu dùng serialized index, phải có kiểm tra phiên bản, tính toàn vẹn và khả năng rebuild khi file index hỏng hoặc không tương thích.

Bắt buộc đo hiệu năng bằng dataset có quy mô gần 100.000 slang words. Báo cáo thời gian tìm kiếm thực tế, không dùng số liệu giả định.

---

### 6. Khảo sát Design Patterns trước khi code

Trước khi xây dựng, hãy khảo sát tài liệu tại:

- Refactoring.Guru: https://refactoring.guru/design-patterns
- Java examples: https://refactoring.guru/design-patterns/java

Refactoring.Guru mô tả design pattern là các giải pháp mẫu cho những vấn đề thiết kế thường gặp, không phải lý do để áp dụng pattern một cách máy móc. Hãy chọn pattern dựa trên vấn đề thực tế của ứng dụng.

Pattern tôi quyết định sử dụng

#### MVC – Model View Controller

Dùng để phân tách:

- Model: dữ liệu slang và các cấu trúc index.
- View: giao diện Java Swing.
- Controller: xử lý sự kiện và điều phối thao tác.

Không để toàn bộ logic đọc file, tìm kiếm, cập nhật index và quiz nằm trong `JFrame`.

---

### 7. Kiến trúc và tổ chức mã nguồn

Đề xuất cấu trúc package rõ ràng, chẳng hạn:

- `model`
    - `SlangEntry`
    - `SearchHistoryEntry`
    - `ValidationIssue`
- `repository`
    - `SlangRepository`
    - `FileSlangRepository`
- `service`
    - `SlangDictionaryService`
    - `SearchService`
    - `QuizService`
    - `DataValidationService`
- `index`
    - `SlangIndex`
    - `DefinitionIndex`
    - `AutocompleteIndex`
- `gui`
    - `MainFrame`
    - `SearchPanel`
    - `ManagementPanel`
    - `HistoryPanel`
    - `RandomPanel`
    - `QuizPanel`
    - `ValidationPanel`
- `controller`
    - Controller hoặc listener tương ứng nếu cần.
- `util`
    - Các hàm chuẩn hóa chuỗi, đọc cấu hình và hỗ trợ file.

Đây là cấu trúc gợi ý. Hãy điều chỉnh sao cho phù hợp với quy mô đồ án và giải thích lý do.

---

### 8. Giao diện Java Swing

Ứng dụng phải có GUI đầy đủ, không phải console app.

Hãy thiết kế giao diện gồm:

- Cửa sổ chính `JFrame`.
- Menu hoặc thanh điều hướng giữa các chức năng.
- Màn hình tìm kiếm slang word.
- Màn hình tìm kiếm definition.
- Màn hình history.
- Màn hình quản lý add/edit/delete.
- Màn hình random slang word.
- Hai màn hình quiz.
- Màn hình kiểm tra dữ liệu và báo cáo dòng lỗi.

Sử dụng hợp lý:

- `JPanel`
- `JTextField`
- `JButton`
- `JLabel`
- `JTable` hoặc `JList`
- `JScrollPane`
- `JOptionPane`
- `BorderLayout`
- `FlowLayout`
- `GridLayout`

Dùng `CardLayout` nếu cần chuyển qua lại giữa các màn hình trong cửa sổ chính.

Các thao tác có thể tốn thời gian như load dữ liệu lớn, rebuild index hoặc kiểm tra file phải tránh làm treo Event Dispatch Thread. Hãy giải thích cách sử dụng `SwingWorker` hoặc cơ chế tương đương, đồng thời bảo đảm cập nhật component Swing đúng thread.

---

### 9. Xử lý sự kiện và validation

- Dùng event-driven programming của Swing.
- Có thể dùng `ActionListener`, `DocumentListener` và lambda.
- Không để listener chứa business logic dài.
- Validate dữ liệu trước khi add/edit.
- Xử lý trường hợp slang word rỗng, definition rỗng, duplicate, file không tồn tại, file hỏng và lỗi ghi file.
- Hiển thị lỗi rõ ràng bằng dialog hoặc thông báo trên giao diện.
- Delete và Reset phải có confirm dialog.
- Không nuốt ngoại lệ bằng `catch` rỗng.

---

### 10. Kiểm thử và nghiệm thu

Hãy xây dựng test plan trước khi triển khai.

Mỗi chức năng phải có:

- Điều kiện đầu vào.
- Thao tác.
- Kết quả mong đợi.
- Kết quả thực tế.
- Trạng thái pass/fail.

Bắt buộc kiểm tra:

- Tìm kiếm chính xác slang word.
- Tìm kiếm definition chứa keyword.
- History được lưu và hiển thị.
- Add mới.
- Add duplicate và xử lý overwrite/duplicate.
- Edit.
- Delete có confirm.
- Reset khôi phục dữ liệu gốc.
- Random.
- Quiz slang word → definition.
- Quiz definition → slang word.
- Autocomplete theo prefix.
- File làm việc được giữ sau khi đóng và mở lại.
- File gốc không bị thay đổi.
- Index được load lại ở lần chạy tiếp theo.
- Index hỏng hoặc không tương thích có thể rebuild.
- Dataset lớn và thời gian tìm kiếm.
- Dữ liệu Unicode tiếng Việt hoặc ký tự đặc biệt nếu có trong dữ liệu.

Không tự tuyên bố chức năng hoàn thành nếu chưa chạy kiểm thử.

---

### 11. Git và quy định nộp bài

Đối chiếu chính xác phần quy định nộp bài trong `DAMH#1.pdf`.

Theo yêu cầu đã được trích xuất, cần lưu ý:

- Tên file nộp có dạng `MSSV_SoDiemTuDanhGia.zip`.
- Có source code.
- Có file chạy/JAR nếu đề yêu cầu.
- Có dữ liệu cần thiết.
- Có hình chụp các lần commit.
- Có ít nhất 10 commits và phân bố đều qua các ngày; không dồn toàn bộ vào sát deadline.
- Có video demo hoặc link video nếu được yêu cầu trong đề.
- Có file tự đánh giá nếu đề yêu cầu.

Hãy tạo checklist nộp bài dựa trên toàn văn đề, không dựa riêng vào danh sách gợi ý này.

Không tạo commit giả hoặc tạo lịch sử Git giả. Mỗi commit phải phản ánh thay đổi thực tế.

---

### 12. Kế hoạch milestone

Hãy đề xuất các milestone theo thứ tự:

1. Đọc đề, khảo sát dữ liệu và lập requirements checklist.
2. Khảo sát design patterns, chốt kiến trúc.
3. Xây dựng parser và công cụ kiểm tra dữ liệu.
4. Chuẩn hóa file dữ liệu gốc.
5. Xây dựng model, repository và load dữ liệu vào memory.
6. Xây dựng index tìm kiếm và lưu/nạp index.
7. Xây dựng CRUD và cơ chế lưu file làm việc.
8. Xây dựng GUI Swing.
9. Xây dựng autocomplete.
10. Xây dựng history, random và quiz.
11. Kiểm thử, benchmark và xử lý lỗi.
12. Hoàn thiện tài liệu, Git, video demo và đóng gói bài nộp.

Với mỗi milestone, trình bày:

- Mục tiêu.
- File/class sẽ tạo hoặc sửa.
- Kiến thức Java được áp dụng.
- Việc coding agent sẽ làm.
- Kết quả đầu ra.
- Điều kiện nghiệm thu.
- Các lỗi/rủi ro cần chú ý.

### 13. Cách phản hồi trong quá trình thực hiện

Trước khi code, hãy trả về:

1. Bảng requirements đối chiếu với đề.
2. Bảng khảo sát design pattern.
3. Kiến trúc đề xuất.
4. Thiết kế file gốc, file làm việc, history và index.
5. Thiết kế cấu trúc dữ liệu.
6. Sơ đồ luồng dữ liệu.
7. Kế hoạch milestone.
8. Test plan.
9. Các câu hỏi còn thiếu thông tin, nếu có.

Sau khi tôi duyệt kế hoạch, mới bắt đầu triển khai milestone đầu tiên.

Trong quá trình code:

- Giải thích ngắn gọn mục đích của mỗi class.
- Không tự ý thay đổi kiến trúc đã duyệt.
- Nếu phát hiện yêu cầu mâu thuẫn hoặc không rõ, dừng lại và hỏi tôi.
- Không chỉ gửi code rời rạc; phải nêu rõ đường dẫn file và cách chạy.
- Không tuyên bố hoàn thành khi chưa có bằng chứng kiểm thử.

Tôi sử dụng java JDK 21.0.12