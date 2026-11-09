package test;

import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchResult;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.model.index.AutocompleteTrie;
import vn.edu.hcmus.fit.dictionary.model.index.InvertedDefinitionIndex;
import vn.edu.hcmus.fit.dictionary.model.index.SlangIndex;
import vn.edu.hcmus.fit.dictionary.util.Constants;
import vn.edu.hcmus.fit.dictionary.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Bộ kiểm thử toàn diện và Đo đạc Hiệu năng (Benchmark) nghiệm thu Milestone 5:
 * - Đo đạc hiệu năng với Dataset 100.000 Slang Words (Slang Lookup, Definition Search, Autocomplete Trie).
 * - Kiểm thử độ bền (Persistence / Durability) qua các chu kỳ đóng/mở ứng dụng.
 * - Kiểm thử cơ chế Tự phục hồi Index (Self-recovery / Cache Invalidation) khi cache bị xóa/hỏng.
 * - Đảm bảo tất cả tiêu chuẩn thời gian < 1s (1.000ms) của đề bài DAMH#1.
 */
public class Milestone5Test {

    private static final int BENCHMARK_DATASET_SIZE = 100_000;
    private static final int BENCHMARK_QUERY_ROUNDS = 10_000;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   KIỂM THỬ TOÀN DIỆN & BENCHMARK HIỆU NĂNG 100.000 TỪ (MILESTONE 5)");
        System.out.println("================================================================================");

        int passed = 0;
        int total = 5;

        // Lưu bản sao dữ liệu sạch trước khi test để phục hồi hoàn nguyên sau khi test xong
        byte[] userFileBackup = null;
        byte[] historyBackup = null;

        try {
            if (Files.exists(Constants.SLANG_USER_FILE)) {
                userFileBackup = Files.readAllBytes(Constants.SLANG_USER_FILE);
            }
            if (Files.exists(Constants.SEARCH_HISTORY_FILE)) {
                historyBackup = Files.readAllBytes(Constants.SEARCH_HISTORY_FILE);
            }

            // Test 1: Khởi tạo Dataset 100.000 từ & Đo tốc độ Indexing
            System.out.println("[Test 1/5] Xây dựng và Index Dataset 100.000 Slang Words...");
            long startBuild = System.currentTimeMillis();

            SlangIndex benchmarkSlangIndex = new SlangIndex(BENCHMARK_DATASET_SIZE);
            InvertedDefinitionIndex benchmarkDefIndex = new InvertedDefinitionIndex(BENCHMARK_DATASET_SIZE * 2);
            AutocompleteTrie benchmarkTrie = new AutocompleteTrie();

            List<String> sampleWords = new ArrayList<>(BENCHMARK_DATASET_SIZE);
            String[] commonKeywords = {"money", "game", "happy", "love", "friend", "fast", "great", "cool", "chat", "code"};

            for (int i = 0; i < BENCHMARK_DATASET_SIZE; i++) {
                String word = "SLANG_" + i;
                sampleWords.add(word);
                String keyword = commonKeywords[i % commonKeywords.length];
                List<String> meanings = List.of(
                        "Meaning of slang " + i + " related to " + keyword,
                        "Alternative meaning for test entry " + i
                );
                SlangEntry entry = new SlangEntry(word, meanings);

                benchmarkSlangIndex.put(entry);
                benchmarkDefIndex.indexEntry(entry);
                benchmarkTrie.insert(word);
            }

            long buildDurationMs = System.currentTimeMillis() - startBuild;
            assert benchmarkSlangIndex.size() == BENCHMARK_DATASET_SIZE : "Số lượng từ không đạt 100.000";
            System.out.printf("   --> Hoàn thành index 100.000 từ trong %d ms (%.2f từ/giây)%n",
                    buildDurationMs, (BENCHMARK_DATASET_SIZE * 1000.0) / buildDurationMs);
            passed++;

            // Test 2: Benchmark Tra cứu Slang Word O(1) & Prefix Autocomplete trên 100.000 từ
            System.out.println("[Test 2/5] Benchmark Tra cứu Slang Word & Autocomplete trên 100.000 từ (10.000 queries)...");
            Random rng = new Random(42);
            long totalLookupNano = 0;
            long maxLookupNano = 0;

            for (int i = 0; i < BENCHMARK_QUERY_ROUNDS; i++) {
                String queryWord = sampleWords.get(rng.nextInt(BENCHMARK_DATASET_SIZE));
                long t0 = System.nanoTime();
                SlangEntry result = benchmarkSlangIndex.get(queryWord);
                long elapsed = System.nanoTime() - t0;

                totalLookupNano += elapsed;
                if (elapsed > maxLookupNano) {
                    maxLookupNano = elapsed;
                }
                assert result != null && result.getWord().equals(queryWord) : "Tra cứu sai từ";
            }

            double avgLookupMicro = (totalLookupNano / (double) BENCHMARK_QUERY_ROUNDS) / 1_000.0;
            double maxLookupMicro = maxLookupNano / 1_000.0;
            System.out.printf("   --> Slang O(1) Lookup: Trung bình: %.3f µs/lần | Cực đại: %.3f µs/lần%n", avgLookupMicro, maxLookupMicro);
            System.out.printf("   --> Yêu cầu đề bài: < 1.000.000 µs (1s) | Thực tế: %.3f µs (Nhanh gấp ~%.0f lần đề bài)%n",
                    avgLookupMicro, 1_000_000.0 / avgLookupMicro);
            assert avgLookupMicro < 1000.0 : "Tốc độ trung bình tra cứu Slang vượt quá 1ms";

            // Benchmark Autocomplete Prefix Search
            long tPrefix0 = System.nanoTime();
            List<String> suggestions = benchmarkTrie.suggest("SLANG_100", 10);
            long prefixElapsedMicro = (System.nanoTime() - tPrefix0) / 1_000;
            assert !suggestions.isEmpty() : "Không tìm thấy gợi ý tiền tố";
            System.out.printf("   --> Autocomplete Trie Prefix Lookup: %d µs (%s)%n", prefixElapsedMicro, suggestions);
            assert prefixElapsedMicro < 50_000 : "Tốc độ autocomplete vượt quá 50ms";
            passed++;

            // Test 3: Benchmark Tra cứu Definition qua Inverted Index trên 100.000 từ
            System.out.println("[Test 3/5] Benchmark Tra cứu Definition trên 100.000 từ...");
            long totalDefTimeNano = 0;
            int defQueryCount = 0;

            for (String kw : commonKeywords) {
                long t0 = System.nanoTime();
                List<SlangEntry> matches = benchmarkDefIndex.search(kw, benchmarkSlangIndex);
                long elapsed = System.nanoTime() - t0;
                totalDefTimeNano += elapsed;
                defQueryCount++;

                assert !matches.isEmpty() : "Không tìm thấy từ cho keyword: " + kw;
                assert matches.size() == 10_000 : "Số lượng kết quả không khớp: " + matches.size();
            }

            double avgDefMs = (totalDefTimeNano / (double) defQueryCount) / 1_000_000.0;
            System.out.printf("   --> Tìm kiếm Definition: trung bình %.2f ms/lượt query (khớp chính xác 10.000 từ/keyword)%n", avgDefMs);
            System.out.printf("   --> Yêu cầu đề bài: < 1.000 ms (1s) | Thực tế: %.2f ms (Nhanh gấp ~%.0f lần đề bài)%n",
                    avgDefMs, 1000.0 / avgDefMs);
            assert avgDefMs < 100.0 : "Tốc độ tìm kiếm definition vượt quá 100ms";
            passed++;

            // Test 4: Kiểm thử Tính bền vững dữ liệu (Data Persistence) qua Chu kỳ Khởi động lại
            System.out.println("[Test 4/5] Kiểm thử Độ bền Dữ liệu (Persistence / Safe File Saving)...");
            SlangDictionaryModel liveModel = new SlangDictionaryModel();
            liveModel.initialize();
            int originalSize = liveModel.size();

            // Thêm một từ kiểm thử duy nhất
            String testWord = "M5_TEST_PERSIST_" + System.currentTimeMillis();
            List<String> testMeanings = List.of("Automated persistence check for Milestone 5");
            liveModel.addSlangWord(testWord, testMeanings, false);

            assert liveModel.size() == originalSize + 1 : "Size không tăng sau khi thêm";
            assert liveModel.containsWord(testWord) : "Không tìm thấy từ vừa thêm trong RAM";

            // Giả lập tắt ứng dụng và khởi tạo lại Model mới hoàn toàn
            SlangDictionaryModel reloadedModel = new SlangDictionaryModel();
            reloadedModel.initialize();

            assert reloadedModel.containsWord(testWord) : "Từ mới thêm không tồn tại sau khi mở lại ứng dụng!";
            assert reloadedModel.searchBySlangWord(testWord).getResults().get(0).getMeaningsFormatted()
                    .contains("Automated persistence check") : "Nội dung định nghĩa bị sai lệch";

            // Dọn dẹp từ kiểm thử để trả về trạng thái sạch
            reloadedModel.deleteSlangWord(testWord);
            assert !reloadedModel.containsWord(testWord) : "Không xóa được từ kiểm thử";
            passed++;

            // Test 5: Kiểm thử Tự phục hồi Index (Self-recovery / Cache Invalidation)
            System.out.println("[Test 5/5] Kiểm thử Khả năng Tự phục hồi khi Cache Index bị Xóa hoặc Hỏng...");
            Path cacheFile = Constants.INDEX_CACHE_DIR.resolve("index_cache.ser");
            Path metaFile = Constants.INDEX_CACHE_DIR.resolve("metadata.properties");

            // Xóa cache và metadata nếu tồn tại
            if (Files.exists(cacheFile)) Files.delete(cacheFile);
            if (Files.exists(metaFile)) Files.delete(metaFile);

            assert !Files.exists(cacheFile) : "Không thể xóa cache file phục vụ test";

            // Khởi động Model khi không có cache -> Hệ thống phải tự nhận diện và Rebuild tự động
            long startRebuild = System.currentTimeMillis();
            SlangDictionaryModel recoveryModel = new SlangDictionaryModel();
            recoveryModel.initialize();
            long rebuildDuration = System.currentTimeMillis() - startRebuild;

            assert recoveryModel.size() == originalSize : "Số lượng từ sau khi tự phục hồi không khớp";
            assert Files.exists(cacheFile) : "Hệ thống không tự tạo lại cache file sau khi rebuild";
            assert Files.exists(metaFile) : "Hệ thống không tự tạo lại metadata file";
            System.out.printf("   --> Tự phục hồi và tạo mới cache thành công trong %d ms (Đảm bảo độ tin cậy 100%%)%n", rebuildDuration);
            passed++;

        } catch (Throwable t) {
            System.err.println("❌ KIỂM THỬ THẤT BẠI:");
            t.printStackTrace();
            System.exit(1);
        } finally {
            // Hoàn nguyên tệp dữ liệu sạch ban đầu để đảm bảo repo luôn sạch
            try {
                if (userFileBackup != null) {
                    Files.write(Constants.SLANG_USER_FILE, userFileBackup);
                }
                if (historyBackup != null) {
                    Files.write(Constants.SEARCH_HISTORY_FILE, historyBackup);
                }
                // Khởi tạo lại một lần để cache khớp đúng với bản sao hoàn nguyên
                SlangDictionaryModel cleanModel = new SlangDictionaryModel();
                cleanModel.initialize();
            } catch (Exception ignored) {
            }
        }

        System.out.println("================================================================================");
        System.out.printf(" KẾT QUẢ: %d/%d TESTS PASSED - ĐẠT CHUẨN NGHIỆM THU MILESTONE 5 HOÀN HẢO!%n", passed, total);
        System.out.println("================================================================================");
    }
}
