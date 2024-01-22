// NAME: SYED MUHAMMAD ALI HASSAN
// SID: 2248952
// Team: Java Jugglers
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean exitProgram = false; // Flag to control the program's main loop

        while (!exitProgram) {
            // Displaying the welcome message and options to the user
            System.out.println("Welcome to Redgate's SQL Code Analysis Dashboard");
            System.out.println("1) LOGIN");
            System.out.println("2) Exit the program");

            // Prompting the user to make a choice
            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();

            // Switch statement to handle user choices
            switch (choice) {
                case 1:
                    // Handle the LOGIN option
                    new Login().executeLogin(); // Create a Login object and execute the login process
                    break;
                case 2:
                    // Handle the Exit option
                    System.out.println("Exiting the program...");
                    exitProgram = true; // Set the flag to true to exit the loop
                    break;
                default:
                    // Handle invalid choices entered by the user
                    System.out.println("Invalid choice, please enter 1 or 2.");
                    break;
            }
        }

        // Close the scanner resource before exiting the program
        scanner.close();
    }
}
