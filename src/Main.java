import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.List;
import java.util.Properties;
import java.util.LinkedList;
import java.io.File;

public class Main {
    public static void main (String[] args) {
        try {
            // Starting up
            String cp = System.getProperty("java.class.path");
            Properties defaultProperties = new Properties();
            defaultProperties.load(new FileInputStream(new File(cp, "Default.properties")));
            Properties userProperties = new Properties(defaultProperties);
            File propertiesSavings = new File(cp, "User.properties"); 
            userProperties.load(new FileInputStream(propertiesSavings));
            Path repositoryFolder = Paths.get(userProperties.getProperty("repositoryFolder"));
            Path userWorkFile = Paths.get(userProperties.getProperty("userWorkFile"));
            // Running
            if (args.length == 0) {
                throw new InvalidCommandException();
            }
            Scanner in;
            switch (args[0]) {
                case "add":
                    in = new Scanner(System.in);
                    List<Path> queue = new LinkedList<Path>();
                    System.out.print("> ");
                    while (in.hasNext()) {
                        SmartFileSearch visitor = new SmartFileSearch(in.next());
                        Files.walkFileTree(repositoryFolder, visitor);
                        LinkedList<Path> matches = visitor.getResult();
                        if (matches.size() > 1) {
                            System.out.println("Match was ambiguous:");
                            for (Path file : matches) {
                                System.out.println(repositoryFolder.relativize(file));
                            }
                        } else if (matches.size() == 0) {
                            System.out.println("Nothing found.");
                        } else {
                            queue.add(matches.getLast());
                            System.out.print("File ");
                            System.out.print(matches.getLast().getFileName().toString());
                            System.out.println(" has been added to the queue.");
                        }
                        System.out.print("> ");
                    }
                    System.out.println();
                    UserSourceCode.insert(repositoryFolder,
                                          userWorkFile,
                                          queue);
                    break;
                case "start":
                    File workspaceFolder = userWorkFile.getParent().toFile();
                    RecursiveDirectoryDeleter.deleteIgnoreHidden(workspaceFolder);
                    File savesFolder = new File(workspaceFolder, UserSourceCode.SAVES);
                    savesFolder.mkdir();
                case "new":
                    BoilerplateCodeFinder visitor = new BoilerplateCodeFinder();
                    Files.walkFileTree(repositoryFolder, visitor);
                    String code = visitor.getResult()
                                + "\n\n"
                                + UserSourceCode.BEGIN
                                + "\n"
                                + UserSourceCode.END
                                + "\n\nint main () {\n    \n}";
                    Files.writeString(userWorkFile, code);
                    break;
                case "clear":
                    UserSourceCode.clear(userWorkFile);
                    break;
                case "hide":
                    UserSourceCode.hide(userWorkFile);
                    break;
                case "save":
                    if (args.length == 1) {
                        throw new InvalidCommandException();
                    }
                    Files.copy(userWorkFile,
                               userWorkFile.getParent()
                                           .resolve(UserSourceCode.SAVES)
                                           .resolve(args[1]),
                               StandardCopyOption.REPLACE_EXISTING);
                    break;
                case "load":
                    if (args.length == 1) {
                        throw new InvalidCommandException();
                    }
                    Files.copy(userWorkFile.getParent()
                                           .resolve(UserSourceCode.SAVES)
                                           .resolve(args[1]),
                               userWorkFile,
                               StandardCopyOption.REPLACE_EXISTING);
                    break;
                case "workspace":
                    System.out.println("Currently set workspace directory:");
                    System.out.println(userWorkFile.getParent().toString());
                    System.out.print("Input a new path for the workspace directory,");
                    System.out.println(" or an empty string to keep it unchanged:");
                    in = new Scanner(System.in);
                    String workspaceName = in.nextLine();
                    if (!workspaceName.isEmpty()) {
                        userWorkFile = Paths.get(workspaceName)
                                            .toAbsolutePath()
                                            .normalize()
                                            .resolve("src.cpp");
                        System.out.println("Workspace directory has been set successfully to:");
                        System.out.println(userWorkFile.getParent().toString());
                    } else {
                        System.out.println("Workspace directory was kept unchanged.");
                    }
                    break;
                case "repository":
                    System.out.println("Currently set repository directory:");
                    System.out.println(repositoryFolder.toString());
                    System.out.print("Input a new path for the repository directory,");
                    System.out.println(" or an empty string to keep it unchanged:");
                    in = new Scanner(System.in);
                    String repositoryName = in.nextLine();
                    if (!repositoryName.isEmpty()) {
                        repositoryFolder = Paths.get(repositoryName)
                                            .toAbsolutePath()
                                            .normalize();
                        System.out.println("Repository directory has been set successfully to:");
                        System.out.println(repositoryFolder.toString());
                    } else {
                        System.out.println("Repository directory was kept unchanged.");
                    }
                    break;
                default:
                    throw new InvalidCommandException();
            }
            // Exiting
            userProperties.setProperty("repositoryFolder", repositoryFolder.toString());
            userProperties.setProperty("userWorkFile", userWorkFile.toString());
            userProperties.store(new FileOutputStream(propertiesSavings), "Saved paths");
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        } catch (InvalidCommandException e2) {
            System.err.println(e2.getMessage());
        } catch (CycleFoundException e3) {
            System.err.println(e3.getMessage());
        }
    }
}
