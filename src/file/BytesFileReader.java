package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BytesFileReader {

    public static byte[] read(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
