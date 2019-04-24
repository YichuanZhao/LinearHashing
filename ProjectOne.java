package PBStorage;

import java.util.ArrayList;
import java.util.Scanner;

import java.util.Random;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProjectOne {

	/* intToByteArray function is used to convert
	 * int to byte array, in this project, each int stored
	 * in the datatbase as 4 bytes */
	public static byte[] intToByteArray(int value) {
		byte[] array = new byte[4];
		for (int i = 3; i >= 0; i--) {
			array[3 - i] = (byte) (value >> i * 8);
		}
		return array;
	}

	/* byteArrayToInt function is used to convert 
	 * convert byte array to int*/
	public static int byteArrayToInt(byte[] byteArray) {
		int value = 0;
		for (int i = 0; i < byteArray.length; i++) {

			value += ((int) byteArray[i] & 0xff) << (8 * (3 - i));
		}
		return value;
	}

	/* stringToByteArray function is used to Convert a string to byte array (byte[]). 
	 * A string needs to be converted to a byte array to be inserted into a page*/
    public static byte[] stringToByteArray(String str){

        int index = str.indexOf(',');

        String ID = str.substring(1, index);

        int l = str.length();

        String dataString = str.substring(index + 1, l-1);

        byte[] ans = new byte[100];

        char[] buffer = dataString.toCharArray();

        int res = Integer.valueOf(ID);

        byte IDValue[] = intToByteArray(res);

        for(int i=0; i<4; i++){
            ans[i] = IDValue[i];
        } 

        for(int i=4; i<dataString.length(); i++){
            ans[i] = (byte)buffer[i];
        }
  
        return ans;
    }
    
    /* Read all the records from file and save them into arraylist of byte arrays.
    * Prepare all the records to be in the format of byte arrays, in order for 
    * them to be inserted into pages.*/
    public static ArrayList<byte[]> getRecords(String textFilePath){

        BufferedReader reader;

        ArrayList<byte[]> data = new ArrayList<byte[]>();

        try{
            reader = new BufferedReader(new FileReader(textFilePath));

            String line = reader.readLine();

            while(line != null){
                byte temp[] = stringToByteArray(line);
                data.add(temp);
                line = reader.readLine();
            }

            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return data;

    }

    /* print the content of the buffer, this function is used for debgging*/
	public static void printBufferContent(byte[] tempBuffer) {
		String print = "[ ";
		for (int i = 0; i<tempBuffer.length; i++) {
			print = print + " " + tempBuffer[i] + " ";
		}
		print = print + "]";
		System.out.println(print);
	}
	
	/* Get the physical address given the linear address and the hash map, LtoP_Map. 
	* LtoP_Map maps the linear address to the physical address*/
	public static int getPhy(int linearAddr, byte[] hashmap) {
		byte[] phyAddr = new byte[4];
		for (int i = 0; i <4; i++) {
			phyAddr[i] = hashmap[linearAddr*4+i];
		}
		return byteArrayToInt(phyAddr);
	}

	/* Write the address of a new allocated first page of a chain to the hash map of LtoP_Map */
	public static byte[] writeLtoP_Map(int physicalAddr, byte[] hashmap, int linearAddr) {
		// compute the location that store the physical address of the page,
		// since we use 4 bytes to store an address, we do the following calcualtion
		int counter = 4 + linearAddr*4;
		
		byte[] tem = new byte[4];
		tem = intToByteArray(physicalAddr);
		for (int i = 0; i<4; i++) {
			hashmap[counter+i] = tem[i];
		}
		return hashmap;
	}
	
	/* This function is used to write a record/tuple to a page given the page address 
	* and content of the record in byte. */
	public static void writeOneRecord(int pageAddr, byte[] record, PBStorage MyStorage) throws Exception {
		
		byte[] buffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(pageAddr, buffer);
		
		// get number of tuples in the page, this information is stored in 4-8th bytes in each page
		byte[] numTupleByte = new byte[4];
		for (int i = 4; i<8; i++) {
			numTupleByte[i-4] = buffer[i];
		}
		int numTuple = byteArrayToInt(numTupleByte);
		// calculate the position to store the new record/tuple
		int offset = numTuple*100+8;
		// update the number of bytes in the pages
		numTupleByte = intToByteArray(numTuple+1);
		//	write the record into page
		for (int i = 4; i<8; i++) {
			buffer[i] = numTupleByte[i-4];
		}
		for (int i = 0; i<record.length; i++) {
			buffer[offset+i] = record[i];
		}
		MyStorage.WritePage(pageAddr, buffer);
	}

	/* This function is used to write one record to a chain given the first page address and record, 
	* if all the existing pages of this chain are full, then we allocate a new page and write 
	* the record to the page. */
	public static void writeOneRecordToChain(int firstPage, PBStorage MyStorage, byte[] tuple, 
			PBFileEntry e) throws Exception {
		byte[] initializePage = new byte[MyStorage.pageSize];
		int currentPage = firstPage;
		//	readBuffer is used to store the content read from page
		byte[] readBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(currentPage, readBuffer);
		// To check whether a page is full, we use next page pointer which is stored in the 
		// first 4 bytes in the page, if this pointer is 0, then this page is not full.
		// Thus the following is to find the page that is not full.
		int nextPage = getNextPagePointer(currentPage, MyStorage);
		while (nextPage != 0) {
			currentPage = nextPage;
			nextPage = getNextPagePointer(currentPage, MyStorage);
		}
		// read the content of the non-full page and get the location/offset to write the record
		MyStorage.ReadPage(currentPage, readBuffer);
		byte[] numTupleByte = new byte[4];
		for (int i = 0; i<4; i++) {
			numTupleByte[i] = readBuffer[4+i];
		}
		int numTuple = byteArrayToInt(numTupleByte);
		int offsetBuffer = numTuple*100+8;
		// Try to write the record to the found non-full page, if the page has the enough space
		// for the record, then write it to this page, otherwise allocate a new page and wirte 
		// the record into this newly allocated page.
		if (offsetBuffer+100<readBuffer.length) {
			writeOneRecord(currentPage, tuple, MyStorage);
		}
		else {
			int newPage = MyStorage.AllocatePage();
			MyStorage.WritePage(newPage, initializePage);
			e.setNumberOfPages(e.getNumberOfPages()+1);
			e.setACL( (float) e.getNumberOfPages()/(e.getM()+e.getsP()) );
			byte[] newPageByte = new byte[4];
			newPageByte = intToByteArray(newPage);
			for (int i = 0; i<4; i++) {
				readBuffer[i] = newPageByte[i];
			}
			MyStorage.WritePage(currentPage, readBuffer);
			currentPage = newPage;
			writeOneRecord(currentPage, tuple, MyStorage);	
		}
	}
	
	/* get the next page address given the current page address */
	public static int getNextPagePointer(int currentPageAddr, PBStorage MyStorage) throws Exception {
		
		byte[] currentBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(currentPageAddr, currentBuffer);
		// next page address is stored in the first 4 bytes of the current page
		byte[] part = new byte[4];
		for (int i = 0; i < 4; i++) {
			part[i] = currentBuffer[i];
		}
		int nextPage = byteArrayToInt(part);
		return nextPage;
	}

	/* This function is used to do the split if ACL is larger than maximum ACL. */
	public static void doSplit(PBStorage MyStorage, PBFileEntry fileEntry) throws Exception {
		//	read file home page to a buffer
		int fileHomePage = Integer.parseInt( fileEntry.getHomePage() );
		byte[] fileHomePageBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(fileHomePage, fileHomePageBuffer);
		//	Used to store all the records of splitted chain
		ArrayList<byte[]> pagesBuffered = new ArrayList<byte[]>();
		//	Get the home page address of a chain
		int firstPageAddr = getPhy(fileEntry.getsP()+fileHomePage+1, fileHomePageBuffer);
		//	Store all splitted records into array list pagesBuffered
		int nextPage = getNextPagePointer(firstPageAddr, MyStorage);
		byte[] temBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(firstPageAddr, temBuffer);
		pagesBuffered.add(temBuffer);
		MyStorage.DeAllocatePage((long) firstPageAddr);
		fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()-1);
		while (nextPage != 0) {
			byte[] temBuffer1 = new byte[MyStorage.pageSize];
			MyStorage.ReadPage(nextPage, temBuffer1);
			pagesBuffered.add(temBuffer1);
			int tem = nextPage;
			nextPage = getNextPagePointer(tem, MyStorage);
			MyStorage.DeAllocatePage((long) tem);
			fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()-1);
		}

		// allocate new home pages for M side and 2M side
		byte[] initializePage = new byte[MyStorage.pageSize];

		// write the home page of M side
		int homePageMside = MyStorage.AllocatePage();
		fileHomePageBuffer = writeLtoP_Map(homePageMside, fileHomePageBuffer, fileEntry.getsP());
		MyStorage.WritePage(fileHomePage, fileHomePageBuffer);
		MyStorage.WritePage(homePageMside, initializePage);
		
		fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()+1);
		
		// write the home page of 2M side
		int homePage2Mside = MyStorage.AllocatePage();
		fileHomePageBuffer = writeLtoP_Map(homePage2Mside, fileHomePageBuffer, fileEntry.getsP()+fileEntry.getM());
		MyStorage.WritePage(fileHomePage, fileHomePageBuffer);
		MyStorage.WritePage(homePage2Mside, initializePage);
		
		fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()+1);

		//	write back the records to two new chains
		for (int i = 0; i<pagesBuffered.size(); i++) {
			byte[] temByteArray = pagesBuffered.get(i);
			byte[] numRecordsByte = new byte[4];
			for (int j = 0; j<4; j++) {
				numRecordsByte[j] = temByteArray[j+4];
			}
			byte[] temRecord = new byte[100];
			for (int j = 0; j<byteArrayToInt(numRecordsByte); j++) {
				byte[] recordID = new byte[4];
				for (int k = 0; k<100; k++) {
					temRecord[k] = temByteArray[k+8+j*100];
					if (k<4) recordID[k] = temByteArray[k+8+j*100];
				}
				if (byteArrayToInt(recordID)%(2*fileEntry.getM()) == fileEntry.getsP()) firstPageAddr = homePageMside;
				else firstPageAddr = homePage2Mside;
				writeOneRecordToChain(firstPageAddr, MyStorage, temRecord, fileEntry);
			}
		}
		
		//	set sP value
		fileEntry.setsP(fileEntry.getsP()+1);

	}
	

	public static void main(String[] args) throws Exception {
		// Step 1: Create storage.
		PBStorage MyStorage = new PBStorage();
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter the folder path of your storage: ");
		String folderName = reader.nextLine(); // Scans the next token of the input as an int.
		System.out.println("Enter the page size: ");
		String PS = reader.nextLine();
		System.out.println("Enter the number of pages in your storage: ");
		String NP = reader.nextLine();
		System.out.println("Enter the value of M: ");
		String MString = reader.nextLine();
		reader.close();
		// get page size and number of pages
		int pageSize = Integer.parseInt(PS);
		int nPages = Integer.parseInt(NP);
		
		String filename = "projectOne";
		MyStorage.CreateStorage(folderName, pageSize, nPages);
		System.out.println(
				"--Storage has been created successfully" + "with length " + MyStorage.PBFile.length());
		MyStorage.LoadStorage(folderName);
		System.out.println(
				"--Storage has been loaded successfully" + "with length " + MyStorage.PBFile.length());
		
		byte[] initializePage = new byte[MyStorage.pageSize];
				
		// Step 2: Allocate the home page for the linearly hashed file
		int homePage = MyStorage.AllocatePage();
		MyStorage.WritePage(homePage, initializePage);
		System.out.println("Newpage numbers: " + homePage);
		MyStorage.printStats();
		// Add a filename and other properties of files to json file created initially in create storage.
		MyStorage.addPBFileEntry(filename, Integer.toString(homePage));

		// Step 3: Create an LHConfig JSON element and set the homePage to the page allocated for LtoP_Map.
		byte[] LtoP_Map = new byte[MyStorage.pageSize];
		int counter = 0;
		for (int i = 0; i < LtoP_Map.length; i++) {
			LtoP_Map[i] = 'x';
		}
		for (int i = 0; i < intToByteArray(0).length; i++) {
			LtoP_Map[i] = intToByteArray(0)[i];
			counter = i;
		}
		
		String LtoP_File = "LtoPOf_"+filename;
		int M = Integer.parseInt(MString);
		// set entry.java
		PBFileEntry e = new PBFileEntry();
		e.setName(filename);
		e.setHomePage(Integer.toString(homePage));
		e.setLtoP(LtoP_File);
		e.setM(M);
		e.setsP(0);
		e.setNumberOfPages(M);
		e.setACL_Min((float) 1.25);
		e.setACL_Max((float) 1.5);
		e.setACL((float) 1.0);
		
		// Step 4: Allocate M pages to serve as home pages of M chains
		int M_tem = e.getM();
		for (int i = 0; i<M_tem; i++) {
			int homePageOfChain = MyStorage.AllocatePage();
			MyStorage.WritePage(homePageOfChain, initializePage);
			LtoP_Map = writeLtoP_Map(homePageOfChain, LtoP_Map, i);
		}
		MyStorage.WritePage(homePage, LtoP_Map);
		
		//	generate a tuple with 100 bytes first 4 bytes is ID and random for others
		byte[] tuple = new byte[100];
		byte[] ID_tem = new byte[4];
		// Step 5: Get the records and write them to the file
		String filePath = "Records.txt"; // directory of the text file which contains the records
		ArrayList<byte[]> records = getRecords(filePath);
		System.out.println(records.size());
		
		for (int k = 0; k<10000; k++) {
			
			tuple = records.get(k);
			
			//	get the ID from the byte array of tuples
			byte[] tempID = new byte[4];
			for (int k1 = 0; k1<4; k1++) {
				tempID[k1] = tuple[k1];
			}
			int numID = byteArrayToInt(tempID);
			
			//	get home page of the file
			MyStorage.LoadStorage(folderName);
			int home_Page = MyStorage.getHomePage(filename);
			MyStorage.ReadPage(home_Page, LtoP_Map);
			// get the first page of the chain where write the record
			int firstPage = getPhy(numID%M_tem+home_Page+1, LtoP_Map);
			
			writeOneRecordToChain(firstPage, MyStorage, tuple, e);

//			System.out.println(e.getNumberOfPages());
			//	Check and do split
			while (e.getACL()>1.5) {
				doSplit(MyStorage, e);
				if (e.getsP()>=e.getM()) {
					e.setsP(0);
					e.setM(2*e.getM());
				}
				e.setACL( (float) e.getNumberOfPages()/(e.getM()+e.getsP()) );
//				System.out.println(e.getNumberOfPages());
			}
		}

		System.out.println("Current total page number is " + e.getNumberOfPages()); //page number
		System.out.println("The current acl value is " + e.getACL()); //true acl value
		System.out.println("The current sP value is " + e.getsP()); //sP value
		System.out.println("The current M is " + e.getM()); // M value
		MyStorage.printStats();
	}
}
