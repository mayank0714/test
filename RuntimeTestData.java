package rest.helpers;

import org.apache.log4j.Level;

import rest.executors.TestExecutor;

public class RuntimeTestData {

	Notepad notepad;
	
	public RuntimeTestData() {
		notepad 							= new Notepad();
	}

	/**
	 * FUNCTION 	- Get run time variables names and values
	 * @param val	- String with Run Time Variable name
	 * @return		- Run time value of the variable
	 */

	public String GetRunTimeVariableNames(String val) {
		
		int startIndex 						= 0;
		int endIndex 						= 0;
		String resultString 				= val;
		String RunTimeVariableName 			= "";
				
		TestExecutor.log.LogMessage("Checking Runtime values for '" + val + "'");
		
		try {
			
			boolean isRunTimeValuesPresent	= true;
			int searchIndex = 0;
			do {
			
				if(val.contains("<") && val.contains(">") && val.indexOf("<",searchIndex)>=0 && val.indexOf(">",searchIndex)>0) {
					
					startIndex 				= val.indexOf("<",searchIndex);
					endIndex 				= val.indexOf(">",searchIndex);
					RunTimeVariableName		= val.substring(startIndex+1, endIndex);
					
					TestExecutor.log.LogMessage("Runtime varible found as '" + RunTimeVariableName + "'");
					
					String runTimeVarValue	= notepad.getRuntimeValue(RunTimeVariableName);
					
					if(runTimeVarValue != null && !runTimeVarValue.equals("")) {
						searchIndex 		= val.indexOf("<",searchIndex)+runTimeVarValue.length()-2;
						resultString		= resultString.replace("<"+RunTimeVariableName+">", runTimeVarValue);
						//resultString		= resultString.substring(0, startIndex) + runTimeVarValue + resultString.substring(endIndex+1,resultString.length());
					} else {
						return resultString;
					}
					
					//val						= val.substring(endIndex+1, val.length());
					val 					= resultString;
					TestExecutor.log.LogMessage("Result string - " + resultString);
					
					isRunTimeValuesPresent	= true;
					
				} else {
					return resultString;
				}
				
			} while(isRunTimeValuesPresent);
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR,"Unable to get run time variable name for value - " + val + " due to exception " + e.getMessage());
			TestExecutor.report.fail("Unable to get values for '" + val + "' in notepad with error - " + e.getMessage());
		}
		
		return resultString;
		
	}
	
}
