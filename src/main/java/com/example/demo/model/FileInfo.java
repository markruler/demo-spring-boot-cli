package com.example.demo.model;

import java.nio.file.Path;

public class FileInfo {
    private final Path path;
    private final long size;
    private final boolean isDirectory;

    public FileInfo(Path path, long size, boolean isDirectory) {
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    public Path getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}

