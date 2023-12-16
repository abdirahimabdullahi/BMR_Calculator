package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BMRCalculator {
    public static void main(String[] args) {
        Scanner mainScanner = new Scanner(System.in);
        System.out.println("Welcome to the BMR and BMI Calculator!lo");
        System.out.println("Enter 'y' to start the program or any other key to exit.");

        char key = mainScanner.nextLine().trim().toLowerCase().charAt(0);

        if (key == 'y') {
            start(key);
        } else {
            System.out.println("Exiting the program.");
        }

        try{
            toFile();

        }catch(FileNotFoundException e){
            System.out.println("Error processing file: " + e.getMessage());
        }

        mainScanner.close(); // Close the scanner at the end of the program
    }

    /**
     * starts the program with a key, and prompts for
     * the name, age, body type, weight, and height
     * information of an individual displays a summary
     * of the BMR and BMI information  and prompts
     * to quit the program with 'q'
     */
    public static char start(char key) {
        Scanner scanner = new Scanner(System.in);

        while (key != 'q') {
            if (key == 'y') {
                System.out.print("Enter Your first name: ");
                String firstName = getName(scanner);
                System.out.print("Enter Your last Name: ");
                String lastName = getName(scanner);
                String fullName = firstName + " " + lastName;

                System.out.print("Enter your age: ");
                int age = getAge(scanner);

                System.out.print("Are you Female or Male: ");
                char bodyType = getBodyType(scanner);

                System.out.print("Enter your height (e.g., 6'2\"): ");
                int height = getHeight(scanner);

                System.out.print("Enter your weight (in pounds): ");
                double weight = getWeight(scanner);

                double bmi = computeBMI(weight, height);
                double bmr = computeBMR(age, bodyType, weight, height);
                String burnRateStatus = getBurnRate(bmr);

                //System.out.println("Your BMR is " + bmr + " calories/day, categorized as " + burnRateStatus);
                displayResults(fullName, age, bodyType, weight, height, bmi, bmr);

                System.out.println("Recommended healthy weight range: " + computeLowEndHealthyWeight(height) + " lbs to " + computeHighEndHealthyWeight(height) + " lbs");
            }

            System.out.println("\nEnter 'y' to restart or 'q' to quit.");
            key = scanner.nextLine().toLowerCase().charAt(0);
        }
        return key;
    }



    /**
     * computes a person's BMI given their weight and height.
     * @param weight person's weight
     * @param height person's height
     * @return the computed bmiValue of the person
     */
    public static double computeBMI(double weight, int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero.");
        }
        double bmiValue = (703 * weight) / Math.pow(height, 2);
        String bmiCategory = getWeightCategory(bmiValue);

        //System.out.println("BMI Value: " + bmiValue + " (" + bmiCategory + ")");
        return bmiValue;
    }


    /**
     * computes a person's BMR given their weight, height, age, body type (female and male).
     * @param age person's age
     * @param bodyType person's body type
     * @param weight person's age
     * @param height person's height
     * @return computed bmrValue of the person
     */
    public static double computeBMR(int age, char bodyType, double weight, int height) {
        final double WEIGHT_FACTOR = 4.54545;
        final double HEIGHT_FACTOR = 15.875;
        final int AGE_FACTOR = 5;
        final int FEMALE_OFFSET = 161;

        double bmrValue;

        if (bodyType == 'M' || bodyType == 'm') {
            bmrValue = (WEIGHT_FACTOR * weight) + (HEIGHT_FACTOR * height) - (AGE_FACTOR * age) + AGE_FACTOR;
        } else if (bodyType == 'F' || bodyType == 'f') {
            bmrValue = (WEIGHT_FACTOR * weight) + (HEIGHT_FACTOR * height) - (AGE_FACTOR * age) - FEMALE_OFFSET;
        } else {
            throw new IllegalArgumentException("Invalid body type: " + bodyType);
        }

        //System.out.println("BMR Value: " + bmrValue);
        return bmrValue;
    }

    /**
     * returns the high end of the range for the recommended "healthy"
     * weight for an adult for a specific height.
     * @param height person's height
     * @return recommendedWeight of the person
     */
    public static double computeHighEndHealthyWeight(int height) {
        final int BASE_WEIGHT = 100;
        final int INCREMENT_PER_INCH_MALE = 5;
        final int HEALTHY_WEIGHT_MARGIN = 20;

        double recommendedWeight;

        if (height > 60) {
            recommendedWeight = BASE_WEIGHT + ((height - 60) * INCREMENT_PER_INCH_MALE) + HEALTHY_WEIGHT_MARGIN;
        }else {
            recommendedWeight = HEALTHY_WEIGHT_MARGIN;
        }

        System.out.println("High End Healthy Weight: " + recommendedWeight);
        return recommendedWeight;
    }

    /**
     * returns the low end of the range for the recommended "healthy"
     * weight for an adult for a specific height.
     * @param height person's height

     * @return recommendedWeight for person
     */
    public static double computeLowEndHealthyWeight(int height) {
        final int BASE_WEIGHT = 110;
        final int INCREMENT_PER_INCH_MALE = 5;

        double recommendedWeight;

        if (height > 60) {
            recommendedWeight = BASE_WEIGHT + ((height - 60) * INCREMENT_PER_INCH_MALE);
        }else {
            recommendedWeight = BASE_WEIGHT;
        }

        System.out.println("Low End Healthy Weight: " + recommendedWeight);
        return recommendedWeight;
    }


    /**
     * reads in test data and writes out the results to
     * bmr_results.txt file, in the "results" folder.
     * @throws FileNotFoundException error thrown if file is not found
     */
    public static void toFile() throws FileNotFoundException {
        File inputFile = new File("src/main/java/org/example/bmr.txt"); // Ensure this path is correct
        Scanner scanner = new Scanner(inputFile);

        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdir();
        }

        File outputFile = new File(resultsDir, "bmr_results.txt");
        PrintWriter output = new PrintWriter(outputFile);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // Splitting by spaces assuming the format is like: "Jack 24 Male 141.8 5'9\""
            String[] data = line.split("\\s+");

            // Check if data array has the expected number of elements
            if (data.length < 5) {
                System.out.println("Skipping invalid line: " + line);
                continue; // Skip this line and go to the next one
            }

            String name = data[0];
            int age = Integer.parseInt(data[1]);
            char bodyType = data[2].charAt(0);
            double weight = Double.parseDouble(data[3]);
            int height = parseHeight(data[4]);

            double bmi = computeBMI(weight, height);
            double bmr = computeBMR(age, bodyType, weight, height);

            output.println("Name: " + name);
            output.println("Age: " + age);
            output.println("Body Type: " + bodyType);
            output.println("Weight: " + weight);
            output.println("Height: " + height);
            output.println("BMI: " + bmi);
            output.println("BMR: " + bmr);
            output.println("-------------------------------------");
        }

        scanner.close();
        output.close();
    }

    private static int parseHeight(String heightStr) {
        String[] heightParts = heightStr.split("'");
        int feet = Integer.parseInt(heightParts[0]);
        int inches = Integer.parseInt(heightParts[1].replaceAll("[^\\d]", ""));
        return getTotalHeightInInches(feet, inches);
    }




    /**
     * displays the BMR and BMI results for the information provided.
     * @param fullName user's name
     * @param age user's age
     * @param bodyType user's body type
     * @param weight user's weight
     * @param height user's height
     * @param bmi user's bmi
     * @param bmr user's bmr
     */
    public static void displayResults(String fullName, int age, char bodyType, double weight, int height, double bmi, double bmr) {
        String bodyTypeStr = (bodyType == 'M' || bodyType == 'm') ? "Male" : "Female";
        String bmiCategory = getWeightCategory(bmi);

        System.out.println("\n=========== Health Metrics ===========");
        System.out.println("Name: " + fullName);
        System.out.println("Age: " + age + " years");
        System.out.println("Gender: " + bodyTypeStr);
        System.out.println("Weight: " + weight + " lbs");
        System.out.println("Height: " + height + " inches");
        System.out.println("--------------------------------------");
        System.out.println("BMI: " + String.format("%.2f", bmi) + " (" + bmiCategory + ")");
        System.out.println("BMR: " + String.format("%.2f", bmr) + " calories/day");
        System.out.println("======================================\n");
    }



    /**
     * converts the string representation of age (e.g. 21) to integer precision,
     * using Integer.parseInt(value). This method Throws Runtime Exceptions
     * for invalid inputs using a isValidInteger method.
     * @param scanner scanner object
     * @return age person's age
     */
    public static int getAge(Scanner scanner) {
        while(true){
            String ageInput = scanner.nextLine();
            if (isValidInteger(ageInput)) {
                int age = Integer.parseInt(ageInput);
                if (age < 0) {
                    throw new RuntimeException("Invalid input: Age cannot be negative.");
                }else{
                    return age; // entered valid age
                }
            }else{
                System.out.println("Invalid input: Age must be a numeric value. Please try again");
            }
        }
    }

    /**
     * returns a person's resting burn rate status for calories as:
     * LOW, MODERATE or HIGH
     * @param calories calories taken
     * @return resting burn rate status
     */
    public static String getBurnRate(double calories) {
        if (calories < 1200) {
            return "LOW";
        } else if (calories >= 1200 && calories <= 2000) {
            return "MODERATE";
        } else {
            return "HIGH";
        }
    }

    /**
     * converts the string representation of body type (e.g. Female)
     * to letter character 'F' or 'M', using charAt(0) string method.
     * This method Throws Runtime Exception for invalid inputs using a
     * isValidBodyType method.
     * @param scanner scanner object
     * @return person's body type
     */
    public static char getBodyType(Scanner scanner) {
        while (true) {

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("No input provided. Please enter 'M' for Male or 'F' for Female.");
                continue;
            }

            char bodyType = input.toUpperCase().charAt(0);
            if (isValidBodyType(String.valueOf(bodyType))) {
                return bodyType;
            } else {
                System.out.println("Invalid body type entered. Please enter 'M' for Male or 'F' for Female.");
            }
        }
    }

    /**
     *
     converts the string representation for value in feet to
     integer precision, using Integer.parseInt(value).
     * @param value in feet
     * @return integer precision of value
     */
    public static int getFeet(String value) {
        // Remove non-numeric characters except for the delimiter (e.g., ' or space)
        String feetString = value.split("[^0-9]")[0];

        try {
            return Integer.parseInt(feetString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input for feet: " + value);
        }
    }

    /**
     *getHeight	converts the string representation of height (e.g. 5'6")
     * to integer precision. Total height is converted to inches
     * with getTotalHeightInInches method.
     * Throws Runtime Exception for invalid inputs using isValidHeight method.
     * @param scanner scanner object
     * @return the height
     */
    public static int getHeight(Scanner scanner) {
        while(true){
            System.out.print("Enter your height (e.g., 5'6\"): ");
            String heightInput = scanner.nextLine();

            if (isValidHeight(heightInput)) {
                int apostropheIndex = heightInput.indexOf("'");
                String feetPart = heightInput.substring(0, apostropheIndex);
                String inchesPart = heightInput.substring(apostropheIndex + 1).replaceAll("[^\\d]", ""); // Remove non-numeric characters like "

                int feet = Integer.parseInt(feetPart);
                int inches = Integer.parseInt(inchesPart);

                return getTotalHeightInInches(feet, inches);
            }else {
                System.out.println("Invalid height format. Please enter height in the format (eg. 5'4'')");
            }
        }

    }



    /**
     * uses a try-catch control block to validate the height information.
     * @param value height to be validated
     * @return true is valid, false otherwise.
     */
    public static boolean isValidHeight(String value) {
        int apostropheIndex = value.indexOf("'");

        if (apostropheIndex == -1 || apostropheIndex == 0 || apostropheIndex == value.length() - 1) {
            return false;
        }

        try {
            // Extract feet and inches
            String feetPart = value.substring(0, apostropheIndex);
            String inchesPart = value.substring(apostropheIndex + 1).replaceAll("[^\\d]", ""); // Remove non-numeric characters like "

            // Parse feet and inches
            int feet = Integer.parseInt(feetPart);
            int inches = Integer.parseInt(inchesPart);

            return feet >= 0 && inches >= 0 && inches < 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static String getName(Scanner scanner) {
        if(scanner == null){
            throw new IllegalArgumentException("input cannot be empty");
        }
        String name;
        while(true){
            name = scanner.nextLine().trim();
            if(!name.isEmpty()){
                return name;
            }
            System.out.println("Name cannot be empty or whitespace. Please try again.");
        }
    }

    /**
     * uses a try-catch control block to validate
     * values entered that are supposed to read
     * in as integer precision.
     * @param value int val
     * @return true if valid false otherwise
     */
    public static boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }





    /**
     * uses a try-catch control block to validate the body
     * type information as a letter character 'F' for female type, 'M' for male type.
     * @param value body type
     * @return true is valid, false otherwise
     */
    public static boolean isValidBodyType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String normalizedValue = value.trim().toUpperCase();

        if (normalizedValue.equals("M") || normalizedValue.equals("MALE")) {
            return true;
        } else if (normalizedValue.equals("F") || normalizedValue.equals("FEMALE")) {
            return true;
        }

        return false;
    }


    /**
     * converts the string representation of value in inches
     * to integer precision, using Integer.parseInt(value).
     * @param value inches
     * @return value to integer precision
     */
    public static int getInches(String value) {
        try {
            // Split the input string based on non-numeric characters
            String[] parts = value.split("[^0-9]+");
            // The inches part should be the second element in the array
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid format for height.");
            }
            int inches = Integer.parseInt(parts[1]);
            System.out.println("Inches: " + inches);
            return inches;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid input for inches. Please enter a valid numeric value.");
        }
    }

    /**
     * converts the string representation of  the weight into a double
     * precision floating point decimal . Throws Runtime Exception
     * for invalid inputs, using Double.parseDouble(value).
     * @param scanner scanner object
     * @return weight
     */
    public static double getWeight(Scanner scanner) {
        System.out.print("Enter your weight (in pounds): ");

        while (true) {
            String weightInput = scanner.nextLine();

            if (isValidWeight(weightInput)) {
                return Double.parseDouble(weightInput);
            } else {
                System.out.println("Invalid input for weight. Please enter a positive numeric value.");
                System.out.print("Enter your weight (in pounds): ");
            }
        }
    }

    /**
     * 	uses a try-catch control block to
     * 	validate the weight information.
     * @param value weight
     * @return true if valid false otherwise
     */
    public static boolean isValidWeight(String value) {
        try {
            double weight = Double.parseDouble(value);
            if (weight < 0) {
                throw new IllegalArgumentException("Weight cannot be negative.");
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static int getTotalHeightInInches(int feet, int inches) {
        return (feet * 12) + inches;
    }


    /**
     * returns a person's BMI weight category as
     * UNDERWEIGHT, HEALTHY, OVERWEIGHT or OBESE
     * @param bmiValue person's bmi value
     * @return category according to bmiValue
     */
    public static String getWeightCategory(double bmiValue) {
        if (bmiValue < 18.5) {
            return "UNDERWEIGHT";
        } else if (bmiValue >= 18.5 && bmiValue <= 24.9) {
            return "HEALTHY";
        } else if (bmiValue >= 25.0 && bmiValue <= 29.9) {
            return "OVERWEIGHT";
        } else {
            return "OBESE";
        }
    }

    /**
     * gives a description of the program, with added
     * information about the BMR (LOW, MEDIUM, HIGH)
     * Status and BMI  (UNDERWEIGHT, HEALTHY, OVERWEIGHT or OBESE)
     * and prompts to enter the name, age, body type, weight,
     * and height information of an individual.
     */
    public static void intro() {
        System.out.println("Welcome to the BMR and BMI Calculator!");
        System.out.println("This program calculates your Basal Metabolic Rate (BMR) and Body Mass Index (BMI).");
        System.out.println("\nBMR is an estimate of the number of calories your body needs to perform basic functions at rest. It is categorized as follows:");
        System.out.println("LOW: Below 1200 calories");
        System.out.println("MODERATE: 1200 to 2000 calories");
        System.out.println("HIGH: Above 2000 calories");
        System.out.println("\nBMI is a measure of body fat based on height and weight. It helps categorize your weight status as follows:");
        System.out.println("UNDERWEIGHT: BMI less than 18.5");
        System.out.println("HEALTHY: BMI between 18.5 and 24.9");
        System.out.println("OVERWEIGHT: BMI between 25 and 29.9");
        System.out.println("OBESE: BMI 30 or more");
        System.out.println("\nPlease enter your name, age, body type (Male/Female), weight (in pounds), and height (in feet and inches) to begin.");
    }

}
