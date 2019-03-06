import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


class test{

	public static void main(String[] args) throws Exception{

        String name = "";

        int num = 10000;
		// System.out.println(numUniqueEmails(test));
        // stringToByteArray("[700,Linda Walker,CE,76310]");
        ArrayList<Integer> IDs = generateUniqueIDs(num);
        ArrayList<String> first_names = generateNames(num);
        ArrayList<String> last_names = generateNames(num);
        ArrayList<Integer> zips = generateZip(num);
        ArrayList<String> majors = generateMajor(num);

        PrintWriter writer = new PrintWriter("Records.txt", "UTF-8");


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

        writer.close();

        
	}

    public static int numUniqueEmails(String[] emails) {
        
        int l = emails.length;
        
        HashSet<String> temp = new HashSet<String>();
        
        for(int i=0; i<l; i++){
            String[] parts = emails[i].split("@");
            String part1 = parts[0];
            String part2 = parts[1];
            
            // System.out.println(part1);

            int index = part1.indexOf('+');

            // System.out.println(part1.substring(0,index));
            

            String sstr = "";
            if(index > 0){
                sstr = part1.substring(0, index);
            }else{
                sstr = part1;
            }
            sstr = sstr.replace(".","");
            
            System.out.println(sstr);
            temp.add(sstr + part2);
            
            
        }
        
        return temp.size();
    }

    public static byte[] intToByteArray(int value) {
        byte[] array = new byte[4];
        for (int i = 3; i >= 0; i--) {
            array[3 - i] = (byte) (value >> i * 8);
        }
        return array;
    }

    public static int byteArrayToInt(byte[] byteArray) {
        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {

            value += ((int) byteArray[i] & 0xff) << (8 * (3 - i));
        }
        return value;
    }

    public static byte[] stringToByteArray(String str){

        int index = str.indexOf(',');

        String ID = str.substring(1, index);

        int l = str.length();

        String dataString = str.substring(index + 1, l-1);


        // System.out.println(ID);

        // System.out.println(dataString);

        byte[] ans = new byte[100];

        char[] buffer = dataString.toCharArray();

        int res = Integer.valueOf(ID);
        // System.out.print(res);
        byte IDValue[] = intToByteArray(res);

        for(int i=0; i<4; i++){
            ans[i] = IDValue[i];
        } 

        for(int i=4; i<dataString.length(); i++){
            ans[i] = (byte)buffer[i];
        }

        int test = byteArrayToInt(IDValue);

        // System.out.println(test);

        // for(int i=0; i<4; i++){
        //     System.out.println(ans[i]);
        // }   
        return ans;
    }

    public static ArrayList<byte[]> getData(){

        BufferedReader reader;

        ArrayList<byte[]> data = new ArrayList<byte[]>();

        try{
            reader = new BufferedReader(new FileReader("/home/yichuan/Documents/leetcode/Emp.txt"));

            String line = reader.readLine();

            while(line != null){
                byte temp[] = stringToByteArray(line);
                data.add(temp);
                // System.out.println(line);
                line = reader.readLine();
            }

            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return data;

    }

    public static void generateData(int num){
        Random rand = new Random();
        int randomNum = rand.nextInt(89999) + 10000;

        System.out.println(randomNum);

    }

    public static ArrayList<Integer> generateUniqueIDs(int num){

        ArrayList<Integer> list = new ArrayList<Integer>();

        for(int i=0; i<num; i++){
            list.add(i);
        }

        Collections.shuffle(list);

        return list;

    }

    public static ArrayList<Integer> generateZip(int num){

        ArrayList<Integer> list = new ArrayList<Integer>();

        Random rand = new Random();

        for(int i=0; i<num; i++){
            list.add(rand.nextInt(89999) + 10000);
        }

        return list;
    }

    public static ArrayList<String> generateMajor(int num){
        String majors[] = {"ME", "EE", "BS", "Bio", "ComS", "Phy", "Math", "CE", "Chem", "test"};

        Random rand = new Random();

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0; i < num; i++){
            list.add(majors[rand.nextInt(9)]);
        }

        return list;

    }

    public static ArrayList<String> generateNames(int num){
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
}