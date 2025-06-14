package org.aryanoor.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.aryanoor.services.IAM;
import org.aryanoor.services.OpenRouterChat;

/**
 * The CLI class provides a command-line interface for user authentication and chatbot interaction.
 * It loads API configurations, manages user login, and facilitates chatbot communication.
 */
class CLI {

    private String apiUrl; // API URL for chatbot
    private String apiKey; // API key for authentication
    private static final String CONFIG_FILE = "config.properties"; // Configuration file path

    /**
     * Constructor for CLI.
     * Loads the API configuration from the properties file.
     *
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public CLI() throws IOException {
        loadConfig();
    }

    /**
     * Loads API configuration (apiUrl and apiKey) from a properties file.
     *
     * @throws IOException If an error occurs while reading the file.
     */
    private void loadConfig() throws IOException {
        Properties properties = new Properties();
        if (Files.exists(Paths.get(CONFIG_FILE))) {
            List<String> lines = Files.readAllLines(Paths.get(CONFIG_FILE));
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    if (parts[0].trim().equalsIgnoreCase("apiUrl")) {
                        apiUrl = parts[1].trim();
                    } else if (parts[0].trim().equalsIgnoreCase("apiKey")) {
                        apiKey = parts[1].trim();
                    }
                }
            }
        } else {
            System.out.println("Configuration file not found. Please create 'config.properties' with apiUrl and apiKey.");
            System.exit(1);
        }
    }

    /**
     * Runs the CLI for user authentication and chatbot interaction.
     *
     * @throws IOException If an error occurs during input/output operations.
     */
    public void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // If no user is registered, prompt for registration
        if (!Files.exists(Paths.get("user.data"))) {
            System.out.print("No registered user found. Please register.\nEnter username: ");
            String username = reader.readLine();
            System.out.print("Enter password: ");
            String password = reader.readLine();
            IAM newUser = new IAM(username, password);
            newUser.signUp();
        }

        System.out.println(">Hello Again! Let's login to your account");

        // Login process
        while (true) {
            System.out.print("Enter username: ");
            String username = reader.readLine();
            System.out.print("Enter password: ");
            String password = reader.readLine();
            IAM user = new IAM(username, password);
            if (user.login(username, password)) {
                break;
            }
            System.out.println("Try again.");
        }

        OpenRouterChat chatBot = new OpenRouterChat(apiUrl, apiKey);
        System.out.println("Welcome to the chatbot! Type 'exit' to quit.");

        // Chat loop
        while (true) {
            System.out.print("> Enter your question: ");
            String question = reader.readLine();

            if (question.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            System.out.println("Thinking...");
            String response = chatBot.sendChatRequest(question);
            System.out.println(response);
            System.out.println(">---------------------------------<");
        }
    }

    /**
     * Processes the user input to ensure proper formatting.
     *
     * Assignment Task:
     * - Capitalizes the first letter of each sentence.
     * - Ensures the sentence ends with a question mark.
     *
     * @param prompt The original user input.
     * @return The formatted user input.
     */
    private String promptPreprocess(String prompt) {
        if (prompt == null || prompt.trim().isEmpty())
            return "";

        // Trim and ensure the first letter is capitalized
        prompt = prompt.trim();
        String formatted = prompt.substring(0, 1).toUpperCase() + prompt.substring(1);
        // Ensure the sentence ends with a question mark
        if (!formatted.endsWith("?")) {
            formatted += "?";
        }
        return formatted;
    }


    /**
     * Counts the number of words in a given string.
     *
     * @param input The original string.
     * @return The number of words in the string.
     */
    private int countWords(String input) {
        if (input == null || input.trim().isEmpty())
            return 0;
        return input.trim().split("\\s+").length;
    }
}
