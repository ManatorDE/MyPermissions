import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Minify {
    public static String minify(String html) {
        // Remove whitespace and newlines
        return html.replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Minify <file-path> || java Minify <folder-path>");
            return;
        } else {
            File f = new File(args[0]);
            if (f.isDirectory()) {
                // Minify all HTML files in the directory
                File[] files = f.listFiles((dir, name) -> name.endsWith(".html"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            String content = new String(Files.readAllBytes(file.toPath()));
                            String minified = minify(content);
                            String newFileName = file.getName().replace(".html", "-min.html");
                            Files.write(new File("../", newFileName).toPath(), minified.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // Minify the single HTML file
                try {
                    String content = new String(Files.readAllBytes(f.toPath()));
                    String minified = minify(content);
                    String newFileName = f.getName().replace(".html", "-min.html");
                    Files.write(new File("../", newFileName).toPath(), minified.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
