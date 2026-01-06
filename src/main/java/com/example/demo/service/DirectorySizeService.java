package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DirectorySizeService {

    /**
     * 디렉토리의 총 용량을 바이트 단위로 계산합니다.
     *
     * @param directoryPath 디렉토리 경로
     * @return 디렉토리의 총 용량 (바이트)
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public long calculateSize(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("디렉토리가 존재하지 않습니다: " + directoryPath);
        }

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("디렉토리가 아닙니다: " + directoryPath);
        }

        return calculateSizeRecursive(path);
    }

    /**
     * 재귀적으로 디렉토리 내 모든 파일의 크기를 계산합니다.
     *
     * @param path 디렉토리 경로
     * @return 총 용량 (바이트)
     * @throws IOException 파일 시스템 접근 오류 시
     */
    private long calculateSizeRecursive(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }

        if (Files.isDirectory(path)) {
            try {
                return Files.list(path)
                        .mapToLong(child -> {
                            try {
                                return calculateSizeRecursive(child);
                            } catch (IOException e) {
                                // 개별 파일/디렉토리 접근 오류는 무시하고 계속 진행
                                System.err.println("경고: " + child + " 접근 실패 - " + e.getMessage());
                                return 0;
                            }
                        })
                        .sum();
            } catch (IOException e) {
                throw new IOException("디렉토리 읽기 실패: " + path, e);
            }
        }

        return 0;
    }
}

