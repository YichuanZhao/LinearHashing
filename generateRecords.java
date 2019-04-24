import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


class generateRecords{

	public static void main(String[] args) throws Exception{
        // Generate certain amount of records
        // Each record contains an ID, name (first_name last_name), major, and zip. 
        // Each record is in the form [ID, Name, major, zip] 
        // Example: [279,Jack Scott,ME,47545]

        int num = 10000; // The amount of records to be generated.

		// System.out.println(numUniqueEmails(test));
        // stringToByteArray("[700,Linda Walker,CE,76310]");
        ArrayList<Integer> IDs = generateUniqueIDs(num); // Randomly generate certain amount of IDs. IDs are integers. The IDs are stored in an array list. 
        ArrayList<String> first_names = generateNames(num); // Randomly generate certain amount of strins. Each string generated here represents a first name. The strings are stored in an array list.
        ArrayList<String> last_names = generateNames(num); // Randomly generate certain amount of strings. Each string generated here represents a last name. The strings are stored in an array list.
        ArrayList<Integer> zips = generateZips(num); // Randomly generate cetains amount of zips. Each zip is a 6 digits integer. The zips are stored in an array list.
        ArrayList<String> majors = generateMajors(num); // Randomly select certain amount of majors from the list ["ME", "EE", "BS", "Bio", "ComS", "Phy", "Math", "CE", "Chem"]. The majors are stored in an array list. 

        PrintWriter writer = new PrintWriter("Records.txt", "UTF-8"); // Open the local file Records.txt, where the records will be written into. 


        for(int i=0; i<num; i++){
            String temp = "[";
            temp = temp + Integer.toString(IDs.get(i));
            temp = temp + ",";

            temp = temp + first_names.get(i);
            temp = temp + " ";

            temp = temp + last_names.get(i);
            temp = temp + ",";

            temp = temp + majors.get(i);
            temp = temp + ",";

            temp = temp + Integer.toString(zips.get(i));
            temp = temp + "]";

            writer.println(temp);

        }

        writer.close(); //Close the file.

        
	}


    public static ArrayList<Integer> generateUniqueIDs(int num){
        // Randomly generate certain amount of IDs. IDs are integers. The IDs are stored in an array list. 

        ArrayList<Integer> list = new ArrayList<Integer>();

        for(int i=0; i<num; i++){
            list.add(i);
        }

        Collections.shuffle(list);

        return list;

    }

    public static ArrayList<Integer> generateZips(int num){
        // Randomly generate cetains amount of zips. Each zip is a 6 digits integer. The zips are stored in an array list.

        ArrayList<Integer> list = new ArrayList<Integer>();

        Random rand = new Random();

        for(int i=0; i<num; i++){
            list.add(rand.nextInt(89999) + 10000);
        }

        return list;
    }

    public static ArrayList<String> generateMajors(int num){

        //Randomly select certain amount of majors from the list ["ME", "EE", "BS", "Bio", "ComS", "Phy", "Math", "CE", "Chem"]. The majors are stored in an array list. 

        String majors[] = {"ME", "EE", "BS", "Bio", "ComS", "Phy", "Math", "CE", "Chem", "test"};

        Random rand = new Random();

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0; i < num; i++){
            list.add(majors[rand.nextInt(9)]);
        }

        return list;

    }

    public static ArrayList<String> generateNames(int num){
        // Randomly generate certain amount of strings. The strings are stored in an array list.

        String alp = "abcdefghijklmnopqrstuvwxyz";

        ArrayList<String> list = new ArrayList<String>();

        Random rand1 = new Random();
        Random rand2 = new Random();
        for(int i=0; i<num; i++){
            String temp = "";
            int v = rand1.nextInt(3) + 5;

            for(int j=0; j<v; j++){
                int t = rand2.nextInt(26); 
                temp = temp + alp.charAt(t);
            }    

            list.add(temp);
        }

        return list;

    }

    // public static byte[] intToByteArray(int value) {
    //     //Convert integer into a byte array

    //     byte[] array = new byte[4];
    //     for (int i = 3; i >= 0; i--) {
    //         array[3 - i] = (byte) (value >> i * 8);
    //     }
    //     return array;
    // }

    // public static int byteArrayToInt(byte[] byteArray) {
    //     // Convert a byte arrya in to an integer.

    //     int value = 0;
    //     for (int i = 0; i < byteArray.length; i++) {

    //         value += ((int) byteArray[i] & 0xff) << (8 * (3 - i));
    //     }
    //     return value;
    // }

    // public static byte[] stringToByteArray(String str){
    //     // Convert a string into a byte array.

    //     int index = str.indexOf(',');

    //     String ID = str.substring(1, index);

    //     int l = str.length();

    //     String dataString = str.substring(index + 1, l-1);


    //     // System.out.println(ID);

    //     // System.out.println(dataString);

    //     byte[] ans = new byte[100];

    //     char[] buffer = dataString.toCharArray();

    //     int res = Integer.valueOf(ID);
    //     // System.out.print(res);
    //     byte IDValue[] = intToByteArray(res);

    //     for(int i=0; i<4; i++){
    //         ans[i] = IDValue[i];
    //     } 

    //     for(int i=4; i<dataString.length(); i++){
    //         ans[i] = (byte)buffer[i];
    //     }

    //     int test = byteArrayToInt(IDValue);

    //     // System.out.println(test);

    //     // for(int i=0; i<4; i++){
    //     //     System.out.println(ans[i]);
    //     // }   
    //     return ans;
    // }

    // public static ArrayList<byte[]> getRecords(){

    //     //Get records from a local text file.

    //     BufferedReader reader;

    //     ArrayList<byte[]> data = new ArrayList<byte[]>();

    //     try{
    //         reader = new BufferedReader(new FileReader("Emp.txt"));

    //         String line = reader.readLine();

    //         while(line != null){
    //             byte temp[] = stringToByteArray(line);
    //             data.add(temp);
    //             // System.out.println(line);
    //             line = reader.readLine();
    //         }

    //         reader.close();
    //     }catch(IOException e){
    //         e.printStackTrace();
    //     }

    //     return data;

    // }

}