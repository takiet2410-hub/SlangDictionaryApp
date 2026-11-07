package vn.edu.hcmus.fit.dictionary.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Tiện ích xử lý file an toàn (Safe Atomic Write, SHA-256 Checksum, Directory creation).
 */
public final class FileUtils {
    private FileUtils() {
        // Chặn khởi tạo
    }

    /**
     * Đảm bảo thư mục cha tồn tại.
     */
    public static void ensureDirectory(Path dir) throws IOException {
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    /**
     * Tính toán mã băm SHA-256 của file để kiểm tra tính toàn vẹn và version cache.
     */
    public static String calculateSha256(Path file) throws IOException {
        if (!Files.exists(file)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream is = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Ghi file an toàn (Safe Atomic Write): Ghi ra file .tmp trước, sau đó atomic rename/replace
     * sang file đích. Tránh hỏng file khi gặp sự cố ngắt điện hoặc crash giữa chừng.
     */
    public static void safeWriteLines(Path targetFile, List<String> lines) throws IOException {
        if (targetFile.getParent() != null) {
            ensureDirectory(targetFile.getParent());
        }
        Path tempFile = targetFile.resolveSibling(targetFile.getFileName().toString() + ".tmp");
        
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }

        try {
            Files.move(tempFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileSystemException e) {
            // Fallback nếu OS (Windows) không hỗ trợ atomic move xuyên partition hoặc file lock tạm thời
            Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Sao chép file an toàn có ghi đè.
     */
    public static void copyFile(Path source, Path target) throws IOException {
        if (target.getParent() != null) {
            ensureDirectory(target.getParent());
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
