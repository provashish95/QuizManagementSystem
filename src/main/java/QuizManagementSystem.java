import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class QuizManagementSystem {
    static Scanner scanner = new Scanner(System.in);
    static String userFile = "./src/main/resources/users.json";
    static String quizFile = "./src/main/resources/quiz.json";


    //Main class for run ...
    public static void main(String[] args) throws Exception {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        JSONObject user = authenticate(username, password);
        if (user == null) {
            System.out.println("Invalid credentials!");
            return;
        }

        String role = (String) user.get("role");
        if (role.equals("admin")) {
            System.out.println("Welcome admin! Please create new questions in the question bank.");
            handleAdmin();
        } else if (role.equals("student")) {
            System.out.println("Welcome " + username + " to the quiz! We will throw you 10 questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' to start.");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("s")) {
                handleStudent();
            }
        }
    }

    //Authentication for Admin & Student...
    public static JSONObject authenticate(String username, String password) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray users = (JSONArray) parser.parse(new FileReader(userFile));
        for (Object obj : users) {
            JSONObject user = (JSONObject) obj;
            if (user.get("username").equals(username) && user.get("password").equals(password)) {
                return user;
            }
        }
        return null;
    }

//Handle Admin ...
    public static void handleAdmin() throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray questions;
        try {
            questions = (JSONArray) parser.parse(new FileReader(quizFile));
        } catch (FileNotFoundException e) {
            questions = new JSONArray();
        }

        while (true) {
            System.out.print("Do you want to add more questions? (press 's' to start and 'q' to quit): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("q"))
                break;

            JSONObject q = new JSONObject();
            System.out.print("Input your question: ");
            q.put("question", scanner.nextLine());

            for (int i = 1; i <= 4; i++) {
                System.out.print("Input option " + i + ": ");
                q.put("option " + i, scanner.nextLine());
            }

            System.out.print("What is the answer key (1-4)?: ");
            q.put("answerkey", Integer.parseInt(scanner.nextLine()));
            questions.add(q);
            System.out.println("Saved successfully!");
        }

        try (FileWriter file = new FileWriter(quizFile)) {
            file.write(questions.toJSONString());
            file.flush();
        }
    }


    //Handle Student...

    public static void handleStudent() throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray questions = (JSONArray) parser.parse(new FileReader(quizFile));

        int score = 0;
        Random rand = new Random();
        for (int i = 1; i <= 10; i++) {
            int index = rand.nextInt(questions.size());
            JSONObject q = (JSONObject) questions.get(index);

            System.out.println("[Question " + i + "] " + q.get("question"));
            for (int j = 1; j <= 4; j++) {
                System.out.println(j + ". " + q.get("option " + j));
            }

            System.out.print("Your answer (1-4): ");
            int ans;
            try {
                ans = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                ans = 0;
            }

            long correct = (long) q.get("answerkey");
            if (ans == correct) score++;
        }

        System.out.println("\nYou have got " + score + " out of 10");
        if (score >= 8) System.out.println("Excellent!");
        else if (score >= 5) System.out.println("Good.");
        else if (score >= 2) System.out.println("Very poor!");
        else System.out.println("Very sorry, you are failed.");

        System.out.print("Would you like to start again? (s for start, q for quit): ");
        String again = scanner.nextLine();
        if (again.equalsIgnoreCase("s")) handleStudent();
    }

}
