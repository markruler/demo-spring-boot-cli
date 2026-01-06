package com.example.demo.runner;

import com.example.demo.service.CommandHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 디렉토리 용량 계산 명령어
 */
@Component
public class DirectorySizeRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DirectorySizeRunner.class);

    private final CommandHandlerService commandHandlerService;

    public DirectorySizeRunner(CommandHandlerService commandHandlerService) {
        this.commandHandlerService = commandHandlerService;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "size":
                    commandHandlerService.handleSizeCommand(args);
                    break;
                case "list":
                    commandHandlerService.handleListCommand(args);
                    break;
                case "top":
                    commandHandlerService.handleTopCommand(args);
                    break;
                case "find":
                    commandHandlerService.handleFindCommand(args);
                    break;
                default:
                    // 기본 동작: 첫 번째 인자를 디렉토리 경로로 간주
                    if (args.length == 1) {
                        commandHandlerService.handleSizeCommand(new String[]{"size", args[0]});
                    } else {
                        log.error("알 수 없는 명령어: {}", command);
                        printUsage();
                        System.exit(1);
                    }
            }
        } catch (IOException e) {
            log.error("오류: 디렉토리 용량 계산 중 문제가 발생했습니다.");
            log.error("상세: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            log.error("오류: {}", e.getMessage());
            System.exit(1);
        }
    }


    private void printUsage() {
        log.info("디렉토리 용량 계산 CLI 툴");
        log.info("==========================================");
        log.info("사용법:");
        log.info("  size <디렉토리경로>              - 디렉토리 총 용량 계산");
        log.info("  list <디렉토리경로>              - 디렉토리 내 항목 목록과 용량 표시");
        log.info("  top <디렉토리경로> [상위N개]     - 가장 큰 파일/디렉토리 상위 N개 표시 (기본: 10)");
        log.info("  find <디렉토리경로> <최소크기>   - 특정 크기 이상의 파일 찾기");
        log.info("");
        log.info("예시:");
        log.info("  java -jar demo.jar size /Users/username/Documents");
        log.info("  java -jar demo.jar list /Users/username/Documents");
        log.info("  java -jar demo.jar top /Users/username/Documents 20");
        log.info("  java -jar demo.jar find /Users/username/Documents 100MB");
        log.info("  java -jar demo.jar find /Users/username/Documents 1GB");
        log.info("");
        log.info("참고: 명령어 없이 디렉토리 경로만 입력하면 size 명령어로 실행됩니다.");
    }
}
