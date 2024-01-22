import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewAndApplyFixes {
    private List<Alert> alerts;  // List to hold Alert objects
    private Scanner scanner;  // Scanner for reading user input

    // Constructor to initialize the ViewAndApplyFixes object


    public ViewAndApplyFixes(String directoryPath) {
        this.alerts = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        processDirectory(directoryPath);  // Process the directory containing SQL files
    }

    // Processes each SQL file in the given directory to fetch linting alerts
    private void processDirectory(String directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.sql")) {
            for (Path entry : stream) {
                alerts.addAll(fetchAlertsFromSQLFluff(entry.toString()));  // Add alerts from each file
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle potential I/O errors
        }
    }

    // Fetches linting alerts from a single SQL file using SQLFluff
    private List<Alert> fetchAlertsFromSQLFluff(String filePath) {
        List<Alert> fetchedAlerts = new ArrayList<>();
        String output = runSQLFluffCommand(filePath);

        // Parse the JSON output from SQLFluff
        JsonParser parser = new JsonParser();
        JsonArray filesArray = parser.parse(output).getAsJsonArray();

        // Extract alert details and add to fetchedAlerts list
        int srNumber = alerts.size() + 1;  // Continue numbering from existing alerts
        for (JsonElement fileElement : filesArray) {
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray violationsArray = fileObject.getAsJsonArray("violations");

            for (JsonElement violationElement : violationsArray) {
                JsonObject violationObject = violationElement.getAsJsonObject();
                Alert alert = new Alert(
                        srNumber++,
                        violationObject.get("code").getAsString(),
                        violationObject.get("description").getAsString(),
                        filePath,
                        violationObject.get("line_no").getAsInt(),
                        violationObject.get("line_pos").getAsInt()
                );
                fetchedAlerts.add(alert);
            }
        }

        return fetchedAlerts;
    }

    // Runs the SQLFluff lint command on a given file and returns its output
    private String runSQLFluffCommand(String filePath) {
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "sqlfluff", "lint", "--dialect", "ansi", "--format", "json", filePath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();  // Handle exceptions during command execution
        }

        return output.toString();
    }

    // Displays all fetched alerts and prompts the user to select one for more details
    public void displayAlertsAndOptions() {
        System.out.println("Sr No. | Code | Source | Line No. | Line Pos.");
        System.out.println("------------------------------------------------");
        for (Alert alert : alerts) {
            System.out.printf("%6d | %4s | %-6s | %8d | %9d%n",
                    alert.getSrNumber(),
                    alert.getCode(),
                    alert.getFilePath(), // Displaying the file path
                    alert.getLineNo(),
                    alert.getLinePos());
        }
        selectAlert();  // Prompt user to select an alert
    }

    // Allows the user to select an alert by its serial number
    private void selectAlert() {
        System.out.print("Enter the Sr No. of the alert to view details and Fix Options: ");
        int srNumber = scanner.nextInt();

        Alert selectedAlert = alerts.stream()
                .filter(a -> a.getSrNumber() == srNumber)
                .findFirst()
                .orElse(null);

        // Display alert details and offer fix options if an alert is found
        if (selectedAlert != null) {
            displayAlertDetails(selectedAlert);
            offerFixOptions(selectedAlert);
        } else {
            System.out.println("Alert not found for Sr No. " + srNumber);
        }
    }

    // Displays detailed information about a selected alert
    private void displayAlertDetails(Alert alert) {
        System.out.println("Detailed Alert Information:");
        System.out.println("----------------------------");
        System.out.println("Serial Number: " + alert.getSrNumber());
        System.out.println("Code: " + alert.getCode());
        System.out.println("Description: " + alert.getDescription());
        System.out.println("File Path: " + alert.getFilePath());
        System.out.println("Line Number: " + alert.getLineNo());
        System.out.println("Line Position: " + alert.getLinePos());
        System.out.println("----------------------------");
    }

    // Offers the user options to either view automated fixes or manually edit the code
    private void offerFixOptions(Alert alert) {
        System.out.println("Choose an option:");
        System.out.println("1. View Automated Suggested Fixes");
        System.out.println("2. Manual Edit the Code");
        System.out.printf("Enter Option: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            displayAndApplyAutoFixes(alert);
        } else if (choice == 2) {
            openFileInNotepad(alert.getFilePath());
        }
    }

    // Displays suggested fixes and prompts the user to apply them
    private void displayAndApplyAutoFixes(Alert alert) {
        String fixes = getAutoFixes(alert.getFilePath());
        System.out.println("Suggested Fixes:");
        System.out.println(fixes);
        System.out.printf("Apply these fixes? (yes/no): ");
        String apply = scanner.next();

        if (apply.equalsIgnoreCase("yes")) {
            applyFixes(alert.getFilePath());
        }
    }

    // Executes the SQLFluff lint command to fetch suggested fixes
    private String getAutoFixes(String filePath) {
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "sqlfluff", "lint", "--dialect", "ansi", "--format", "json", filePath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();  // Handle exceptions during command execution
        }

        return output.toString();
    }

    // Applies the suggested fixes to the SQL file
    private void applyFixes(String filePath) {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "sqlfluff", "fix", "--force", "--dialect", "ansi", filePath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            String outputStr = output.toString();
            if (outputStr.contains("no fixable linting violations found")) {
                System.out.println("No fixable linting violations found in " + filePath);
                System.out.println(outputStr);
            } else if (process.exitValue() == 0) {
                System.out.println("Fixes successfully applied to " + filePath);
                System.out.println(outputStr);
            } else {
                System.out.println("An error occurred while applying fixes.");
                System.out.println(outputStr);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();  // Handle exceptions during command execution
        }
    }

    // Opens the specified file in Notepad for manual editing
    private void openFileInNotepad(String filePath) {
        try {
            Runtime.getRuntime().exec("notepad.exe " + filePath);
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions during file opening
        }
    }
}
