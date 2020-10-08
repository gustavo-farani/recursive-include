import java.io.File;

public class RecursiveDirectoryDeleter {
    public static void deleteIgnoreHidden (File dir) {
        File[] adj = dir.listFiles();
        for (File file : adj) {
            if (file.isHidden()) continue;
            if (file.isDirectory()) deleteIgnoreHidden(file);
            file.delete();
        }
    }
}