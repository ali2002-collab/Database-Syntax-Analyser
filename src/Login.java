import java.io.*;
import java.util.Scanner;

public class Login {
    private String username; // Stores the user's email
    private String password; // Stores the user's password

    /**
     * Reads and validates user credentials.
     * Ensures that the provided email is a Redgate email.
     */
    private void readCredentials() {
        Scanner scanner = new Scanner(System.in);

        // Validate the email format
        boolean validEmail = false;
        while (!validEmail) {
            System.out.print("Enter email (must be a Redgate email): ");
            this.username = scanner.nextLine();

            // Check if the email ends with the Redgate domain
            if (this.username.endsWith("@red-gate.com")) {
                validEmail = true;
            } else {
                System.out.println("Invalid email format. Please use a Redgate email.");
            }
        }

        // Prompt for password
        System.out.print("Enter password: ");
        this.password = scanner.nextLine();
    }

    /**
     * Authenticates the user against the stored credentials.
     *
     * @return true if authentication is successful, false otherwise.
     */
    public boolean authenticate() {
        try {
            // Open the file containing user credentials
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Ali Hassan\\IdeaProjects\\SPF\\src\\userdb.txt"));
            String line;
            // Read each line (user credential) in the file
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                // Check if the credentials match
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    reader.close();
                    return true; // Authentication successful
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions related to file operations
        }
        return false; // Authentication failed
    }

    /**
     * Executes the login process by reading credentials, authenticating them,
     * and then directing to the dashboard upon successful login.
     */
    public void executeLogin() {
        readCredentials(); // Read user credentials
        boolean isAuthenticated = authenticate(); // Authenticate the user

        // Check the result of authentication
        if (isAuthenticated) {
            // Login successful
            System.out.println("Login Successful!");
            System.out.println("-------------------");
            System.out.println("Welcome " + username);
            System.out.println("-------------------");

            // Proceed to the dashboard
            Dashboard dashboard = new Dashboard();
            dashboard.displayOptions();
        } else {
            // Login failed
            System.out.println("Login Failed!, Credentials not Verified :(");
        }
    }
}
