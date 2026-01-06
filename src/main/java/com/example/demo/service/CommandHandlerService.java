package com.example.demo.service;

import com.example.demo.model.FileInfo;
import com.example.demo.util.SizeFormatter;
import com.example.demo.util.SizeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CommandHandlerService {

    private static final Logger log = LoggerFactory.getLogger(CommandHandlerService.class);
    private static final String LINE = "----------------------------------------";

    private final DirectorySizeService directorySizeService;
    private final SizeParser sizeParser;

    public CommandHandlerService(DirectorySizeService directorySizeService, SizeParser sizeParser) {
        this.directorySizeService = directorySizeService;
        this.sizeParser = sizeParser;
    }

    /**
     * size 명령어를 처리합니다.
     *
     * @param args 명령어 인자
     * @throws IllegalArgumentException 인자가 부족한 경우
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public void handleSizeCommand(String[] args) throws IOException {
        validateArgs(args, 2, "사용법: size <디렉토리경로>");

        String directoryPath = args[1];
        long sizeInBytes = directorySizeService.calculateSize(directoryPath);
        String formattedSize = SizeFormatter.format(sizeInBytes);

        log.info("디렉토리 경로: {}", directoryPath);
        log.info("총 용량: {} ({} bytes)", formattedSize, sizeInBytes);
    }

    /**
     * list 명령어를 처리합니다.
     *
     * @param args 명령어 인자
     * @throws IllegalArgumentException 인자가 부족한 경우
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public void handleListCommand(String[] args) throws IOException {
        validateArgs(args, 2, "사용법: list <디렉토리경로>");

        String directoryPath = args[1];
        List<FileInfo> fileInfos = directorySizeService.listDirectory(directoryPath);

        log.info("디렉토리 경로: {}", directoryPath);
        printTableHeader("이름", "용량", "타입");

        fileInfos.stream()
                .sorted((a, b) -> Long.compare(b.getSize(), a.getSize()))
                .forEach(fileInfo -> {
                    String name = fileInfo.getPath().getFileName().toString();
                    String size = SizeFormatter.format(fileInfo.getSize());
                    String type = fileInfo.isDirectory() ? "[DIR]" : "[FILE]";
                    printTableRow(name, size, type);
                });

        long totalSize = fileInfos.stream().mapToLong(FileInfo::getSize).sum();
        log.info(LINE);
        log.info("총 용량: {} ({} bytes)", SizeFormatter.format(totalSize), totalSize);
    }

    /**
     * top 명령어를 처리합니다.
     *
     * @param args 명령어 인자
     * @throws IllegalArgumentException 인자가 부족한 경우
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public void handleTopCommand(String[] args) throws IOException {
        validateArgs(args, 2, "사용법: top <디렉토리경로> [상위N개]\n예시: top /path/to/dir 10");

        String directoryPath = args[1];
        int topN = args.length >= 3 ? Integer.parseInt(args[2]) : 10;

        List<FileInfo> topFiles = directorySizeService.getTopFiles(directoryPath, topN);

        log.info("디렉토리 경로: {}", directoryPath);
        log.info("상위 {}개 가장 큰 파일/디렉토리:", topN);
        printTableHeader("경로", "용량", "타입");

        topFiles.forEach(fileInfo -> {
            String path = fileInfo.getPath().toString();
            String size = SizeFormatter.format(fileInfo.getSize());
            String type = fileInfo.isDirectory() ? "[DIR]" : "[FILE]";
            printTableRow(path, size, type);
        });
    }

    /**
     * find 명령어를 처리합니다.
     *
     * @param args 명령어 인자
     * @throws IllegalArgumentException 인자가 부족하거나 잘못된 크기 형식인 경우
     * @throws IOException 파일 시스템 접근 오류 시
     */
    public void handleFindCommand(String[] args) throws IOException {
        validateArgs(args, 3, "사용법: find <디렉토리경로> <최소크기>\n예시: find /path/to/dir 100MB\n      find /path/to/dir 1GB");

        String directoryPath = args[1];
        String sizeStr = args[2].toUpperCase();
        long minSizeBytes = sizeParser.parseSize(sizeStr);

        List<FileInfo> largeFiles = directorySizeService.findLargeFiles(directoryPath, minSizeBytes);

        log.info("디렉토리 경로: {}", directoryPath);
        log.info("최소 크기: {} ({} bytes)", sizeStr, minSizeBytes);
        log.info("조건에 맞는 파일/디렉토리: {}개", largeFiles.size());
        printTableHeader("경로", "용량", "타입");

        largeFiles.forEach(fileInfo -> {
            String path = fileInfo.getPath().toString();
            String size = SizeFormatter.format(fileInfo.getSize());
            String type = fileInfo.isDirectory() ? "[DIR]" : "[FILE]";
            printTableRow(path, size, type);
        });
    }

    /**
     * 인자 개수를 검증합니다.
     *
     * @param args 명령어 인자
     * @param minArgs 최소 인자 개수
     * @param errorMessage 에러 메시지
     */
    private void validateArgs(String[] args, int minArgs, String errorMessage) {
        if (args.length < minArgs) {
            log.error(errorMessage);
            System.exit(1);
        }
    }

    /**
     * 테이블 헤더를 출력합니다.
     *
     * @param col1 첫 번째 컬럼 제목
     * @param col2 두 번째 컬럼 제목
     * @param col3 세 번째 컬럼 제목
     */
    private void printTableHeader(String col1, String col2, String col3) {
        log.info(LINE);
        log.info(String.format("%-50s %15s %10s", col1, col2, col3));
        log.info(LINE);
    }

    /**
     * 테이블 행을 출력합니다.
     *
     * @param col1 첫 번째 컬럼 값
     * @param col2 두 번째 컬럼 값
     * @param col3 세 번째 컬럼 값
     */
    private void printTableRow(String col1, String col2, String col3) {
        log.info(String.format("%-50s %15s %10s", col1, col2, col3));
    }
}

