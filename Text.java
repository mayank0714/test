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

public class Text {

	public String notepadFilePath;
	
	/**
	 * CONSTRUCTOR		- Set the file path of note pad file 
	 * @param filePath	- File path of the note pad file
	 */
	
	public Text(String filePath){
		this.notepadFilePath = filePath;
	}
	
	public Text(){
	}

	/**
	 * FUNCTION				- Get text from note pad
	 * @return				- Content of the note pad file
	 * @throws IOException
	 */
	
	public String getAllText() throws IOException {
		
		String val						= "";
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(notepadFilePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				val 				= val + strLine;
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return val;
	}

	/**
	 * FUNCTION 			- Write value in note pad
	 * @param Value			- Content to be written in the note pad file
	 * @throws IOException
	 */
	
	public void setText(String Value) throws IOException {
		
		try {
			
			FileWriter writer 				= new FileWriter(notepadFilePath, true);
            BufferedWriter bufferedWriter 	= new BufferedWriter(writer);
 
            bufferedWriter.newLine();
            bufferedWriter.write(Value);
            bufferedWriter.close();
            
        } catch (Exception e) {
        	TestExecutor.log.LogMessage(Level.ERROR, "Unable to set text in notepad with error - " + e.getMessage());
        	e.printStackTrace();
        }
		
	}

	/**
	 * FUNCTION 			- Search text in note pad file
	 * @param Value			- Content to be searched in the note pad file	
	 * @return				- True, If the content is found, Else, False
	 * @throws IOException
	 */
	
	public boolean searchText(String Value) throws IOException {
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(notepadFilePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(strLine.contains(Value)) {
					in.close();
					return true;
				}
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * FUNCTION 			- Search text and get line from note pad
	 * @param Value			- Text to be searched in the note pad file
	 * @return				- Line content in which the content is found
	 * @throws IOException
	 */
	
	public String getLine(String Value) throws IOException {
		
		String line = "";
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(notepadFilePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(strLine.contains(Value)) {
					in.close();
					return strLine;
				}
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return line;
	}

	/**
	 * FUNCTION				- Get line from note pad based on line number
	 * @param lineNumber	- Line number
	 * @return				- Line content of the line number specified
	 * @throws IOException
	 */
	
	public String getLine(int lineNumber) throws IOException {
		
		String line = "";
		int i 		= 1;
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(notepadFilePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(i==lineNumber) {
					in.close();
					return strLine;
				}
				
				i++;
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR,"Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return line;
	}

	/**
	 * FUNCTION 		- Get text from note pad
	 * @param filePath	- Path of the file whose content is required
	 * @return			- Content of the file in String format
	 */
	
	public String getAllText(String filePath) {
		
		String val						= "";
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(filePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				val 				= val + strLine;
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR,"Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return val;
	}

	/**
	 * FUNCTION 		- Write value in note pad
	 * @param filePath	- Path of the file in which the content is to be written
	 * @param Value		- Content to be written
	 * @throws IOException
	 */
	
	public void setText(String filePath, String Value) throws IOException {
		
		try {
			
			FileWriter writer 				= new FileWriter(filePath, true);
            BufferedWriter bufferedWriter 	= new BufferedWriter(writer);
 
            bufferedWriter.newLine();
            bufferedWriter.write(Value);
            bufferedWriter.close();
            
        } catch (Exception e) {
        	TestExecutor.log.LogMessage(Level.ERROR, "Unable to set text in notepad with error - " + e.getMessage());
        	e.printStackTrace();
        }
		
	}

	/**
	 * FUNCTION 		- Search text in file
	 * @param filePath	- Path of the file
	 * @param Value		- Content to be searched in the file
	 * @return			- True, If the content is found, Else, False
	 * @throws IOException
	 */
	
	public boolean searchText(String filePath, String Value) throws IOException {
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(filePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(strLine.contains(Value)) {
					in.close();
					return true;
				}
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * FUNCTION 		- Search text and get line content
	 * @param filePath	- Path of the file
	 * @param Value		- Content to be searched
	 * @return			- Line content in which the value is found
	 * @throws IOException
	 */
	
	public String getLine(String filePath, String Value) throws IOException {
		
		String line = "";
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(filePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(strLine.contains(Value)) {
					in.close();
					return strLine;
				}
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return line;
	}

	/**
	 * FUNCTION 			- Get line content from the file based on line number
	 * @param filePath		- Path of the file
	 * @param lineNumber	- Line number
	 * @return				- Content of the line at line number specified
	 * @throws IOException
	 */
	
	public String getLine(String filePath, int lineNumber) throws IOException {
		
		String line = "";
		int i 		= 1;
		
		 try {
			
			FileInputStream fstream 	= new FileInputStream(filePath);
			DataInputStream in 			= new DataInputStream(fstream);
			BufferedReader br 			= new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				
				if(i==lineNumber) {
					in.close();
					return strLine;
				}
				
				i++;
			}

			in.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unable to get value from notepad with error - " + e.getMessage());
			e.printStackTrace();
		}
		
		return line;
	}

	
}

