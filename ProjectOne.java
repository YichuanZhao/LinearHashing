package PBStorage;

import java.util.ArrayList;
import java.util.Scanner;

import java.util.Random;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProjectOne {

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

//        int test = byteArrayToInt(IDValue);

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
            reader = new BufferedReader(new FileReader("/home/yichuan/Documents/leetcode/Records.txt"));

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

    
	public static void printBufferContent(byte[] tempBuffer) {
		String print = "[ ";
		for (int i = 0; i<tempBuffer.length; i++) {
			print = print + " " + tempBuffer[i] + " ";
		}
		print = print + "]";
		System.out.println(print);
	}
	
	public static int getPhy(int linearAddr, byte[] temBuffer) {
		byte[] array = new byte[4];
		for (int i = 0; i <4; i++) {
			array[i] = temBuffer[linearAddr*4+i];
		}
		return byteArrayToInt(array);
	}

	public static byte[] writeLtoP_Map(int physicalAddr, byte[] temBuffer, int LinearAddr) {
		int counter = 4 + LinearAddr*4;
		
		byte[] tem = new byte[4];
		tem = intToByteArray(physicalAddr);
		for (int i = 0; i<4; i++) {
			temBuffer[counter+i] = tem[i];
		}
		return temBuffer;
	}
	
	public static void writeOneRecord(int pageAddr, byte[] record, PBStorage MyStorage) throws Exception {
		
		byte[] buffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(pageAddr, buffer);
		
		// 	get number of tuples in the page
		byte[] numTupleByte = new byte[4];
		for (int i = 4; i<8; i++) {
			numTupleByte[i-4] = buffer[i];
		}
		int numTuple = byteArrayToInt(numTupleByte);
		int offset = numTuple*100+8;
		numTupleByte = intToByteArray(numTuple+1);
		//	write page
		for (int i = 4; i<8; i++) {
			buffer[i] = numTupleByte[i-4];
		}
		for (int i = 0; i<record.length; i++) {
			buffer[offset+i] = record[i];
		}
		MyStorage.WritePage(pageAddr, buffer);
	}
	
	public static void writeOneRecordToChain(int firstPage, PBStorage MyStorage, byte[] tuple, 
			PBFileEntry e) throws Exception {
		byte[] initializePage = new byte[MyStorage.pageSize];
		int currentPage = firstPage;
		//	read from page
		byte[] readBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(currentPage, readBuffer);

		int nextPage = getNextPagePointer(currentPage, MyStorage);
		while (nextPage != 0) {
			currentPage = nextPage;
			nextPage = getNextPagePointer(currentPage, MyStorage);
		}
		MyStorage.ReadPage(currentPage, readBuffer);
		int offsetBuffer = 8;
		
		byte[] numTupleByte = new byte[4];
		for (int i = 0; i<4; i++) {
			numTupleByte[i] = readBuffer[4+i];
		}
		int numTuple = byteArrayToInt(numTupleByte);


		offsetBuffer = numTuple*100+8;

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
//		//	print log file
//		System.out.println("current page is: " + Integer.toString(currentPage));
//		
//		byte[] currentBuffer = new byte[MyStorage.pageSize];
//		MyStorage.ReadPage(currentPage, currentBuffer);
//		
//		byte[] part = new byte[4];
//		for (int i = 0; i < 4; i++) {
//			part[i] = currentBuffer[i+4];
//		}
//		System.out.println("current page record number is: " +  Integer.toString(byteArrayToInt(part)));
//		System.out.println("ACL is: " + Float.toString(e.getACL()));
//		System.out.println("sP is: " + Integer.toString(e.getsP()));
	}
	
	public static int getNextPagePointer(int currentPageAddr, PBStorage MyStorage) throws Exception {
		
		byte[] currentBuffer = new byte[MyStorage.pageSize];
		MyStorage.ReadPage(currentPageAddr, currentBuffer);
		
		byte[] part = new byte[4];
		for (int i = 0; i < 4; i++) {
			part[i] = currentBuffer[i];
		}
		int nextPage = byteArrayToInt(part);
		
		return nextPage;
	}
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
//		printBufferContent(temBuffer);
		pagesBuffered.add(temBuffer);
		
		
//		System.out.println("Next page is");
//		System.out.println(nextPage);
		
		MyStorage.DeAllocatePage(firstPageAddr);
		fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()-1);
		while (nextPage != 0) {
			byte[] temBuffer1 = new byte[MyStorage.pageSize];
			MyStorage.ReadPage(nextPage, temBuffer1);
			pagesBuffered.add(temBuffer1);
			int tem = nextPage;
			nextPage = getNextPagePointer(tem, MyStorage);
			MyStorage.DeAllocatePage(tem);
			fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()-1);
//			printBufferContent(pagesBuffered.get(1));
		}
//		System.out.println(pagesBuffered.size());
		

		//	allocate new home pages for M side and 2M side
		byte[] initializePage = new byte[MyStorage.pageSize];
		
		int homePageMside = MyStorage.AllocatePage();
//		System.out.println(homePageMside);
		fileHomePageBuffer = writeLtoP_Map(homePageMside, fileHomePageBuffer, fileEntry.getsP());
		MyStorage.WritePage(fileHomePage, fileHomePageBuffer);
		MyStorage.WritePage(homePageMside, initializePage);
		
		fileEntry.setNumberOfPages(fileEntry.getNumberOfPages()+1);
		
		int homePage2Mside = MyStorage.AllocatePage();
//		System.out.println(homePage2Mside);
		fileHomePageBuffer = writeLtoP_Map(homePage2Mside, fileHomePageBuffer, fileEntry.getsP()+fileEntry.getM());
		MyStorage.WritePage(fileHomePage, fileHomePageBuffer);
		MyStorage.WritePage(homePage2Mside, initializePage);
		
//		printBufferContent(fileHomePageBuffer);
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
//				System.out.println(firstPageAddr);
				writeOneRecordToChain(firstPageAddr, MyStorage, temRecord, fileEntry);
			}
		}
		//	set sP value
//		System.out.println("sP is");
//		System.out.println(fileEntry.getsP());
		fileEntry.setsP(fileEntry.getsP()+1);

	}
	
	public static byte[] getTuple() {
		byte[] res = new byte[100];
		
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		// create storage.
		PBStorage MyStorage = new PBStorage();
		String folderName = "/home/yichuan/Documents/COMS661/test";
		int pageSize = 4096;
		int nPages = 10000;
		String filename = "projectOne";
		MyStorage.CreateStorage(folderName, pageSize, nPages);
		System.out.println(
				"--Storage has been created successfully" + "with length " + MyStorage.PBFile.length());
		MyStorage.LoadStorage(folderName);
		System.out.println(
				"--Storage has been loaded successfully" + "with length " + MyStorage.PBFile.length());
		
		byte[] initializePage = new byte[MyStorage.pageSize];
		byte[] tempBuffer = new byte[MyStorage.pageSize];
		// asks for a Page numbers
		int homePage = MyStorage.AllocatePage();
		MyStorage.WritePage(homePage, initializePage);
		System.out.println("Newpage numbers: " + homePage);
		MyStorage.printStats();
		MyStorage.addPBFileEntry(filename, Integer.toString(homePage));
		// homePage of the file written
		int counter = 0;
		for (int i = 0; i < tempBuffer.length; i++) {
			tempBuffer[i] = 'x';
		}
		for (int i = 0; i < intToByteArray(0).length; i++) {
			tempBuffer[i] = intToByteArray(0)[i];
			counter = i;
		}
		
		String LtoP_File = "LtoPOf_"+filename;
//		String M = Integer.toString(3);
//		String sP = Integer.toString(0);
//		String ACL_Min = "1.25";
//		String ACL_Max = "1.50";
//		String ACL ="0";
		PBFileEntry e = new PBFileEntry();
		e.setName(filename);
		e.setHomePage(Integer.toString(homePage));
		e.setLtoP(LtoP_File);
		e.setM(3);
		e.setsP(0);
		e.setNumberOfPages(3);
		e.setACL_Min((float) 0.0);
		e.setACL_Max((float) 1.5);
		e.setACL((float) 1.0);
		
		
		int M_tem = e.getM();
		for (int i = 0; i<M_tem; i++) {
			int homePageOfChain = MyStorage.AllocatePage();
			MyStorage.WritePage(homePageOfChain, initializePage);
//			System.out.println(homePageOfChain);
			tempBuffer = writeLtoP_Map(homePageOfChain, tempBuffer, i);
		}
		MyStorage.WritePage(homePage, tempBuffer);
		
		//	generate a tuple with 100 bytes first 4 bytes is ID and random for others
		byte[] tuple = new byte[100];
		byte[] ID_tem = new byte[4];
		
		ArrayList<byte[]> records = getData();
		System.out.println(records.size());
		
		for (int k = 0; k<10000; k++) {
			
//			new Random().nextBytes(tuple);
			tuple = records.get(k);
//			ID_tem = intToByteArray(k);
//			for (int j = 0; j<4; j++) {
//				tuple[j] = ID_tem[j];
//			}
//			
//			tuple = getTuple();
			
			//	get the ID from the byte array of tuples
			byte[] tempID = new byte[4];
			for (int k1 = 0; k1<4; k1++) {
				tempID[k1] = tuple[k1];
			}
			int numID = byteArrayToInt(tempID);
			
			//	calculate page number
			MyStorage.LoadStorage(folderName);
			int home_Page = MyStorage.getHomePage(filename);
			MyStorage.ReadPage(home_Page, tempBuffer);
			
			int firstPage = getPhy(numID%M_tem+home_Page+1, tempBuffer);
			
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
		
//		while (e.getACL()>1.5) {
//			doSplit(MyStorage, e);
//			if (e.getsP()>=e.getM()) {
//				e.setsP(0);
//				e.setM(2*e.getM());
//			}
//			e.setACL( (float) e.getNumberOfPages()/(e.getM()+e.getsP()) );
//			System.out.println(e.getNumberOfPages());
//		}
//		doSplit(MyStorage, e);
		System.out.println("Current total page number is " + e.getNumberOfPages()); //page number
		System.out.println("The current acl value is " + e.getACL()); //true acl value
		System.out.println("The current sP value is " + e.getsP()); //sP value
		System.out.println("The current M is " + e.getM()); // M value
//		MyStorage.printStats();
		
//		byte[] read_Buffer = new byte[MyStorage.pageSize];
//		for (int i = 0; i<=e.getNumberOfPages(); i++) {
//			MyStorage.ReadPage(i, read_Buffer);
//			printBufferContent(read_Buffer);
//		}
		
		
//		MyStorage.LoadStorage(folderName);
//		int currentPage = MyStorage.getHomePage(filename);
//		if (currentPage == -1) {
//			System.out.println("file does not exist");
//		}
//		MyStorage.ReadPage(currentPage, tempBuffer);

	}
}
