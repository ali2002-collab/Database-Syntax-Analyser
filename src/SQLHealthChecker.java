import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.*;

public class SQLHealthChecker {
    private String filePath; // Path to the SQL file being analyzed

    public SQLHealthChecker() {
    }

    // Displays the health of a selected database from a given directory
    public void displayDatabaseHealth(String directoryPath) {
        try {
            // List all available database files in the directory
            List<String> databases = listAvailableDatabases(directoryPath);
            if (databases.isEmpty()) {
                System.out.println("No databases available.");
                return;
            }

            // Display available databases to the user
            System.out.println("Available Databases:");
            for (int i = 0; i < databases.size(); i++) {
                System.out.println((i + 1) + ". " + databases.get(i));
            }

            // Prompt user to select a database
            System.out.print("Select a database number: ");
            int choice = new Scanner(System.in).nextInt();

            // Validate user selection
            if (choice < 1 || choice > databases.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            // Set the file path to the selected database
            this.filePath = Paths.get(directoryPath, databases.get(choice - 1)).toString();
            // Display the health report of the selected database
            this.displayHealth();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Returns a list of database file names in a directory
    private List<String> listAvailableDatabases(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile) // Filter only regular files
                    .map(path -> path.getFileName().toString()) // Get file names
                    .collect(Collectors.toList()); // Collect into a list
        }
    }

    // Displays the health report of the SQL database
    private void displayHealth() {
        try {
            // Evaluate and format the health report
            String healthReport = evaluateHealth();
            System.out.println("------------------");
            System.out.println("SQL Health Report for " + filePath + ":");
            System.out.println(healthReport);
            System.out.println("------------------");
            System.out.println("\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Evaluates the health of the SQL file based on linting errors
    private String evaluateHealth() throws IOException, InterruptedException {
        int totalLines = getTotalLines(); // Get total number of lines in the file
        int errors = getLintingErrors(); // Get total number of linting errors

        // Calculate error density and health score
        double errorDensity = Math.min((double) errors / totalLines, 1.0);
        double healthScore = 100 * (1 - errorDensity);

        // Determine color code based on health score
        String colorCode = getColorCode(healthScore);

        return String.format("Health: %.2f%%, Color Code: %s", healthScore, colorCode);
    }

    // Counts total number of lines in the SQL file
    private int getTotalLines() throws IOException {
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) lineCount++;
        }
        return lineCount;
    }

    // Runs SQLFluff lint command and returns the number of linting errors
    private int getLintingErrors() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "sqlfluff", "lint", "--dialect", "ansi", "--format", "json", filePath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Capture and parse the output from SQLFluff
        String output;
        StringBuilder result = new StringBuilder();
        while ((output = reader.readLine()) != null) {
            result.append(output);
        }

        process.waitFor();
        return parseErrorsFromOutput(result.toString());
    }

    // Parses the output from SQLFluff to count the number of errors
    private int parseErrorsFromOutput(String output) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(output);
            if (element.isJsonArray()) {
                JsonArray files = element.getAsJsonArray();
                if (!files.isEmpty()) {
                    JsonObject file = files.get(0).getAsJsonObject();
                    JsonArray violations = file.getAsJsonArray("violations");
                    return violations.size(); // Return the count of violations
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        }
        return 0; // Return 0 if no errors found or parsing failed
    }

    // Determines the color code for health score
    private String getColorCode(double healthScore) {
        if (healthScore >= 80) {
            return "Green";
        } else if (healthScore >= 50) {
            return "Yellow";
        } else {
            return "Red";
        }
    }
}

