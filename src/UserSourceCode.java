import java.util.Scanner;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.util.Vector;
import java.nio.file.Files;

public class UserSourceCode {
    public static final String END = "//END-LIB:";
    public static final String BEGIN = "//BEGIN-LIB:";
    public static final String FROM = "//FROM-FILE:";
    public static final String SAVES = "saves";
    // repo: absolute path string to the lib repository
    // src: path to the text file in which the user is typing
    // add: paths received as arguments to be added
    public static void insert (Path repo, Path src, List<Path> toAdd) throws IOException, CycleFoundException {
        Scanner in = new Scanner(src);
        StringBuilder[] lines = new StringBuilder[3];
        for (int i = 0; i < 3; i++) {
            lines[i] = new StringBuilder();
        }
        Set<Path> included = new HashSet<Path>();
        for (int section = 0; in.hasNext(); ) {
            String line = in.nextLine();
            switch (line) {
                case BEGIN:
                case END:
                    section++;
                break;
                default:
                    if (line.startsWith(FROM)) {
                        String sub = line.substring(FROM.length());
                        included.add(repo.resolve(sub));
                    }
                    lines[section].append(line);
                    lines[section].append('\n');
            }
        }
        in.close();
        LinkedList<Path> queue = new LinkedList<>();
        Graph<Path> dependencies = new Graph<>();
        for (Path p : toAdd) {
            if (!included.contains(p)) {
                dependencies.indexOf(p);
                queue.addLast(p);
            }
        }
        while (!queue.isEmpty()) {
            Path to = queue.remove();
            for (Path from : LibraryCode.getDependencies(to)) {
                if (!included.contains(from)) {
                    dependencies.addArc(from, to);
                    queue.addLast(from);
                }
            }
        }
        Vector<Path> order = dependencies.topologicalSort();
        for (Path p : order) {
            lines[1].append(FROM + repo.relativize(p).toString())
                    .append('\n')
                    .append(LibraryCode.getContentToCopy(p))
                    .append('\n');
        }
        StringBuilder join = lines[0].append(BEGIN)
                                     .append('\n')
                                     .append(lines[1])
                                     .append(END)
                                     .append('\n')
                                     .append(lines[2]);
        Files.writeString(src, join);
    }
    public static void clear (Path src) throws IOException {
        StringBuilder join = new StringBuilder();
        Scanner in = new Scanner(src);
        boolean ignore = false;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            switch (line) {
                case BEGIN:
                    join.append(BEGIN).append('\n');
                    ignore = true;
                    break;
                case END:
                    join.append(END).append('\n');
                    ignore = false;
                    break;
                default:
                    if (!ignore) join.append(line).append('\n');
            }
        }
        in.close();
        Files.writeString(src, join);
    }
    public static void hide (Path src) throws IOException {
        StringBuilder join = new StringBuilder();
        Scanner in = new Scanner(src);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.startsWith(FROM)) {
                join.append('\n');
            } else if (!line.equals(BEGIN) && !line.equals(END)) {
                join.append(line).append('\n');
            }
        }
        in.close();
        Files.writeString(src, join);
    }
}