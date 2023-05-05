
package rest.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Level;

import rest.executors.TestExecutor;

public class Notepad {

	/**
	 * FUNCTION 			- Get Runtime value from note pad based on variable name
	 * @param VarName 		- Name of Key whose value is to be found in String format
	 * @return				- Value of the key in String format
	 * @throws IOException
	 */
	
	public String getRuntimeValue(String VarName) throws IOException {
		
		String val						= "";
		int inc 						= 0;
		String tempVarName 				= "";
		if(VarName.contains("+")) {
			int pos 					= VarName.indexOf("+");
			inc 						= Integer.parseInt(VarName.substring(pos+1, VarName.length()));
			tempVarName 				= VarName;
			VarName 					= VarName.substring(0, pos-1);
			
			TestExecutor.log.LogMessage("VarName - " + VarName);
		}
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(Reports.runtimeValuefilePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(strLine.contains(VarName)) {
					
					int indexofequal 	= strLine.indexOf("=");
					val 				= strLine.substring(indexofequal + 1, strLine.length());
					br.close();
					in.close();
					fstream.close();
					if(tempVarName.contains("+")) {
						return FunctionClass.getAutoIncreament(val, inc);
					}
					return val;
				}
				
				//TestExecutor.log.LogMessage(strLine);
			    
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.report.fail("Unable to get value for '" + VarName + "' in notepad with error - " + e.getMessage());
			TestExecutor.log.LogMessage(Level.ERROR,"Unable to get value for '" + VarName + "' in notepad with error - " + e.getMessage());
		}
		
		return val;
	}

	/**
	 * FUNCTION 			- Set Runtime value from Notepad based on variable name
	 * @param VarName 		- Name of the key in String format 
	 * @param Value			- Value of the key in String format
	 * @throws IOException
	 */
	
	public void setRuntimeValue(String VarName, String Value) throws IOException {
		
		String record						= VarName + "=" + Value;
		if(record.contains("\n")) {
			//System.out.println(record);
			record = record.replace("\n", "");
			//System.out.println("After replacing: "+record);
		}
		
		int inc 							= 0;
		
		if(VarName.contains("+")) {
			
			int pos 						= VarName.indexOf("+");
			inc 							= Integer.parseInt(VarName.substring(pos+1, VarName.length()));
			VarName 						= VarName.substring(0, pos-1);
			
			TestExecutor.log.LogMessage("VarName - " + VarName);
			
			//Value 							= TestExecutor.fb.getAutoIncreament(Value, inc);
		}
		
		try {
			
			FileWriter writer 				= new FileWriter(Reports.runtimeValuefilePath, true);
            BufferedWriter bufferedWriter 	= new BufferedWriter(writer);
 
            bufferedWriter.newLine();
            bufferedWriter.write(record);
            bufferedWriter.close();
            writer.close();
            
        } catch (IOException e) {
            TestExecutor.report.fail("Unable to set values '" + Value + "' for '" + VarName + "' in notepad with error - " + e.getMessage());
            TestExecutor.log.LogMessage(Level.ERROR,"Unable to set values '" + Value + "' for '" + VarName + "' in notepad with error - " + e.getMessage());
        }
		
	}
	
}

