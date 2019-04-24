package PBStorage;

public class PBFileEntry{
		private String name; // name of file
		private String homePage; //A home pate entry, an integer that is address of a page in a PB storage. -1 indicates that we will not use this field.
		private String LtoP_File; // The name of the file that hosts LtoP_Map.
		private int M; 
		private int sP; 
		private int NumberOfPages; // Number of pages in all chains in the file.
		private float ACL_Min; // Minimum average chain length.
		private float ACL_Max; // Maximum average chain length. 
		private float ACL; // Current average chain length. 
		


		/*
		Set and get methods for each variable.
		*/
		public void setName(String name){
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
		
		public void setHomePage(String homePage){
			this.homePage = homePage;
		}
		
		public String getHomePage(){
			return this.homePage;
		}
		
		public void setLtoP(String filename){
			this.LtoP_File = filename;
		}
		
		public String getLtoP(){
			return this.LtoP_File;
		}
		
		public void setM(int M){
			this.M = M;
		}
		
		public int getM(){
			return this.M;
		}
		
		public void setsP(int sP){
			this.sP = sP;
		}
		
		public int getsP(){
			return this.sP;
		}
		
		public void setNumberOfPages(int NumberOfPages){
			this.NumberOfPages = NumberOfPages;
		}
		
		public int getNumberOfPages(){
			return this.NumberOfPages;
		}
		
		public void setACL_Min(float ACL_Min){
			this.ACL_Min = ACL_Min;
		}
		
		public float getACL_Min(){
			return this.ACL_Min;
		}
		
		public void setACL_Max(float ACL_Max){
			this.ACL_Max = ACL_Max;
		}
		
		public float getACL_Max(){
			return this.ACL_Max;
		}
		
		public void setACL(float ACL){
			this.ACL = ACL;
		}
		
		public float getACL(){
			return this.ACL;
		}		
}