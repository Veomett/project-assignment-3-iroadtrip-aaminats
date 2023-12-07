import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;



public class IRoadTrip {
	
	private Map<String, Map<String, Integer>> bordersData;
	private Map<String, Map<String, Integer>> distanceData;
	private Map<String, String> stateNameData;


    public IRoadTrip (String [] args) {
     // Initialize data structures using the provided file names in args
    	stateNameData = readStateNameFile("state_name.tsv");
        bordersData = readBordersFile("borders.txt");
        distanceData = readDistanceFile("capdist.csv");
    }


    public int getDistance (String country1, String country2) {
    	// find the distance between two countries
        // Return -1 if countries do not exist or do not share a land border
    	if (!(distanceData.containsKey(country1) || bordersData.containsKey(country1)) ||
                !(distanceData.containsKey(country2) || bordersData.containsKey(country2))) {
                System.out.println("Either Country1: " + country1 + " or Country2: " + country2 + " does not exist in either distanceData or bordersData.");
                return -1;
    	}
        // Check if distance data exists for the two countries
        if (!distanceData.get(country1).containsKey(country2) || !distanceData.get(country2).containsKey(country1)) {
            System.out.println("No distance data available for Country1: " + country1 + " and Country2: " + country2);
            return -1;
        }

        // Return the distance between the two countries
        return distanceData.get(country1).get(country2);
    	
    }
    
    private Map<String, Map<String, Integer>> readDistanceFile(String filename) {
        Map<String, Map<String, Integer>> distanceData = new HashMap<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line (header)
                }
                String[] parts = line.split(",");
                String countryA = parts[1].trim();
                String countryB = parts[3].trim();
                // get countryA and countryB from stateNameData if they exist, otherwise use the original values
                countryA = stateNameData != null && stateNameData.containsKey(countryA) ? stateNameData.get(countryA) : countryA;
                countryB = stateNameData != null && stateNameData.containsKey(countryB) ? stateNameData.get(countryB) : countryB;

                // System.out.println(countryA + " " + countryB);
                String distanceString = parts[4].replaceAll(",", "").trim(); // Remove commas and trim spaces
                int distance = Integer.parseInt(distanceString);

                distanceData.putIfAbsent(countryA, new HashMap<>());
                distanceData.get(countryA).put(countryB, distance);
    
                distanceData.putIfAbsent(countryB, new HashMap<>());
                distanceData.get(countryB).put(countryA, distance);
            }
        } catch (IOException e) {
            e.printStackTrace(); // exception handling
        }

        // print out the distanceData
        // for (Map.Entry<String, Map<String, Integer>> entry : distanceData.entrySet()) {
        //     System.out.println("Country: " + entry.getKey());
        //     System.out.println("Neighbors: ");
        //     for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
        //         System.out.println(" - " + neighbor.getKey() + ": " + neighbor.getValue());
        //     }
        // }
    
        return distanceData;
    }
    
 // print out the distanceData
    public void printDistanceData() {
        for (Map.Entry<String, Map<String, Integer>> entry : distanceData.entrySet()) {
            System.out.println("Country: " + entry.getKey());
            System.out.println("Neighbors: ");
            for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
                System.out.println(" - " + neighbor.getKey() + ": " + neighbor.getValue());
            }
        }
    }
    
    private List<String> formatPath(List<String> path) {
        List<String> formattedPath = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            String currentCountry = path.get(i);
            String nextCountry = path.get(i + 1);
            int distance = distanceData.get(currentCountry).get(nextCountry);
            formattedPath.add(currentCountry + " --> " + nextCountry + " (" + distance + " km.)");
        }
        return formattedPath;
    }


    public List<String> findPath(String country1, String country2) {
        if (!distanceData.containsKey(country1) || !distanceData.containsKey(country2)) {
            return new ArrayList<>(); // Return an empty list if countries do not exist
        }
        
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        
        queue.offer(country1);
        visited.add(country1);
        
        while (!queue.isEmpty()) {
            String currentCountry = queue.poll();
            
            if (currentCountry.equals(country2)) {
                // Reconstruct the path from country2 to country1
                List<String> path = new ArrayList<>();
                String country = country2;
                while (country != null) {
                    path.add(country);
                    country = parent.get(country);
                }
                Collections.reverse(path);
                return formatPath(path);
            }
            
            Map<String, Integer> neighbors = distanceData.get(currentCountry);
            for (String neighbor : neighbors.keySet()) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, currentCountry);
                }
            }
        }

        return new ArrayList<>(); // Return an empty list if there is no path
    }


    public void acceptUserInput() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("Enter the name of the first country (type EXIT to quit): ");
            String country1 = scanner.nextLine();
            
            if (country1.equalsIgnoreCase("EXIT")) {
                break;
            }
            
            System.out.print("Enter the name of the second country (type EXIT to quit): ");
            String country2 = scanner.nextLine();
            
            if (country2.equalsIgnoreCase("EXIT")) {
                break;
            }
            
            int shortestPath = getDistance(country1, country2);
            if (shortestPath == -1) {
                System.out.println("Invalid country name. Please enter a valid country name.");
            } else {
                System.out.println("The shortest path between " + country1 + " to " + country2 + " : ");
                System.out.println(findPath(country1, country2));
            }
        }
        
        scanner.close();
    }
    
    private Map<String, Map<String, Integer>> readBordersFile(String filename) {
        Map<String, Map<String, Integer>> bordersData = new HashMap<>();
        Map<String, String> countryNameMapping = new HashMap<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                String originalCountryName = parts[0].trim();
                String standardizedCountryName = getStandardizedCountryName(originalCountryName);
                Map<String, Integer> borders = bordersData.getOrDefault(standardizedCountryName, new HashMap<>());
    
                if (parts.length > 1) {
                    String[] borderEntries = parts[1].split(";");
                    int totalBorderLength = 0;
    
                    for (String entry : borderEntries) {
                        entry = entry.replaceAll(",", ""); // Remove commas from [1]
                        entry = entry.replaceAll("\\.", ""); // Remove decimal points from [1]
                        
                        String[] borderParts = entry.trim().split("\\s+(?=\\d)");
                        if (borderParts.length == 2) {
                            String borderCountry = borderParts[0];
                            
                            int borderLength = parseIntegerWithCommas(borderParts[1]);
                            borders.put(borderCountry, borderLength);
                            totalBorderLength += borderLength;
                        } else if (borderParts.length == 1) {
                            borders.put(borderParts[0], 0);
                        } else {
                            System.err.println("Invalid format in line: " + line);
                        }
                    }
                    borders.put("Total", totalBorderLength);
                }
    
                bordersData.put(standardizedCountryName, borders);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    
        return bordersData;
    }
    
    private String getStandardizedCountryName(String originalName) {
        // Implement logic to standardize country names (e.g., handle alias names)
        // You can use a mapping or a more complex logic based on your requirements
        // For simplicity, let's assume original names are standardized
        return originalName;
    }
    
    private int parseIntegerWithCommas(String input) {
        // Remove commas only if they are surrounded by digits and remove 'km'
        input = input.replaceAll("(?<=\\d),(?=\\d)", "").replaceAll("km", "").trim();
        return Integer.parseInt(input);
    }
    
    
    

    private Map<String, String> readStateNameFile(String filename) {
        Map<String, String> stateNameData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line (header)
                }
                String[] parts = line.split("\t"); // Split using tab space
                String countryId = parts[1];
                String countryName = parts[2];
                String endDate = parts[4];

                // Check if the country is still existing based on the end date
                if (endDate.equals("2020-12-31")) {
                    stateNameData.put(countryId, countryName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // exception handling
        }

        return stateNameData;
    }
    
    public void printBordersData() {
        for (Map.Entry<String, Map<String, Integer>> entry : bordersData.entrySet()) {
            // System.out.println("Country: " + entry.getKey());
            // System.out.println("Neighbors: ");
            for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
                System.out.println(" - " + neighbor.getKey() + ": " + neighbor.getValue());
            }
        }
    }


    public static void main(String[] args) {
        IRoadTrip roadTrip = new IRoadTrip(args);

        roadTrip.acceptUserInput();
    }

}

