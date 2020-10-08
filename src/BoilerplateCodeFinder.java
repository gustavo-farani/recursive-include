import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.io.IOException;

public class BoilerplateCodeFinder extends SimpleFileVisitor<Path> {
    private String code;
    public BoilerplateCodeFinder () {
        this.code = "#include <bits/stdc++.h>\nusing namespace std;";
    }
    @Override
    public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) {
        try {
            if (file.getFileName().toString().equals(LibraryCode.BOILERPLATE)) {
                this.code = Files.readString(file);
            }
            return FileVisitResult.CONTINUE;
        } catch (IOException e) {
            return FileVisitResult.TERMINATE;
        }
    }
    public String getResult () {
        return this.code;
    }
}