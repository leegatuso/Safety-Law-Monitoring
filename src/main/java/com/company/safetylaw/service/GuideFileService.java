package com.company.safetylaw.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Service
public class GuideFileService {
    private final Path directory;

    public GuideFileService(@Value("${app.files-dir}") String directory) throws IOException {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        Files.createDirectories(this.directory);
    }

    public Path path(long id) {
        if (id < 1) throw new IllegalArgumentException("파일 번호 오류");
        return directory.resolve(id + ".pdf");
    }

    public boolean exists(long id) {
        return Files.isRegularFile(path(id));
    }

    public byte[] read(long id) throws IOException {
        return exists(id) ? Files.readAllBytes(path(id)) : null;
    }

    public void write(long id, byte[] bytes) throws IOException {
        if (bytes == null) {
            Files.deleteIfExists(path(id));
            return;
        }
        Path temp = Files.createTempFile(directory, "upload-", ".pdf");
        try {
            Files.write(temp, bytes);
            Files.move(temp, path(id), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public String extract(byte[] bytes) {
        if (bytes.length > 20 * 1024 * 1024
                || bytes.length < 5
                || !new String(bytes, 0, 5, StandardCharsets.US_ASCII).equals("%PDF-"))
            throw new IllegalArgumentException("20MB 이하의 유효한 PDF를 선택하세요.");
        try (var pdf = Loader.loadPDF(bytes)) {
            return new PDFTextStripper().getText(pdf);
        } catch (IOException ex) {
            throw new IllegalArgumentException("PDF 암호 설정이나 파일 손상을 확인하세요.");
        }
    }
}
