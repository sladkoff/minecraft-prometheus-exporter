package de.sldk.mc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.Random;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;

public class FileInspectorTest {
    FileInspector fileInspector;
    Path path;

    @AfterEach
    public void afterEach() {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) { }
        }
    }

    @Nested
    public class WhenPathIsNull {
        @Test
        public void shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> new FileInspector(null));
        }
    }

    @Nested
    public class WhenPathDoesNotExist {
        @BeforeEach
        public void beforeEach() throws IOException {
            path = new File("./some/random/path/that/surely/does/not/exist.bf").toPath();
            fileInspector = new FileInspector(path);
        }

        @Test
        public void returnsSizeZero() throws IOException {
            assertEquals(0, fileInspector.getSize());
        }
    }

    @Nested
    public class WhenPathIsFile {
        @BeforeEach
        public void beforeEach() throws IOException {
            path = File.createTempFile("test", ".txt").toPath();
            fileInspector = new FileInspector(path);
        }

        @Test
        public void returnsZeroIfFileIsEmpty() throws IOException {
            assertEquals(0, fileInspector.getSize());
        }

        @Test
        @RepeatedTest(10)
        public void returnsFileSize() throws IOException {
            int length = new Random().nextInt(10000);
            FileWriter fileWriter = new FileWriter(path.toFile());
            fileWriter.write(new RandomString(length).nextString());
            fileWriter.close();
            assertEquals(length, fileInspector.getSize());
        }
    }

    @Nested
    public class WhenPathIsDirectory {
        @BeforeEach
        public void beforeEach() throws IOException {
            path = Files.createTempDirectory("test-");
            fileInspector = new FileInspector(path);
        }

        @Test
        public void returnsZeroIfDirectoryIsEmpty() throws IOException {
            assertEquals(0, fileInspector.getSize());
        }

        @Test
        public void returnsSizeOfSingleFile() throws IOException {
            long length = createMultipleFilesInTmpDirectory(path, 1);
            assertEquals(length, fileInspector.getSize());
        }

        @Test
        public void returnsSizeOfMultipleFiles() throws IOException {
            int files = new Random().ints(2, 10).findFirst().getAsInt();
            long length = createMultipleFilesInTmpDirectory(path, files);
            assertEquals(length, fileInspector.getSize());
        }

        @Test
        public void returnSizeOfNestedDirectories() throws IOException {
            int directories = new Random().ints(2, 10).findFirst().getAsInt();
            long length = createMultiplePopulatedDirectories(path, directories);
            assertEquals(length, fileInspector.getSize());
        }
    }

    private static long createMultiplePopulatedDirectories(Path path, int directories) throws IOException {
        long length = 0;
        for (int i = 0; i < directories; i++) {
            int files = new Random().ints(2, 10).findFirst().getAsInt();
            Path directory = Files.createTempDirectory(path, "dir" + i + "-");
            length += createMultipleFilesInTmpDirectory(directory, files);
        }
        return length;
    }

    private static long createMultipleFilesInTmpDirectory(Path path, int files) throws IOException {
        int totalLength = 0;
        for (int i = 0; i < files; i++) {
            int length = new Random().nextInt(10000);
            createFileInTmpDirectory(path, "test" + i + ".txt", length);
            totalLength += length;
        }
        return totalLength;
    }

    private static File createFileInTmpDirectory(Path path, String name, int length) throws IOException {
        File file = path.resolve(name).toFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new RandomString(length).nextString());
        fileWriter.close();
        return file;
    }
}
