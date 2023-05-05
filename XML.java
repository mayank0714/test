package rest.helpers;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rest.executors.TestExecutor;


public class XML {

	String path 								= "";
	
	/**
	 * CONSTRUCTOR 		- Set the value of 'path' variable
	 * @param xmlPath	- file path of the xml file
	 */
	
	public XML(String xmlPath) {
		path 									= xmlPath;
	}
	
	/**
	 * FUNCTION 		- Find the tag in the xml file and return the tag's value 
	 * @param tagName	- Name of the tag whose value is to be returned
	 * @return			- Value of the tag
	 */
	
	public String readTagVal(String tagName) {	
		
		String tagVal 							= null;
		File file 								= new File(path);
		
		try {
			
			DocumentBuilder builder 			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc 						= builder.parse(file);
			tagVal 								= doc.getElementsByTagName(tagName).item(0).getTextContent();
		} catch(Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Could not get data from config.XML file for the tag "+tagName);
		}
		
		return tagVal;
	}
	
	/**
	 * FUNCTION 			- Get all node-value pair at a specified xpath from a 'xml' file  
	 * @param xpathToFind	- Xpath of the node
	 * @return				- Map of 'node-value' pair at the xpath specified
	 */
	
	public HashMap<String, String> getXPath(String xpathToFind) {
		
		HashMap<String, String> mapToReturn 	= new HashMap<>();
		String nodeName 						= "";
		String nodeVal 							= "";
		File file 								= new File(path);
		
		try {
			
			DocumentBuilder builder 			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc 						= builder.parse(file);
			XPathFactory xpathFactory 			= XPathFactory.newInstance();
			XPath xpath 						= xpathFactory.newXPath();
			XPathExpression expr 				= xpath.compile(xpathToFind);
			NodeList  nodes 					= (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			System.out.println(nodes.item(0).getChildNodes().getLength());
			
			NodeList childNodes 				= nodes.item(0).getChildNodes();
			
			System.out.println(childNodes.getLength());
			
			for (int i = 0; i < childNodes.getLength(); i++) {
				
				Node node 						= childNodes.item(i);
				
				switch(node.getNodeType()) {
					case 1:
						nodeName 				= node.getNodeName();
						nodeVal 				= node.getTextContent().trim();
						
						mapToReturn.put(nodeName, nodeVal);
						
						System.out.println(nodeName + ":" + nodeVal);
						
						break;
				}
			}
        
		} catch(Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Could not get data from config.XML file for the path "+xpathToFind);
		}
		
		return mapToReturn;
	}
}
