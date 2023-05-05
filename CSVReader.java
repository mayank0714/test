package rest.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class CSVReader {

	public int RecordCount;
	public Scanner sc;
	public String filepath="";
	public HashMap<String,String>[] csvData;
	
	public CSVReader(String filePath) {
		RecordCount = 0;
		this.filepath = filePath;
		
		try {
			sc = new Scanner(new File(filepath)); 
			sc.useDelimiter(","); 
			while(sc.hasNextLine()) {
				sc.nextLine();
				RecordCount++;
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		csvData = new HashMap[RecordCount];
	}
	
	public int GetRecordCount() {
		return RecordCount;
	}
	
	public HashMap<String,String>[] GetCsvData() {
		
		Boolean firstRow = true;
		String firstStr[]=null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line =  null;
			int recCount=0;
			while((line=br.readLine())!=null){
				if(firstRow) {
					firstRow=false;
					firstStr = line.split(",");
				} else {
					String str[] = line.split(",");
					csvData[recCount] = new HashMap<String, String>();
			        for(int i=0;i<str.length;i++){
			            csvData[recCount].put(firstStr[i], str[i]);
			            System.out.println("recCount - "+String.valueOf(recCount));
			            System.out.println("firststr - "+firstStr[i]);
			            System.out.println("str - "+str[i]);
			        }
			        recCount++;
				}
		        
		    }
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return csvData;
	}
	
}
