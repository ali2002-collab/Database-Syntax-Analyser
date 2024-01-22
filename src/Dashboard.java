import java.util.Scanner;

public class Dashboard {
    // Array of options available in the dashboard
    private final String[] options = {
            "SQL Health Checker",
            "View Alert",
            "View & Apply Fixes",
            "Logout"
    };

    // Displays the dashboard options and handles user selection
    public void displayOptions() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        // Loop until user selects to logout (last option)
        while (choice != options.length) {
            System.out.println("Dashboard Options:");
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }

            System.out.print("Select an option (1-" + options.length + "): ");
            choice = scanner.nextInt();

            // Handle user choice based on the option selected
            switch (choice) {
                case 1:
                    // Handle SQL Health Checker option
                    System.out.println("SQL Health Checker is Selected.");
                    SQLHealthChecker healthChecker = new SQLHealthChecker();
                    healthChecker.displayDatabaseHealth("C:\\Users\\Ali Hassan\\IdeaProjects\\SPF\\src\\Databases");
                    break;
                case 2:
                    // Handle View Alert option
                    System.out.println("View Alert selected.");
                    ViewAlert viewAlert = new ViewAlert("C:\\Users\\Ali Hassan\\IdeaProjects\\SPF\\src\\Databases");
                    viewAlert.displayAlerts();
                    viewAlert.viewAlertDetails();
                    break;
                case 3:
                    // Handle View & Apply Fixes option
                    System.out.println("View & Apply Fixes selected.");
                    ViewAndApplyFixes viewAndApplyFixes = new ViewAndApplyFixes("C:\\Users\\Ali Hassan\\IdeaProjects\\SPF\\src\\Databases");
                    viewAndApplyFixes.displayAlertsAndOptions();
                    break;
                case 4:
                    // Handle Logout option
                    System.out.println("Logging out...");
                    break;
                default:
                    // Handle invalid option selection
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
