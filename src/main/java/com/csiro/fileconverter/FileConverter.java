package com.csiro.fileconverter;
import com.google.gson.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class FileConverter {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int userInput;

        do {
            System.out.println("Please select the following option \n 1. Print CSV File (data1.csv) \n 2. Print JSON file (data2.json) \n 3. Exit \n");
            System.out.println("Enter a number:");
            userInput = scanner.nextInt();

            if (userInput == 1) {
                readCSVFile();
            } else if (userInput == 2) {
                readJsonFile();
            } else if (userInput == 3) {
                System.out.println("Exiting program...");
                break;
            } else {
                System.out.println("Invalid input, please enter 1, 2 or 3");
            }

        } while (true);

        scanner.close();

    }

    public static void readCSVFile(){

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String filePath = "./data1.csv";
        String line = "";

        List<HashMap<String, Object>> recordList = new ArrayList<>();
        int count = 0;

        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            while( (line = br.readLine() )!= null){
                if (count != 0){
                    HashMap<String, Object> record = new HashMap<>();
                    String[] values = line.split(",");
                    record.put("id", UUID.randomUUID());
                    record.put("studentNum", values[0]);
                    record.put("resourceType", "Patient");
                    record.put("patientId", values[1]);
                    record.put("testType", values[2]);
                    record.put("testDate", convertDateFormat(values[3]));

                    List<String> difDiagList = new ArrayList<>();
                    int index = 4;

                    //This is to make sure index does not go out of bounds
                    while (index < values.length) {
                        if (!values[index].isEmpty()) {
                            difDiagList.add(values[index]);
                        }
                        index++;
                    }

                    record.put("differentialDiagnosis", difDiagList);
                    record.put("identifier", UUID.randomUUID());
                    recordList.add(record);
                }
                count ++;
            }

            String jsonFormat = gson.toJson(recordList);
            System.out.println(jsonFormat);

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void readJsonFile(){

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        try {
            List<HashMap<String, Object>> recordList = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader("./data2.json"));
            JsonArray array = gson.fromJson(br, JsonArray.class);
            br.close();

            for (JsonElement  obj : array) {
                HashMap<String, Object> record = new HashMap<>();
                JsonObject jsonObject = obj.getAsJsonObject();
                record.put("id", UUID.randomUUID());
                record.put("patientId", jsonObject.get("patientId").getAsString());
                record.put("resourceType", "Patient");
                record.put("differentialDiagnosis", jsonObject.get("differentialDiagnosis"));
                record.put("confirmedDiagnosis", jsonObject.get("confimedDiagnosis").getAsString());
                record.put("testType", jsonObject.get("test_type").getAsString());
                record.put("testDate", jsonObject.get("test_date").getAsString());
                record.put("identifier", UUID.randomUUID().toString());
                recordList.add(record);
            }
            String jsonFormat = gson.toJson(recordList);
            System.out.println(jsonFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Converts date to FHIR standard yyyy-MM-dd
    public static String convertDateFormat(String dateStr) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = oldFormat.parse(dateStr);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
