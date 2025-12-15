package com.talet.talet.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BookReader {
    private final Path basePath;

    // 생성자 주입으로 Path 준비
    public BookReader(@Value("${storage.book-root-dir}") String bookDir) {
        this.basePath = Paths.get(bookDir).toAbsolutePath().normalize();
    }

    public String read(String bookName, String fileName) throws Exception {
        Path path = basePath.resolve(bookName).resolve(fileName + ".txt").normalize();
        // 디렉터리 탈출 방지
        if (!path.startsWith(basePath)) {
            throw new SecurityException("허용 디렉터리 밖 접근");
        }
        // 존재/파일 여부 확인
        if (Files.notExists(path) || !Files.isRegularFile(path)) {
            throw new NoSuchFileException("파일이 존재하지 않거나 일반 파일이 아님: " + path);
        }
        // 인코딩 명시(줄바꿈, 한글 포함 그대로 읽힘)
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
