package cullinan.helpers.other;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DirectoryCopier {
    public static void copyFolder(Path src, Path dest) throws IOException {
        deleteFolder(new File(dest.toUri()));
        Files.createDirectories(dest);

        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }

    private static void deleteFolder(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteFolder(f);
            }
        }
        file.delete();
    }

    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
