package de.sldk.mc.utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class PathUtils {
    private Path path;

    public PathUtils(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null!");
        }
        this.path = path;
    }

    public long getSize() throws IOException {
        final AtomicLong size = new AtomicLong(0);
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return size.get();
    }
}
