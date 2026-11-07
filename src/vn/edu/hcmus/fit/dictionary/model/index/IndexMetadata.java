package vn.edu.hcmus.fit.dictionary.model.index;

import java.io.Serial;
import java.io.Serializable;

/**
 * Metadata lưu thông tin phiên bản và SHA-256 của file dữ liệu nguồn.
 * Giúp xác thực tính toàn vẹn của Serialized Index Cache khi khởi động.
 */
public class IndexMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String version;
    private final String sourceSha256;
    private final int entryCount;
    private final long createdTimestamp;

    public IndexMetadata(String version, String sourceSha256, int entryCount) {
        this.version = (version != null) ? version : "1.0";
        this.sourceSha256 = (sourceSha256 != null) ? sourceSha256 : "";
        this.entryCount = entryCount;
        this.createdTimestamp = System.currentTimeMillis();
    }

    public String getVersion() {
        return version;
    }

    public String getSourceSha256() {
        return sourceSha256;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Kiểm tra xem metadata này có còn hợp lệ với mã băm hiện tại của file dữ liệu không.
     */
    public boolean isValidFor(String currentSha256) {
        if (currentSha256 == null || currentSha256.isBlank()) {
            return false;
        }
        return this.sourceSha256.equalsIgnoreCase(currentSha256.trim());
    }

    @Override
    public String toString() {
        return "IndexMetadata{" +
                "version='" + version + '\'' +
                ", sourceSha256='" + sourceSha256 + '\'' +
                ", entryCount=" + entryCount +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
