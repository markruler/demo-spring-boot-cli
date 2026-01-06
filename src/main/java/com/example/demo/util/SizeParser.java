package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class SizeParser {

    /**
     * 크기 문자열을 바이트로 변환합니다.
     *
     * @param sizeStr 크기 문자열 (예: "100MB", "1GB", "500KB")
     * @return 바이트 단위 크기
     * @throws IllegalArgumentException 잘못된 형식인 경우
     */
    public long parseSize(String sizeStr) {
        sizeStr = sizeStr.trim().toUpperCase();
        long multiplier = 1;

        if (sizeStr.endsWith("KB")) {
            multiplier = 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB")) {
            multiplier = 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("GB")) {
            multiplier = 1024L * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("TB")) {
            multiplier = 1024L * 1024 * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("B")) {
            sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
        }

        try {
            double value = Double.parseDouble(sizeStr.trim());
            return (long) (value * multiplier);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 크기 형식: " + sizeStr + ". 예: 100MB, 1GB, 500KB");
        }
    }
}

