import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewAlert {
    private List<Alert> alerts; // List to hold Alert objects parsed from SQLFluff output

    // Constructor to initialize the ViewAlert object with a directory path
    public ViewAlert(String directoryPath) {
        this.alerts = new ArrayList<>();
        processDirectory(directoryPath); // Process each SQL file in the directory
    }

    // Processes all SQL files in the given directory and accumulates alerts
    private void processDirectory(String directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.sql")) {
            for (Path entry : stream) {
                // Fetch and add alerts from each file to the alerts list
                alerts.addAll(fetchAlertsFromSQLFluff(entry.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle I/O exceptions
        }
    }

    // Fetches linting alerts from a SQL file using SQLFluff
    private List<Alert> fetchAlertsFromSQLFluff(String filePath) {
        List<Alert> fetchedAlerts = new ArrayList<>();
        String output = runSQLFluffCommand(filePath); // Run SQLFluff command and get output

        // Parse the output from SQLFluff and extract alert details
        JsonParser parser = new JsonParser();
        JsonArray filesArray = parser.parse(output).getAsJsonArray();

        int srNumber = alerts.size() + 1; // Assign a unique serial number to each alert
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
                fetchedAlerts.add(alert); // Add new Alert object to the list
            }
        }

        return fetchedAlerts;
    }

    // Executes the SQLFluff command and returns its output
    private String runSQLFluffCommand(String filePath) {
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "sqlfluff", "lint", "--dialect", "ansi", "--format", "json", filePath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // Read and append output from the process
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor(); // Wait for the process to complete
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Handle exceptions during process execution
        }

        return output.toString();
    }

    // Displays all fetched alerts in a formatted table
    public void displayAlerts() {
        System.out.println("Sr No. | Code | Source | Line No. | Line Pos.");
        System.out.println("------------------------------------------------");
        for (Alert alert : alerts) {
            // Format and display each alert's details
            System.out.printf("%6d | %4s | %-6s | %8d | %9d%n",
                    alert.getSrNumber(),
                    alert.getCode(),
                    alert.getFilePath(),
                    alert.getLineNo(),
                    alert.getLinePos());
        }
    }

    // Prompts the user to select an alert and displays its detailed information
    public void viewAlertDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Sr No. of the alert to view details: ");
        int srNumber = scanner.nextInt();

        // Find and display details of the selected alert
        Alert selectedAlert = alerts.stream()
                .filter(a -> a.getSrNumber() == srNumber)
                .findFirst()
                .orElse(null);

        if (selectedAlert != null) {
            // Display detailed information for the selected alert
            System.out.println("Detailed Alert Information:");
            System.out.println("----------------------------");
            System.out.println("Serial Number: " + selectedAlert.getSrNumber());
            System.out.println("Code: " + selectedAlert.getCode());
            System.out.println("Description: " + selectedAlert.getDescription());
            System.out.println("File Path: " + selectedAlert.getFilePath());
            System.out.println("Line Number: " + selectedAlert.getLineNo());
            System.out.println("Line Position: " + selectedAlert.getLinePos());
            System.out.println("----------------------------");
        } else {
            System.out.println("Alert not found for Sr No. " + srNumber); // Alert not found
        }
    }
}



