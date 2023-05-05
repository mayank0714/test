package rest.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;

import rest.executors.TestExecutor;

public class PropertyFileReader {

	public Properties properties;
	public static PropertyFileReader envProperties;
	
	/** 
	 * Initialize properties file with file (/configuration.properties) 
	 */
	
	public PropertyFileReader() {
		properties 								= loadProperties();
	}
	
	/**
	 * CONSTRUCTOR 			- Initialize properties file with given file path
	 * @param propertyFile	- Path of property file to be loaded in String format
	 */
	
	public PropertyFileReader(String propertyFile) {
		properties 								= loadProperties(propertyFile);
	}
	
	
	/**
	 * FUNCTION - Initialize properties file with  file (/src/test/java/Resources/configuration.properties)
	 * @return 	- Properties as an Object
	 */
	
	public Properties loadProperties() {
		
		File file 								= new File("./configuration.properties");
		FileInputStream fileInput 				= null;
		Properties props 						= new Properties();

		try {
			
			fileInput 							= new FileInputStream(file);
			
			props.load(fileInput);
			
			fileInput.close();
			
		}  catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Execption in load of property file " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		return props;
	}

	/**
	 * FUNCTION 			- Load Properties file
	 * @param propertyFile	- Name of the property file whose properties are to be loaded in String format 
	 * @return				- Properties as an Object
	 */
	
	public Properties loadProperties(String propertyFile) {
		
		File file 							= new File("./src/main/resources/" + propertyFile + ".properties");
		FileInputStream fileInput 			= null;
		Properties props 					= new Properties();

		try {
			
			fileInput 						= new FileInputStream(file);
			
			props.load(fileInput);
			fileInput.close();
			
		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Execption in load of property file " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		return props;
	}
	
	/** 
	 *  Get Instance of Properties Reader
	 */
	
	public static PropertyFileReader getInstance() {
		
		if (envProperties == null) {
			envProperties 					= new PropertyFileReader();
		}
		
		return envProperties;
	}
	
	/**
	 * FUNCTION 			- Get Instance of Properties Reader
	 * @param propertyFile	- Path of Property file
	 * @return				- PropertyFile as an Object
	 */
	
	public static PropertyFileReader getInstance(String propertyFile) {
		
		PropertyFileReader envProperties = null;
		
		if (envProperties == null) {
			envProperties 					= new PropertyFileReader(propertyFile);
		}
		
		return envProperties;
	}
	
	/**
	 * FUNCTION 	- Get Property for key 
	 * @param key	- Key/Property whose value is to be found in String format 
	 * @return		- Value of the Property
	 */
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * FUNCTION 			- Set property on the basis of key and value
	 * @param key			- Property Name in String format
	 * @param value			- Property Value in String format
	 * @throws IOException	-
	 */
	
	public void setProperty(String key, String value) throws IOException {	
		
		String fileName 					= "./src/main/resources/configuration.properties";
		File file 							= new File(fileName);
		FileOutputStream fileOut 			= new FileOutputStream(file);
		Properties props 					= new Properties();
		props 								= properties;
		
		props.setProperty(key, value);
		
		props.store(fileOut, null);
		
		fileOut.close();
	}

}
