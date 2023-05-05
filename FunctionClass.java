package rest.helpers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import rest.executors.TestExecutor;

public class FunctionClass {
	
	DateFormat dateFormat	= new SimpleDateFormat("yyyy-mm-dd");

    Notepad notepad;
    public static boolean Allnod = true;

    public FunctionClass() {
        notepad = new Notepad();
    }

	/**
	 * FUNCTION		- Remove braces from string
	 * @param name	- String to be modified
	 */

    public String removeBracesFromString(String name) {

        String expectedname = null;

        if (name.contains("(")) {

            try {
                expectedname = name.substring((name.indexOf("(") + 1), name.indexOf(")"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            expectedname = name;
        }

        return expectedname;
    }
    
	public String getTagText(String xml, String tagName) throws IOException {
		
		System.out.println(xml);
		DOMParser parser = new DOMParser();
		try {
		    parser.parse(new InputSource(new java.io.StringReader(xml)));
		    Document doc = parser.getDocument();
		    String message = doc.getElementsByTagName(tagName).item(0).getTextContent();
		    //System.out.println("Tag Text is: "+message);
		    
		    return message;
		} catch (SAXException e) {
			e.printStackTrace();
		    // handle SAXException 
		} catch (IOException e) {
			e.printStackTrace();
		    // handle IOException 
		}
		return null;
	}
	
	public String getAttributeValue(String xml, String tagName,String attr, String index) throws IOException {
		
		System.out.println(xml);
		DOMParser parser = new DOMParser();
		try {
		    parser.parse(new InputSource(new java.io.StringReader(xml)));
		    Document doc = parser.getDocument();
		    Node val = doc.getElementsByTagName(tagName).item(Integer.valueOf(index)).getAttributes().getNamedItem(attr);
		    
		    //System.out.println("Attribute Value is:" +val.getNodeValue());
		    
		    return val.getNodeValue();
		} catch (SAXException e) {
			e.printStackTrace();
		    // handle SAXException 
		} catch (IOException e) {
			e.printStackTrace();
		    // handle IOException 
		}
		return null;
	}
    
    
    /**
	 * FUNCTION 		- Set headers for a request
	 * @param headers	- Header names separated by ':' to be set for a request in String format
	 * @param values	- Values separated by ':' corresponding to the headers in  String format
	 * @return			- Request specification of request 
	 */

    public RequestSpecification setHeader(String headers, String values) {
        String delim			= ":";
        Pattern pr				= Pattern.compile(delim);
        String[] headerKeys 	= pr.split(headers);
        String[] headerValues 	= pr.split(values);
		
        RequestSpecification requestSpec = null;
        
        HashMap<String, String> headVal = new HashMap<>();

        RequestSpecBuilder builder 		= new RequestSpecBuilder();
        for (int i = 0; i < headerKeys.length; i++) {

            String key		= headerKeys[i];
            String value 	= headerValues[i];
            
            //System.out.println("Key is: " + key + " and value is: " + value);
			
            switch (key) {
			  
            	case "Content-Type": 
            		builder.setContentType(value);
            		break;
			  
            	case "Cookie": 
            		String []allCookiePair = value.split(";");
            		for(int j=0;j<allCookiePair.length;j++) 
            		{
            			headVal.put(allCookiePair[j].split("=")[0],allCookiePair[j].split("=")[1]); 
            		}
            		builder.addCookies(headVal);
            		break;
            	case "Accept":
            		builder.setAccept(value);
            		break;
            	case "X-Spree-Token":
                    builder.addHeader("X-Spree-Token", value);
                    break;
            	case "AuthorizationFromWebUINetworkTab":
            		if(value.equalsIgnoreCase("true")) {
            			builder.addHeader("Authorization", GetTokenFromWebDeveloperNetworkTab.getToken());
            		}
            		break;
            	case "Authorization":
            		builder.addHeader("Authorization", value);
                    break;	    
			  default: 
				  builder.addHeader(key, value);
				  break; 
            }	         
        }
        requestSpec = builder.build();
        return requestSpec;
    }
    
    
    /**
	 * FUNCTION 		- Set form data field for request
	 * @param body		- Form Data value (key-value pair separated by '=') as 'formdata(key1=value1,key2=value2,key3=value3...)' separated by ',' to be set in String format
	 * @return			- Request specification of request 
	 */

    public RequestSpecification setformdata(String body,RequestSpecification req) {

        RequestSpecification requestSpec 	= null;
        RequestSpecBuilder builder 			= new RequestSpecBuilder();
        HashMap<String, String> formdataVal = new HashMap<>();
        builder.addRequestSpecification(req);
        
		try {
			if(body.contains("formdata(")) {
		
		
				body = body.replace("formdata(", "");
				body = body.replace(")", "");

				String[] formKeys 	= body.split(";");
		        for (int i = 0; i < formKeys.length; i++) {
		        	
		        	String key 		= formKeys[i].split("=")[0];
		        	String value 	= formKeys[i].split("=")[1];
		        	
		        	if(key.trim().charAt(0)=='*') {
		        		builder.addMultiPart(key.substring(1,key.length()), new File(value));
		        		continue;
		        	}
		        	if(value.equalsIgnoreCase("blank")) {
		        		value="";
		        	}
		            //System.out.println("form data Key is: " + formKeys[i]);
		            System.out.println("Key - " + key + "\n Value - " + value);
		                            
		            formdataVal.put(key, value);
		        }
		        
		        builder.addParams(formdataVal);
		    	
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}

        requestSpec = builder.build();
        return requestSpec;
    }
    
    /**
	 * FUNCTION 				- Validate all Node Values
	 * @param resp				- Response in which node values to be verified as a Response variable
	 * @param parentPath 		- Parent path for nodes to be verified separated by ',' in a String format
	 * @param node				- Nodes to be verified separated by ',' in a String format
	 * @param nodeValue			- Expected Nodes Values corresponding to the nodes to be verified separated by ',' in a String format
	 * @param nodeArrayValue 	- node array values obtained from response
	 * @return					- Array list of obtained values. 
     * @throws IOException 
	 */
    
    public ArrayList<String> validateNodeValue(Response resp, String parentPath, String node, String nodeValue,
            ArrayList<String> nodeArrayValue) throws IOException {

        int Parent_no = 0;
        Allnod = true;
        JsonPath jsonPathEvaluator 	= resp.jsonPath();
        String[] parentPathArray 	= { parentPath };

        if (parentPath.contains(",")) {
            parentPathArray = parentPath.split(",");
        }

        String runTimeVar = "";

        TestExecutor.log.LogMessage("parentPath - " + parentPath);
        TestExecutor.log.LogMessage("node - " + node);
        TestExecutor.log.LogMessage("nodeValue - " + nodeValue);
        TestExecutor.log.LogMessage("parentPathArray.length - " + String.valueOf(parentPathArray.length));

        // Iterating over array of Parent Paths

        for (Parent_no = 0; Parent_no < parentPathArray.length; Parent_no++) {

            int noOfNodes = 1;

            TestExecutor.log.LogMessage("parentPathArray[Parent_no] - " + parentPathArray[Parent_no]);

            if (!parentPathArray[Parent_no].equals("$") && !parentPathArray[Parent_no].contains("$[")) {
                noOfNodes = resp.body().path(parentPathArray[Parent_no] + ".size()");
            }

            String[] nodeValueArray = { nodeValue };
            String[] nodeArray 		= { node };

            if (nodeValue.contains(",")) {
                nodeValueArray = nodeValue.split(","); // Value picked from excel sheet
            }

            if (node.contains(",")) {
                nodeArray = node.split(",");
            }

            if (!(noOfNodes == 0)) {

                nodeArrayValue.clear();

                char last = nodeArray[Parent_no].charAt(nodeArray[Parent_no].length() - 1);

                // Check whether the Node is in JSON Array

                if(nodeArray[Parent_no].contains("xmlTagValue")) {
                	String temp = nodeArray[Parent_no];
                	temp = temp.replace("xmlTagValue(", "");
                	temp = temp.replace(")", "");
                	temp = temp.replace("#", "");
                	String []tempArgs = temp.split(";");
                	for(int i=0;i<tempArgs.length;i++) {
                    	if(tempArgs[i].contains("<") && tempArgs[i].contains(">")) {
                    		String tp = tempArgs[i].substring(tempArgs[i].indexOf("<")+1,tempArgs[i].indexOf(">"));
                    		//System.out.println("tp is: "+tp);
                    		//System.out.println("rtv is: "+new Notepad().getRuntimeValue(tp));
                    		tempArgs[i] = tempArgs[i].replace("<"+tp+">", new Notepad().getRuntimeValue(tp));
                    	}
                		
                	}
                	
                	nodeArrayValue.add(getTagText(tempArgs[0],tempArgs[1]));
                	
                } else {
	                if (last == '#') {
	
	                    nodeArray[Parent_no] = nodeArray[Parent_no].replace("#", "");
	                    Object obj = null;
	
	                    if (parentPathArray[Parent_no].equals("$") || parentPathArray[Parent_no].contains("$["))
	                        obj = jsonPathEvaluator.get(nodeArray[Parent_no]);
	                    else
	                        obj = jsonPathEvaluator.get(parentPathArray[Parent_no] + "." + nodeArray[Parent_no]);
	
	                    nodeArrayValue.add(obj.toString()); // Value of node fetched from Server
	
	                    TestExecutor.log.LogMessage("Node Value obtained - " + obj.toString());
	
	                } else {
	                    Object obj = jsonPathEvaluator.get(parentPathArray[Parent_no] + "." + nodeArray[Parent_no]);
	                    //Object obj = jsonPathEvaluator.get(nodeArray[Parent_no]);
	                    nodeArrayValue.add(obj.toString()); // Value of node fetched from Server
	                }
                }
                Boolean nod = true;

                // For each node in nodeArrayValue

                for (int d = 0; d < nodeArrayValue.size(); d++) {

                    if (nodeArrayValue.get(d) != null)
                        TestExecutor.log.LogMessage("Do Nothing");
                    else
                        nodeArrayValue.set(d, "");

                    try {

                        if (nodeValueArray[Parent_no].trim().contains("<")
                                && nodeValueArray[Parent_no].trim().contains(">")) {

                            int startIndex 				= nodeValueArray[Parent_no].trim().indexOf("<");
                            int endIndex 				= nodeValueArray[Parent_no].trim().indexOf(">");
                            runTimeVar 					= "";
                            runTimeVar 					= nodeValueArray[Parent_no].trim().substring(startIndex + 1, endIndex);
                            //nodeValueArray[Parent_no] 	= nodeArrayValue.get(d);

                            notepad.setRuntimeValue(runTimeVar, nodeArrayValue.get(d));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // If node value from excel sheet is # print info in the Extent Report 

                    
                    if (nodeValueArray[Parent_no].equalsIgnoreCase("#")) {

                        TestExecutor.report.info("Node " + nodeArray[Parent_no] + "Node value of '"
                                + nodeArray[Parent_no] + "' is '" + nodeArrayValue.get(Parent_no) + "'");
                        continue;

                        // Else validate value of node from response and value in excel

                    } else if (!(nodeArrayValue.get(d).equalsIgnoreCase(nodeValueArray[Parent_no].trim()))) {

                        nod = false;

                        if (nodeValueArray[Parent_no].trim().contains("<") && nodeValueArray[Parent_no].trim().contains(">")) {

                            nod = true;

                            TestExecutor.report.info("Run time value for '" + runTimeVar + "' is '" + nodeArrayValue.get(d) + "'");

                        } else if(nodeValueArray[Parent_no].charAt(0)=='#'){

                            if(regexMatcher(nodeValueArray[Parent_no].substring(1,nodeValueArray[Parent_no].length()),nodeArrayValue.get(0))) {
                               nod = true;
                            }
                        }
						/*
						 * else { TestExecutor.report.fail("Run time value for '" + runTimeVar +
						 * "' is '" + nodeArrayValue.get(d) + "'"); }
						 */
                        
                        Allnod = Boolean.logicalAnd(Allnod, nod);
                    }
                }
                if (!(nodeValueArray[Parent_no].equalsIgnoreCase("#"))) {
                    if(nodeValueArray[Parent_no].charAt(0)=='#') {
                    	TestExecutor.report.verifyRegex(
                                "Node value of <b>'" + nodeArray[Parent_no] + "' </b> node is <b>'" + nodeValueArray[Parent_no] + "'</b>", nodeValueArray[Parent_no].substring(1,nodeValueArray[Parent_no].length()), nodeArrayValue.get(0));
                    }
                    else if(nodeValueArray[Parent_no].contains("<") && nodeValueArray[Parent_no].contains(">")) {
                    	TestExecutor.report.info("Node " + nodeArray[Parent_no] + " Node value of '"
                                + nodeArray[Parent_no] + "' is stored successfully");
                    }
                    else if(nodeArray[Parent_no].contains("xmlTagValue")) {
                    	TestExecutor.report.verify(nodeValueArray[Parent_no], nodeArrayValue.get(0));
                    }
                    else {
	                	TestExecutor.report.verify("Value of "+ parentPathArray[Parent_no] +"."+nodeArray[Parent_no] + " is ",nodeValueArray[Parent_no], nodeArrayValue.get(0));
                    }
                } 
            } else {
                TestExecutor.report.info("There is no node in the search criteria");
            }
        }

        return nodeArrayValue;
    }


    /**
	 * FUNCTION 		- Copy logo to a file Path and Hide logo
	 * @param filePath	- File Path where logo is to be copied in String format
	 */
    
    public void copyLogo(String filePath) {

        try {

            copyFile(new File("").getAbsolutePath() + File.separator + "logo.png", filePath);

            Path path = Paths.get(filePath);

            // Hide logo image file
            
            DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);

            Files.setAttribute(path, "dos:hidden", true);

        } catch (Exception e) {
            TestExecutor.log.LogMessage(Level.ERROR, "Exception occured while copying coforge logo with message - " + e.getMessage());
        }
    }

    public void copyClientLogo(String filePath) {

        try {

            copyFile(new File("").getAbsolutePath() + File.separator + "clientLogo.png", filePath);

            Path path = Paths.get(filePath);

            // Hide logo image file
            
            DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);

            Files.setAttribute(path, "dos:hidden", true);

        } catch (Exception e) {
            TestExecutor.log.LogMessage(Level.ERROR, "Exception occured while copying logo with message - " + e.getMessage());
        }
    }
    
        /**
	 * FUNCTION 			- Verify Response Code of a response message
	 * @param resp		 	- Response from which response code to be verified as a Response variable
	 * @param ResponseCode 	- Expected response code as an 'int' variable
	 * @return				- True or False based on the expected response code and actual response code from the response 
	 */
    
    public boolean responseCodeVerify(Response resp, int ResponseCode) throws ArithmeticException {

        boolean flag = false;

        try {

            TestExecutor.log.LogMessage(resp.asString());
            TestExecutor.log.LogMessage("Actual Response Code :" + resp.getStatusCode());
            TestExecutor.log.LogMessage("ExpectedResponse Code :" + ResponseCode);

            if (ResponseCode == resp.getStatusCode()) {
                flag = true;
            }

            TestExecutor.report.verify(ResponseCode, resp.getStatusCode());

            long resptime = resp.getTimeIn(TimeUnit.MILLISECONDS);

            TestExecutor.log.LogMessage("Time in response" + resptime);

            TestExecutor.report.info("Response Time (Milliseconds): "+ Long.toString(resptime));

            //Reports.info("Response : ", resp.asString());					// Prints response in the report

        } catch (Exception e) {

            TestExecutor.report.fail("Web Services not responding with exception - "+ e.getMessage());
            TestExecutor.log.LogMessage(Level.ERROR, e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    
	/**
	 * FUNCTION		- Get auto increased value from an alphanumeric value
	 * @param var 	- Variable name of notepad value logs in String format
	 * @param val 	- Increment/Decrement factor as an 'int' variable
	 * @return 		- Incremented/decremented value
	 */
    
    public static String getAutoIncreament(String var, int val) {

        String incVar = "";

        try {

            int i;

            for (i = var.length() - 1; i >= 0; i--) {

                Character ch = var.charAt(i);

                if (!Character.isDigit(ch)) {
                    break;
                }

            }

            incVar = var.substring(0, i + 1);
            String test = var.substring(i + 1, var.length());

            System.out.println(" test - " + test);
            System.out.println(" incvar - " + incVar);

            incVar = incVar + String.valueOf(Integer.parseInt(var.substring(i + 1, var.length())) + val);

        } catch (Exception e) {
            TestExecutor.log.LogMessage(Level.ERROR, e.getMessage());
            e.printStackTrace();
        }

        return incVar;
    }
    
    /**
     * FUNCTION		- Regular Expression Matcher: Takes two string variables as input.
     * @param regex - Regular Expression as expected value in String format 
     * @param exp	- Expression to be matched with the regular expression in String format
     * @return		- True, if 'exp' satisfies the 'regex', else false
     */
    
    public boolean regexMatcher(String regex,String exp) {
        
        try {
            
            Pattern p = Pattern.compile(regex);               
            Matcher m = p.matcher(exp);  
            return m.matches();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void copyFile(String srcFilePath, String destFilePath) {
		File source = new File(srcFilePath);
		File dest = new File(destFilePath);
		try {
		    FileUtils.copyFile(source, dest);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
