package com.example.demo.service;

import com.example.demo.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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

    /**
     * 디렉토리 내 직접 하위 항목들의 정보를 반환합니다.
     *
     * @param directoryPath 디렉토리 경로
     * @return 파일/디렉토리 정보 리스트
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public List<FileInfo> listDirectory(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("디렉토리가 존재하지 않습니다: " + directoryPath);
        }

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("디렉토리가 아닙니다: " + directoryPath);
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        try (Stream<Path> paths = Files.list(path)) {
            paths.forEach(child -> {
                try {
                    long size = Files.isDirectory(child) 
                        ? calculateSizeRecursive(child) 
                        : Files.size(child);
                    fileInfos.add(new FileInfo(child, size, Files.isDirectory(child)));
                } catch (IOException e) {
                    // 개별 항목 접근 오류는 무시
                }
            });
        }

        return fileInfos;
    }

    /**
     * 디렉토리 내 가장 큰 파일/디렉토리 상위 N개를 반환합니다.
     *
     * @param directoryPath 디렉토리 경로
     * @param topN 상위 N개
     * @return 파일/디렉토리 정보 리스트 (크기 내림차순)
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public List<FileInfo> getTopFiles(String directoryPath, int topN) throws IOException {
        List<FileInfo> allFiles = getAllFilesRecursive(directoryPath);
        return allFiles.stream()
                .sorted(Comparator.comparingLong(FileInfo::getSize).reversed())
                .limit(topN)
                .toList();
    }

    /**
     * 특정 크기 이상의 파일을 찾습니다.
     *
     * @param directoryPath 디렉토리 경로
     * @param minSizeBytes 최소 크기 (바이트)
     * @return 조건에 맞는 파일 정보 리스트
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public List<FileInfo> findLargeFiles(String directoryPath, long minSizeBytes) throws IOException {
        List<FileInfo> allFiles = getAllFilesRecursive(directoryPath);
        return allFiles.stream()
                .filter(file -> file.getSize() >= minSizeBytes)
                .sorted(Comparator.comparingLong(FileInfo::getSize).reversed())
                .toList();
    }

    /**
     * 재귀적으로 디렉토리 내 모든 파일 정보를 수집합니다.
     *
     * @param directoryPath 디렉토리 경로
     * @return 모든 파일 정보 리스트
     * @throws IOException 파일 시스템 접근 오류 시
     */
    private List<FileInfo> getAllFilesRecursive(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("디렉토리가 존재하지 않습니다: " + directoryPath);
        }

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("디렉토리가 아닙니다: " + directoryPath);
        }

        List<FileInfo> allFiles = new ArrayList<>();
        collectFilesRecursive(path, allFiles);
        return allFiles;
    }

    /**
     * 재귀적으로 파일 정보를 수집합니다.
     *
     * @param path 현재 경로
     * @param fileInfos 수집된 파일 정보 리스트
     */
    private void collectFilesRecursive(Path path, List<FileInfo> fileInfos) {
        try {
            if (Files.isRegularFile(path)) {
                long size = Files.size(path);
                fileInfos.add(new FileInfo(path, size, false));
            } else if (Files.isDirectory(path)) {
                long size = calculateSizeRecursive(path);
                fileInfos.add(new FileInfo(path, size, true));

                try (Stream<Path> paths = Files.list(path)) {
                    paths.forEach(child -> collectFilesRecursive(child, fileInfos));
                }
            }
        } catch (IOException e) {
            // 개별 항목 접근 오류는 무시
        }
    }
}

