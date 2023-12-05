import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRoadTrip {
	
	private Map<String, Map<String, Integer>> bordersData;
	private Map<String, Map<String, Integer>> distanceData;
	private Map<String, String> stateNameData;


    public IRoadTrip (String [] args) {
     // Initialize data structures using the provided file names in args
    	bordersData = readBordersFile(args[0]);
        distanceData = readDistanceFile(args[1]);
        stateNameData = readStateNameFile(args[2]);
    }


    public int getDistance (String country1, String country2) {
    	// find the distance between two countries
        // Return -1 if countries do not exist or do not share a land border
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
    	 //   find the shortest path between two countries
        // Return an empty list if countries do not exist or there is no path
        return null;
    }


    public void acceptUserInput() {
     
        // Validate and get country names, then print the path if it exists
        System.out.println("IRoadTrip - skeleton");
    }
    private Map<String, Map<String, Integer>> readBordersFile(String filename) {
        Map<String, Map<String, Integer>> bordersData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                String country = parts[0].trim();
                Map<String, Integer> borders = new HashMap<>();

                if (parts.length > 1) {
                    String[] borderEntries = parts[1].split(";");
                    for (String entry : borderEntries) {
                        String[] borderParts = entry.trim().split(" ");
                        String borderCountry = borderParts[0];
                        int borderLength = Integer.parseInt(borderParts[1]);
                        borders.put(borderCountry, borderLength);
                    }
                }

                bordersData.put(country, borders);
            }
        } catch (IOException e) {
            e.printStackTrace(); // exception handling
        }

        return bordersData;
    }

    private Map<String, Map<String, Integer>> readDistanceFile(String filename) {
        Map<String, Map<String, Integer>> distanceData = new HashMap<>();

        // read and populate data from capdist.csv
        // Similar to the readBordersFile method

        return distanceData;
    }

    private Map<String, String> readStateNameFile(String filename) {
        Map<String, String> stateNameData = new HashMap<>();

        //read and populate data from state_name.tsv
        // Similar to the readBordersFile method

        return stateNameData;
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}

