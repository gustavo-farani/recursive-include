import java.nio.file.Path;
import java.nio.file.Files;
import java.util.regex.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class LibraryCode {
    private static final Pattern INCLUDE_DIRECTIVE = Pattern.compile("#include \"(.*)\"\n");
    public static final String BOILERPLATE = "boilerplate.cpp";
    public static List<Path> getDependencies (Path file) throws IOException {
        Matcher tokens = INCLUDE_DIRECTIVE.matcher(Files.readString(file));
        LinkedList<Path> dependencies = new LinkedList<>();
        while (tokens.find()) {
            String rel = tokens.group(1);
            if (!rel.endsWith(BOILERPLATE)) {
                Path path = Path.of(file.getParent().toString(), tokens.group(1))
                                .normalize();
                dependencies.add(path);
            }
        }
        return dependencies;
    }
    public static String getContentToCopy (Path file) throws IOException {
        String text = Files.readString(file);
        return INCLUDE_DIRECTIVE.matcher(text).replaceAll("").strip();
    }
}