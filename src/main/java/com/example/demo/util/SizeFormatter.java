package com.example.demo.util;

public class SizeFormatter {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;

    /**
     * 바이트 크기를 사람이 읽기 쉬운 형식으로 변환합니다.
     *
     * @param bytes 바이트 크기
     * @return 포맷된 문자열 (예: "1.5 GB", "500 MB")
     */
    public static String format(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }

        if (bytes < KB) {
            return bytes + " B";
        } else if (bytes < MB) {
            return String.format("%.2f KB", bytes / (double) KB);
        } else if (bytes < GB) {
            return String.format("%.2f MB", bytes / (double) MB);
        } else if (bytes < TB) {
            return String.format("%.2f GB", bytes / (double) GB);
        } else {
            return String.format("%.2f TB", bytes / (double) TB);
        }
    }
}

