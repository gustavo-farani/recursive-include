import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.FileVisitResult;
import java.util.LinkedList;

public class SmartFileSearch extends SimpleFileVisitor<Path> {
    private String pattern;
    private LinkedList<Path> matches;
    public SmartFileSearch (String pattern) {
        this.pattern = pattern;
        this.matches = new LinkedList<Path>();
    }
    public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) {
        if (file.toString().contains(pattern)) {
            matches.add(file);
        }
        return FileVisitResult.CONTINUE;
    }
    public LinkedList<Path> getResult () {
        return this.matches;
    }
}