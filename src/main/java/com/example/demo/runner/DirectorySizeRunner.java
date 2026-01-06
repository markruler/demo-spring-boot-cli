package com.example.demo.runner;

import com.example.demo.service.DirectorySizeService;
import com.example.demo.util.SizeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DirectorySizeRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DirectorySizeRunner.class);

    private final DirectorySizeService directorySizeService;

    public DirectorySizeRunner(DirectorySizeService directorySizeService) {
        this.directorySizeService = directorySizeService;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String directoryPath = args[0];

        try {
            long sizeInBytes = directorySizeService.calculateSize(directoryPath);
            String formattedSize = SizeFormatter.format(sizeInBytes);

            log.info("디렉토리 경로: {}", directoryPath);
            log.info("총 용량: {} ({} bytes)", formattedSize, sizeInBytes);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            log.error("디렉토리 용량 계산 중 문제가 발생했습니다.");
            log.error("상세: {}", e.getMessage());
            System.exit(1);
        }
    }

    private void printUsage() {
        log.info("사용법: java -jar <jar파일> <디렉토리경로>");
        log.info("예시: java -jar demo-0.0.1-SNAPSHOT.jar /Users/username/Documents\n");
        log.info("디렉토리 경로를 인자로 전달해주세요.");
    }
}

