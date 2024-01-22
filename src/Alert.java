public class Alert {
    // Fields to store alert details
    private int srNumber;     // Serial number of the alert
    private String code;      // Code of the alert (e.g., lint error code)
    private String filePath;  // File path where the alert was found
    private int lineNo;       // Line number in the file where the alert was found
    private int linePos;      // Line position (column number) where the alert was found
    private String description; // Description of the alert

    /**
     * Constructor for Alert class.
     * Initializes an Alert object with given parameters.
     *
     * @param srNumber Serial number of the alert.
     * @param code Code of the alert.
     * @param description Description of the alert.
     * @param filePath File path where the alert was found.
     * @param lineNo Line number in the file where the alert was found.
     * @param linePos Line position (column number) where the alert was found.
     */
    public Alert(int srNumber, String code, String description, String filePath, int lineNo, int linePos) {
        this.srNumber = srNumber;
        this.code = code;
        this.description = description;
        this.filePath = filePath;
        this.lineNo = lineNo;
        this.linePos = linePos;
    }

    // Getter methods for accessing the fields of the Alert class

    public int getSrNumber() {
        return srNumber;
    }

    public String getCode() {
        return code;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getLinePos() {
        return linePos;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Provides a string representation of the Alert object.
     * This method is useful for logging and displaying alert information.
     *
     * @return A string representation of the Alert object.
     */
    @Override
    public String toString() {
        return "Alert{" +
                "srNumber=" + srNumber +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", filePath='" + filePath + '\'' +
                ", lineNo=" + lineNo +
                ", linePos=" + linePos +
                '}';
    }
}
