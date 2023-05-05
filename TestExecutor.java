package rest.executors;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.poi.ss.usermodel.Sheet;

import com.networknt.schema.ValidationMessage;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import rest.helpers.CSVReader;
import rest.helpers.CoforgeFormattedException;
import rest.helpers.Excel;
import rest.helpers.FunctionClass;
import rest.helpers.HoverflyVirtualization;
import rest.helpers.Log;
import rest.helpers.PropertyFileReader;
import rest.helpers.Reports;
import rest.helpers.RestFunctions;
import rest.helpers.RuntimeTestData;
import rest.helpers.SchemaValidator;
import rest.helpers.Text;
import rest.helpers.XML;

public class TestExecutor {

	public static int responseCount = 0;
	public static String testDataFilePath = "", requestsFilePath = "",
			responsesFilePath = "", Node_Value = "", runTimeValues[] = null, currentModuleName = "",
			reportFilePath = "", RepName = "";
	
	public static Response resp = null;
	public static Reports report = null;
	public static Log log = null;
	public static Text text = null;
	public static ArrayList<String> nodeArrayValue = null;
	public static FunctionClass fb = null;
	public static PropertyFileReader property = null;
	public static RuntimeTestData runTimeTestData = null;
	public static RestFunctions restFunctions = null;
	public static XML configXml = null;
	public static HashMap<String, String> dependency = null;
	public static HashMap<String,String>[] testData;
	public static File schemaFile;
	public static Boolean currentTestDependency;
	public static void main(String[] args) throws Exception {

		HoverflyVirtualization virtualization = new HoverflyVirtualization();
		try {

			// Get Report file path

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();

			// 2016/11/16 12:08:43 -> 2016_11_16 12_08_43

			RepName = ((dateFormat.format(date)).replace(":", "_")).replace("/", "_"); 
			String sysPath = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "Reports_"
					+ RepName;
			reportFilePath = sysPath + File.separator;
			String stmp = RepName.replace(" ", "-");

			// Initialize variables

			log = new Log(reportFilePath + "ExecutionDetails.log");

			nodeArrayValue = new ArrayList<String>();
			dependency = new HashMap<>();
			fb = new FunctionClass();
			property = new PropertyFileReader();
			runTimeTestData = new RuntimeTestData();
			restFunctions = new RestFunctions();

			configXml = new XML(new File("").getAbsolutePath() + File.separator + "config.xml");
			report = new Reports();
			if (property.getProperty("TestDataFileType").equalsIgnoreCase("csv")) {
				currentModuleName = configXml.readTagVal("ModuleName");
				testDataFilePath = new File("").getAbsolutePath() + File.separator + "TestData" + File.separator
						+ currentModuleName + ".csv";
			} else {
				testDataFilePath = new File("").getAbsolutePath() + File.separator + "TestData" + File.separator
						+ "APITestCases" + ".xlsx";
			}

			// Set proxy if required

			if (property.getProperty("IsSystemProxySetUpRequired").equalsIgnoreCase("true")) {
				System.setProperty("http.proxyHost", property.getProperty("SystemProxyHost"));
				System.setProperty("http.proxyPort", property.getProperty("SystemProxyPort"));
				System.setProperty("https.proxyHost", property.getProperty("SystemProxyHost"));
				System.setProperty("https.proxyPort", property.getProperty("SystemProxyPort"));
			}

			// Read Test Case Count in module sheet

			if (property.getProperty("TestDataFileType").equalsIgnoreCase("csv")) {
				testData = new CSVReader(testDataFilePath).GetCsvData();
			} else {
				testData = new Excel().getHashMapData(testDataFilePath);
			}

			// Get Test data for current module

			System.out.println("Test Data - " + testData);
			System.out.println("total Tests - " + String.valueOf(testData.length));
			for(int testno=0;testno<testData.length;testno++) {
				if(testData[testno]==null) {
					log.LogMessage(Level.ERROR, "testData[testno] is null - " + String.valueOf(testno));
					break;
				}
				nodeArrayValue.clear();
				runTimeValues = new String[testData.length];
				if (property.getProperty("TestDataFileType").equalsIgnoreCase("excel")) {
					currentModuleName=testData[testno].get("Module Name");
				}
				String currentTestNo = testData[testno].get("Test Case No");

				log.LogMessage("Executing Test - '" + currentTestNo + "' for module - '" + currentModuleName
						+ "'");
				String currentTestName = testData[testno].get("Test Case Name");
				String methodName = testData[testno].get("Method");
				String currentDependencyVal= testData[testno].get("Dependency");

				if (methodName.equalsIgnoreCase("GET") || methodName.equalsIgnoreCase("PUT"))
					report.startTest("<font color=#46C7C7>" + methodName
							+ "</font><font color=#8467D7 style='margin-left:10ch;'>"
							+ currentModuleName + " - " + currentTestNo + "</font>"
							+ "<font color=#726E6D> <small> <br>" + currentTestName
							+ "</br></small></font>");
				else if (methodName.equalsIgnoreCase("POST"))
					report.startTest("<font color=#46C7C7>" + methodName
							+ "</font><font color=#8467D7 style='margin-left:9ch;'>" + currentModuleName
							+ " - " + currentTestNo + "</font>" + "<font color=#726E6D> <small> <br>"
							+ currentTestName + "</br></small></font>");
				else
					report.startTest(""
							+ "<font color=#46C7C7>" + methodName
							+ "</font><font color=#8467D7 style='margin-left:7ch;'>" + currentModuleName
							+ " - " + currentTestNo + "</font>" + "<font color=#726E6D> <small> <br>"
							+ currentTestName + "</br></small></font>");


				if (currentDependencyVal != null && !currentDependencyVal.trim().equals("")) {
					log.LogMessage("Test Case dependecy found " + currentDependencyVal);
					System.out.println("Test Dependecies" + dependency);
					String[] currentDependencies = currentDependencyVal.split(",");
					int cd;
					for (cd=0;cd < currentDependencies.length; cd++) {
						if (dependency.get(currentDependencies[cd]) != null && dependency.get(currentDependencies[cd]).equalsIgnoreCase("Fail")) {
							dependency.put(currentTestNo, "Fail");
							report.skip("Dependency Failed '" + currentDependencies[cd] + "' Failed. Skipping test '" + currentTestNo +"'");
							break;
						}
					}
					if (cd != currentDependencies.length) {
						log.LogMessage("Test Case dependecies are passed " + currentDependencyVal);
						continue;
					}
				}

				String currentTestBaseUri = testData[testno].get("Base URI");

				//				HoverflyVirtualization virtualization = new HoverflyVirtualization();
				if(property.getProperty("IsServiceVertualizationModeRequired").equalsIgnoreCase("true")) {
					currentTestBaseUri = "http://localhost:8085";
					virtualization.closeSimulation();
					virtualization.StartSimulation(currentModuleName+currentTestNo);
				}

				String currentTestBasePath = testData[testno].get("Base Path");
				String currentTestRequest = testData[testno].get("Request File Name");
				String currentTestResponse = testData[testno].get("Response File Name");
				String currenttTestBody = testData[testno].get("Body");
				String currentTestMethod = testData[testno].get("Method");
				String currentTestParamNames = testData[testno].get("Request Param Names");
				String currentTestParamValues = testData[testno].get("Request Param Values");
				String currentTestParentPath = testData[testno].get("Parent Path");
				String currentTestNodeNames = testData[testno].get("Node Names");
				String currentTestNodeValues = testData[testno].get("Node Values");
				String currentTestResponseCode = testData[testno].get("Response Code");
				String currentGetHeaderVal = testData[testno].get("Get Header"); 
				String currentHeaders = testData[testno].get("Headers");
				String currentHeaderValues = testData[testno].get("Header Values");

				// Get Run Time Values if present for below

				log.LogMessage(" Before Scanning runtime values");
				log.LogMessage(" Base URI - " + currentTestBaseUri);
				log.LogMessage(" Current Test Node Names - " + currentTestNodeNames);
				log.LogMessage(" Current Test Node Values - " + currentTestNodeValues);
				log.LogMessage(" Base Path - " + currentTestBasePath);
				log.LogMessage(" Param Names - " + currentTestParamNames);
				log.LogMessage(" Param Values - " + currentTestParamValues);
				log.LogMessage(" Current Test Request - " + currentTestRequest);
				log.LogMessage(" currentHeaderValues - " + currentHeaderValues);

				currentTestBasePath = runTimeTestData.GetRunTimeVariableNames(currentTestBasePath);
				currentTestNodeNames = runTimeTestData.GetRunTimeVariableNames(currentTestNodeNames);
				currentTestNodeValues = runTimeTestData.GetRunTimeVariableNames(currentTestNodeValues);
				currenttTestBody = runTimeTestData.GetRunTimeVariableNames(currenttTestBody);
				currentTestParamNames = runTimeTestData.GetRunTimeVariableNames(currentTestParamNames);
				currentTestParamValues = runTimeTestData.GetRunTimeVariableNames(currentTestParamValues);
				currentHeaderValues = runTimeTestData.GetRunTimeVariableNames(currentHeaderValues);


				log.LogMessage(" After Scanning runtime values");
				log.LogMessage(" Base URI - " + currentTestBaseUri);
				log.LogMessage(" Current Test Node Names - " + currentTestNodeNames);
				log.LogMessage(" Current Test Node Values - " + currentTestNodeValues);
				log.LogMessage(" Base Path - " + currentTestBasePath);
				log.LogMessage(" Param Names - " + currentTestParamNames);
				log.LogMessage(" Param Values - " + currentTestParamValues);
				log.LogMessage(" currentHeaderValues - " + currentHeaderValues);


				RequestSpecification req = null;

				if (!currentTestBaseUri.equalsIgnoreCase("")){
					RestAssured.baseURI = currentTestBaseUri;
				}else {
					RestAssured.baseURI = configXml.readTagVal("BASEURI");//"https://demoqa.com";
				}


				RestAssured.basePath = currentTestBasePath;
				if (currentHeaderValues != null) {
					req = fb.setHeader(currentHeaders, currentHeaderValues);
				}

				if (!currentTestNodeNames.equals("") && currentTestNodeNames != null) {
					currentTestNodeNames = currentTestNodeNames + "#";
				}

				switch (currentTestMethod) {

				case "GET":

					// Log Base URI and Base Path in Report

					report.info("URL - " + 
							"<font color=#990099><u><b>" + RestAssured.baseURI + RestAssured.basePath);

					// Log Parameters in report

					if (!(currentTestParamNames.equals("") || currentTestParamNames == null)) {

						String[] parameterNameArray = currentTestParamNames.split(",");
						String[] parameterValueArray = currentTestParamValues.split(",");
						String parmlist = "";

						for (int i = 0; i < parameterNameArray.length; i++) {

							if (parameterValueArray[i].startsWith("xmlAttributeValue")) {
								parameterValueArray[i] = parameterValueArray[i]
										.replace("xmlAttributeValue(", "");
								parameterValueArray[i] = parameterValueArray[i].replace(")", "");
								String[] temp = parameterValueArray[i].split(";");
								try {
									parameterValueArray[i] = new FunctionClass().getAttributeValue(temp[0],
											temp[1], temp[2], temp[3]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							if (parameterNameArray[i].trim().length() > 0) {
								parmlist = parmlist + parameterNameArray[i] + " = " + parameterValueArray[i]
										+ "<br>";
							}
						}

						report.info("Parameters - "+ "<font color=#990099> <b>" + parmlist);
					}

					// Get response

					if (currentTestParamNames != null && !currentTestParamNames.equals("")
							&& currentTestParamValues != null && !currentTestParamValues.equals("")) {
						// restFunctions.getQueryResponse(currentTestParamNames,currentTestParamValues);
						resp = restFunctions.getQueryResponse(currentTestParamNames, currentTestParamValues,
								req);
					} else {
						resp = restFunctions.getQueryResponse(req,RestAssured.baseURI+RestAssured.basePath);
					}

					log.LogMessage("\n * * * * * * * * * Response fetched for Test - '" + currentTestName
							+ "' * * * * * * * * * ");
					log.LogMessage("Response - " + resp.body().asString());
					log.LogMessage(
							"\n * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * \n");

					// Save Response in file and log step of file link in Report

					String currentResponseFilePath = Reports.responseFilePath
							+ currentModuleName.replace(" ", "-") + "-" + currentTestName.replace(" ", "-")
							+ "-" + String.valueOf(responseCount++) + ".json";

					restFunctions.saveResponseToFile(resp, currentResponseFilePath);

					// Verify Response code
					
					if (!(fb.responseCodeVerify(resp, Integer.parseInt(currentTestResponseCode)))) {
						dependency.put(currentTestNo, "Fail");
						continue;
					}

					// If node value is not "" Validate nodes
					Boolean verifyReponseFromFile = false;
					if (currentTestResponse!=null && !currentTestResponse.equalsIgnoreCase("")) {
						String expectedResponseFilepath = new File("").getAbsolutePath() +File.separator + "TestData"
								+ File.separator + "Requests" + File.separator +currentTestResponse+".json";
						String expectedResponseFileData = FileUtils.readFileToString(new File(expectedResponseFilepath));
						//report.verify("Response verification from expected response file", expectedResponseFileData, resp.getBody().asString());
						verifyReponseFromFile = expectedResponseFileData.equalsIgnoreCase(resp.body().asString());
					} else {
						try {
							if(currentTestParentPath.equalsIgnoreCase("") || currentTestParentPath.isEmpty() || currentTestParentPath==null) {
								log.LogMessage("No parent path found for validation");
							} else {

								nodeArrayValue = fb.validateNodeValue(resp, currentTestParentPath,
										currentTestNodeNames, currentTestNodeValues, nodeArrayValue);	
							}
						} catch (NullPointerException npe) {
							dependency.put(currentTestNo, "Fail");
							TestExecutor.report.fail("Response Validation - "+
									"Null Pointer Exception during node validation");
						}
					}

					// If nodes are verified display pass step in report else fail

					String currentResponseFileRelativePath = "" + currentResponseFilePath.substring(
							currentResponseFilePath.indexOf("Response"), currentResponseFilePath.length());

					if (fb.Allnod || verifyReponseFromFile) {
						dependency.put(currentTestNo, "Pass");
						report.ReportSuccessResponse(currentResponseFileRelativePath);
					} else {
						dependency.put(currentTestNo, "Fail");
						report.ReportFailedResponse(currentResponseFileRelativePath);
					}

					break;

				case "PUT":
					report.info("URL - " + 
							"<font color=#990099><u><b>" + RestAssured.baseURI + RestAssured.basePath);

					String putFileData = "";
					Boolean goForPutMethod = false;

					if (!currenttTestBody.equals("") && currenttTestBody != null) {
						putFileData = currenttTestBody;
						goForPutMethod = true;
					}

					if (currentTestRequest != null && !currentTestRequest.equals("") && !goForPutMethod) {

						try {

							File in = new File((new File("").getAbsolutePath() + File.separator + "TestData"
									+ File.separator + "Requests" + File.separator + currentTestRequest+".json"));
							putFileData = FileUtils.readFileToString(in);
							putFileData = runTimeTestData.GetRunTimeVariableNames(putFileData);
							log.LogMessage("Put File Data - " + putFileData);
							goForPutMethod = true;

						} catch (Exception E) {
							log.LogMessage(Level.ERROR, E.getMessage());
							E.printStackTrace();
						}
					}

					if (goForPutMethod) {

						resp = restFunctions.getPutResponse(putFileData, req, currentGetHeaderVal);

						report.pass("Request File - "+ putFileData);

						// Save response to file

						currentResponseFilePath = Reports.responseFilePath
								+ currentModuleName.replace(" ", "-") + "-"
								+ currentTestName.replace(" ", "-") + "-" + String.valueOf(responseCount++)
								+ ".json";

						restFunctions.saveResponseToFile(resp, currentResponseFilePath);

						currentResponseFileRelativePath = "" + currentResponseFilePath.substring(
								currentResponseFilePath.indexOf("Response"),
								currentResponseFilePath.length());

						report.pass(
								"Put Response Body"
										+ "<div align='right' style='float:right'><a target='_blank' href="
										+ currentResponseFileRelativePath + ">Response Json</a></div>");

						// Verify Response code

						Boolean testStatus = false;

						try {
							if (!(fb.responseCodeVerify(resp, Integer.parseInt(currentTestResponseCode)))) {
								dependency.put(currentTestNo, "Fail");
								testStatus = true;
							}
						} catch (Exception e) {
							log.LogMessage(Level.ERROR, e.getMessage());
							e.printStackTrace();
						}

						// If node value is not "" Validate nodes
						try {
							if (currentTestNodeValues != "") {
								// System.out.println("Current test node value is: "+currentTestNodeValues);
								nodeArrayValue = fb.validateNodeValue(resp, currentTestParentPath,
										currentTestNodeNames, currentTestNodeValues, nodeArrayValue);
							}
						} catch (Exception e) {
							System.out.println("Validate Node Exception");
						}

						String relativeRequestBodyFilePath = "";

						if (currentTestRequest != null && !currentTestRequest.equals("")) {

							fb.copyFile(new File("").getAbsolutePath() + File.separator + "TestData"
									+ File.separator + "Requests" + File.separator + currentTestRequest,
									report.responseFilePath + currentModuleName.replace(" ", "-") + "-"
											+ currentTestName.replace(" ", "-") + ".json");
							relativeRequestBodyFilePath = "Responses" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".json";

						} else {

							text = new Text(report.responseFilePath + currentModuleName.replace(" ", "-")
							+ "-" + currentTestName.replace(" ", "-") + ".txt");
							text.setText(putFileData);
							relativeRequestBodyFilePath = "Responses" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".txt";
						}

						if (!testStatus) {
							dependency.put(currentTestNo, "Pass");
							report.pass(
									"Request body for PUT" + "<div align='right' style='float:right'><a "
											+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
											+ relativeRequestBodyFilePath + ">Request Json</a></div>");
						} else {
							report.fail(
									"Request body for PUT" + "<div align='right' style='float:right'><a "
											+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
											+ relativeRequestBodyFilePath + ">Request Json</a></div>");
						}
					} else {
						report.fail("PUT EXECUTION FAILED - "+"PUT BODY OR PUT REQUEST FILE MISSING");
					}

					break;

				case "POST":
					report.info("URL - "+
							"<font color=#990099><u><b>" + RestAssured.baseURI + RestAssured.basePath);

					String postFileData = "";
					Boolean goForPostMethod = false;

					if (!currenttTestBody.equals("") && currenttTestBody != null) {
						postFileData = currenttTestBody;
						goForPostMethod = true;
					}

					if (currentTestRequest != null && !currentTestRequest.equals("") && !goForPostMethod) {

						try {

							String requestFilePath = new File("").getAbsolutePath() +"\\TestData\\Requests\\" + currentTestRequest + ".json";

							File in = new File(requestFilePath);
							postFileData = FileUtils.readFileToString(in);

							postFileData = runTimeTestData.GetRunTimeVariableNames(postFileData);
							goForPostMethod = true;
							log.LogMessage("postFileData - " + postFileData);

						} catch (Exception E) {
							log.LogMessage(Level.ERROR, E.getMessage());
							E.printStackTrace();

						}
					}
					goForPostMethod = true; // comment this line if post does not need to be executed without body
					if (goForPostMethod) {

						resp = restFunctions.getPostResponse(postFileData, req, currentGetHeaderVal);


						if(postFileData==null||postFileData.equalsIgnoreCase("")||postFileData.isEmpty()) {
							report.pass("Request Send"); } else { report.pass("Request Body - "+
									postFileData); }



						// Save response to file

						currentResponseFilePath = Reports.responseFilePath
								+ currentModuleName.replace(" ", "-") + "-"
								+ currentTestName.replace(" ", "-") + "-" + String.valueOf(responseCount++)
								+ ".json";

						restFunctions.saveResponseToFile(resp, currentResponseFilePath);

						currentResponseFileRelativePath = "" + currentResponseFilePath.substring(
								currentResponseFilePath.indexOf("Response"),
								currentResponseFilePath.length());

						report.pass(
								"Post Response Body"
										+ "<div align='right' style='float:right'><a target='_blank' href="
										+ currentResponseFileRelativePath + ">Response Json</a></div>");

						// Verify Response code

						Boolean testStatus = false;

						try {
							if (!(fb.responseCodeVerify(resp, Integer.parseInt(currentTestResponseCode)))) {
								dependency.put(currentTestNo, "Fail");
								testStatus = true;
							}
						} catch (Exception e) {
							log.LogMessage(Level.ERROR, e.getMessage());
							e.printStackTrace();
						}

						// If node value is not "" Validate nodes
						try {
							if (currentTestNodeValues != "") {
								// System.out.println("Current test node value is: "+currentTestNodeValues);
								nodeArrayValue = fb.validateNodeValue(resp, currentTestParentPath,
										currentTestNodeNames, currentTestNodeValues, nodeArrayValue);
							}
						} catch (Exception e) {
							System.out.println("Validate Node Exception");
						}

						String relativeRequestBodyFilePath = "";

						if (currentTestRequest != null && !currentTestRequest.equals("")) {

							fb.copyFile(new File("").getAbsolutePath() + File.separator + "TestData"
									+ File.separator + "Requests" + File.separator + currentTestRequest + ".json",
									report.responseFilePath + currentModuleName.replace(" ", "-") + "-"
											+ currentTestName.replace(" ", "-") + ".json");
							relativeRequestBodyFilePath = "Responses" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".json";

						} else {

							text = new Text(report.responseFilePath + currentModuleName.replace(" ", "-")
							+ "-" + currentTestName.replace(" ", "-") + ".txt");
							text.setText(postFileData);
							relativeRequestBodyFilePath = "Responses" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".txt";
						}

						if (!testStatus) {
							dependency.put(currentTestNo, "Pass");
							if(postFileData==null||postFileData.equalsIgnoreCase("")||postFileData.isEmpty()) {
								//report.pass("Request Body is Empty");
							} else {
								report.pass(
										"Request body for POST" + "<div align='right' style='float:right'><a "
												+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
												+ relativeRequestBodyFilePath + ">Request Json</a></div>");
							}

						} else {
							if(postFileData==null||postFileData.equalsIgnoreCase("")||postFileData.isEmpty()) {
								report.warn("Request Body is Empty");
							} else {
								report.fail(
										"Request body for POST" + "<div align='right' style='float:right'><a "
												+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
												+ relativeRequestBodyFilePath + ">Request Json</a></div>");
							}

						}
					} else {
						report.fail("POST EXECUTION FAILED - "+"POST BODY OR POST REQUEST FILE MISSING");
					}

					break;

				case "DELETE":
					report.info("URL - "+
							"<font color=#990099><u><b>" + RestAssured.baseURI + RestAssured.basePath);

					String deleteFileData = "";
					boolean goFordeleteMethod = false;
					if (!currenttTestBody.equals("") && currenttTestBody != null) {
						deleteFileData = currenttTestBody;
						goFordeleteMethod = true;
					}
					if (currentTestRequest != null && !currentTestRequest.equals("")
							&& !goFordeleteMethod) {
						try {
							File in = new File((new File("").getAbsolutePath() + File.separator + "TestData"
									+ File.separator + "Requests" + File.separator + currentTestRequest+".json"));
							deleteFileData = FileUtils.readFileToString(in);
							deleteFileData = runTimeTestData.GetRunTimeVariableNames(deleteFileData);
							log.LogMessage("DeleteFileData - " + deleteFileData);
							goFordeleteMethod = true;
						} catch (Exception E) {
							log.LogMessage(Level.ERROR, E.getMessage());
							E.printStackTrace();
						}
					}
					if (goFordeleteMethod) {

						if (!(currentTestParamNames.equals("") || currentTestParamNames == null)) {

							String[] parameterNameArray = currentTestParamNames.split(",");
							String[] parameterValueArray = currentTestParamValues.split(",");
							String parmlist = "";

							for (int i = 0; i < parameterNameArray.length; i++) {
								if (parameterNameArray[i].trim().length() > 0) {
									parmlist = parmlist + parameterNameArray[i] + " = "
											+ parameterValueArray[i] + "<br>";
								}
							}

							report.info("Parameters - "+ "<font color=#990099> <b>" + parmlist);
						}

						if (currentTestParamNames != null && !currentTestParamNames.equals("")
								&& currentTestParamValues != null && !currentTestParamValues.equals("")) {

							resp = restFunctions.getDeleteResponse(currentTestParamNames,
									currentTestParamValues, deleteFileData, req);

						} else {
							resp = restFunctions.getDeleteResponse(deleteFileData, req);
						}
						if (deleteFileData != null && deleteFileData.length() > 0) {
							report.pass("Delete Body - "+ deleteFileData);
						}

						// Save response to file
						currentResponseFilePath = Reports.responseFilePath
								+ currentModuleName.replace(" ", "-") + "-"
								+ currentTestName.replace(" ", "-") + "-" + String.valueOf(responseCount++)
								+ ".json";
						restFunctions.saveResponseToFile(resp, currentResponseFilePath);
						currentResponseFileRelativePath = "" + currentResponseFilePath.substring(
								currentResponseFilePath.indexOf("Response"),
								currentResponseFilePath.length());
						report.pass(
								"Delete Response Body"
										+ "<div align='right' style='float:right'><a target='_blank' href="
										+ currentResponseFileRelativePath + ">Response Json</a></div>");
						// Verify Response code
						boolean testStatus = false;
						try {
							if (!(fb.responseCodeVerify(resp, Integer.parseInt(currentTestResponseCode)))) {
								dependency.put(currentTestNo, "Fail");
								testStatus = true;
							}
						} catch (Exception e) {
							log.LogMessage(Level.ERROR, e.getMessage());
							e.printStackTrace();
						}
						// If node value is not "" Validate nodes
						try {
							if (currentTestNodeValues != "") {
								nodeArrayValue = fb.validateNodeValue(resp, currentTestParentPath,
										currentTestNodeNames, currentTestNodeValues, nodeArrayValue);
							}
						} catch (Exception e) {
							System.out.println("Exception in node validation");
						}
						String relativeRequestBodyFilePath = "";
						if (currentTestRequest != null && !currentTestRequest.equals("")) {
							fb.copyFile(new File("").getAbsolutePath() + File.separator + "TestData"
									+ File.separator + "Requests" + File.separator + currentTestRequest+".json",
									report.responseFilePath + currentModuleName.replace(" ", "-") + "-"
											+ currentTestName.replace(" ", "-") + ".json");
							relativeRequestBodyFilePath = "Responses" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".json";
						} else {
							text = new Text(report.responseFilePath + currentModuleName.replace(" ", "-")
							+ "-" + currentTestName.replace(" ", "-") + ".txt");
							text.setText(deleteFileData);
							relativeRequestBodyFilePath = "Request" + File.separator
									+ currentModuleName.replace(" ", "-") + "-"
									+ currentTestName.replace(" ", "-") + ".txt";
						}

						if (currenttTestBody != null && !currenttTestBody.equals("")) {
							if (!testStatus) {
								dependency.put(currentTestNo, "Pass");

								report.pass(
										"Request body for DELETE"
												+ "<div align='right' style='float:right'><a "
												+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
												+ relativeRequestBodyFilePath + ">Request Json</a></div>");
							} else {
								report.fail(
										"Request body for DELETE"
												+ "<div align='right' style='float:right'><a "
												+ report.NewWindowPopUpHTMLCode() + " target='_blank' href="
												+ relativeRequestBodyFilePath + ">Request Json</a></div>");
							}
						}
					}
					else {
						report.fail("DELETE EXECUTION FAILED - "+
								"DELETE BODY OR DELETE REQUEST FILE MISSING");
					}
					break;

				default:
					String message = "Method '" + currentTestMethod
					+ "' is not allowed only GET, PUT, POST & DELETE are allowed";
					log.LogMessage(message);
					report.fail("Invalid Method Name - "+ message);
				}

				// validate schema if required
				String schemaFileName = testData[testno].get("Schema File Name");
				if(schemaFileName==null || schemaFileName.equalsIgnoreCase("") || schemaFileName.isEmpty()) {
					log.LogMessage("Schema validation not required in test");
				} else {
					String schemaFilePath = new File("").getAbsolutePath() + "\\TestData\\schemaFiles\\" + schemaFileName + ".json";
					String realativeRequestFilePath = report.responseFilePath + "//Schema"+currentTestNo+".json";
					String relativeRequestFilePath = "Responses//Schema"+currentTestNo+".json";
					try {

						schemaFile = new File(schemaFilePath);
						FileUtils.copyFile(schemaFile, new File(realativeRequestFilePath));
						report.info("Schema verified from Test data..." + "<div align='right' style='float:right'><a target='_blank' href="
								+ relativeRequestFilePath + ">Schema File</a></div>");

					} catch (Exception e) {
						e.printStackTrace();
						report.fail("unable to read schema file data - " + e.getLocalizedMessage());
					} 
					try {

						Set<ValidationMessage> errors = new SchemaValidator().validateSchemafromSchemaFile(resp, schemaFile); 
						// print validation errors
						if (errors.isEmpty()) {
							System.out.println("no validation errors :-)");
							report.pass("<b><font color='Green'>" + " No validation errors from Schema File" + "</font></b>");
						} else {
							errors.forEach(vm -> System.out.println(vm.getMessage()));
							errors.forEach(vm -> report.fail("<b><font color='Red'>" + vm.getMessage() + "</font></b>"));
						}
					} catch(Exception e) {
						report.fail("<b><font color='Red'>" + "Exception while schema match - " + e.getMessage() + "</font></b>");
					}
				}

				if(property.getProperty("IsServiceVertualizationModeRequired").equalsIgnoreCase("true")) {
					virtualization.closeSimulation();
				}
			}
		} catch(Exception e) {
			if(property.getProperty("IsServiceVertualizationModeRequired").equalsIgnoreCase("true")) {
				virtualization.closeSimulation();
			}
			
			e.printStackTrace();
		}
		if(Reports.report.getStats().getEventsCountFail()>0 || Reports.report.getStats().getChildCountFail()>0 || Reports.report.getStats().getParentCountFail()>0) {
			throw new CoforgeFormattedException();
		}
		
	}
	
}