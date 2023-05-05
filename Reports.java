package rest.helpers;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.filter.log.LogDetail;
import rest.executors.TestExecutor;

public class Reports {

	public static ExtentReports report;
	public static ExtentTest test; 
	public static String RepName				= "";
	public static String responseFilePath		= "";
	public static String runtimeValuefilePath	= "";
	public static ThreadLocal<ExtentTest> extentTest= new ThreadLocal<ExtentTest>();
	
	public Reports() {
		
		
		RepName									= TestExecutor.RepName;
		String sysPath 							= System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "Reports_"+ RepName;
		responseFilePath						= TestExecutor.reportFilePath + "Responses" + File.separator;
		runtimeValuefilePath					= sysPath + File.separator + "ValueLogs.txt";
		
		new File(sysPath).mkdir();
		new File(Reports.responseFilePath.substring(0, Reports.responseFilePath.length()-1)).mkdir();
		
		// Create .txt file to store runtime test data
		
		try {
			Boolean b = new File(Reports.runtimeValuefilePath).createNewFile();	
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	 	//report = new ExtentReports(sysPath + File.separator + "ApiTestAutomationReport.html");				// Initialize HTML file for Report	
		report = ExtentReporterNG.getReportObj(sysPath + File.separator + "ApiTestAutomationReport.html");
		
	 	//report.loadConfig(new File("Extent-config.xml"));													// Load Extent Configuration
 		
		TestExecutor.fb.copyClientLogo(sysPath + File.separator + "clientLogo.png");
		TestExecutor.fb.copyLogo(sysPath + File.separator + "logo.png");	
	 	
	}
	
	public void publish() {
		report.flush();
	}
	/**
	 * FUNCTION 		- Start test: Invoke the startTest method of Extent Reports
	 * @param TCName	- Test Case name which is to be added in the report in String format
	 */	
	
	public void startTest(String TCName) {
		
		//TCName = "<font color=#8467D7><b>" + TCName;
		
		test = report.createTest(TCName);
		extentTest.set(test);
		report.flush();
	}
	
	/**
	 * FUNCTION 		- Stop current test: Invoke the endTest method of Extent Reports
	 * @param TCName	- Test Case Name which needs to be end in the Report
	 */
	
	public void stopTest(String TCName) {
		
		//report.endTest(test);
	}
	
	/**
	 * FUNCTION				- Verify Regex & log step when expected & actual are in string format
	 * @param Description	- Description of the step to be displayed
	 * @param exp			- Regular Expression in String format
	 * @param act			- Actual value which is to be matched with Regular Expression in String format
	 */
	
	public void verifyRegex(String Description,String exp,String act){

        boolean bool 	= new FunctionClass().regexMatcher(exp, act);
        Description		= "<font color='#43C6DB'>" + Description;
		
		if (bool) {
			test.log(Status.PASS,"Expected : " + exp + "<br /> Actual : <b><font color='green'>" + act);
		} else {
			test.log(Status.FAIL,"Expected : " + exp + "<br /> Actual : <b><font color='red'>" + act);
		}
		report.flush();
	}
	
	/**
	 * FUNCTION				- Verify & log step when expected & actual are in string format
	 * @param Description	- Description of the step to be displayed
	 * @param exp			- Expected Value in String format
	 * @param act			- Actual Value to be compared with Expected value in String format
	 */
	
	public void verify(String exp,String act){
		
		if (exp.equalsIgnoreCase(act)) {
			test.log(Status.PASS,"Expected : " + exp + "<br /> Actual : <b><font color='green'>" + act);
		} else {
			test.log(Status.FAIL,"Expected : " + exp + "<br /> Actual : <b><font color='red'>" + act);
		}
		
		report.flush();
		
    }

	public void verify(String message,String exp,String act){
		
		if (exp.equalsIgnoreCase(act)) {
			test.log(Status.PASS,"Expected : "+message + exp + "<br /> Actual : <b><font color='green'>" + act);
		} else {
			test.log(Status.FAIL,"Expected : "+message + exp + "<br /> Actual : <b><font color='red'>" + act);
		}
		
		report.flush();
		
    }
	/**
	 * FUNCTION 			- Verify & log step when expected & actual are in string format
	 * @param Description	- Description of the step to be displayed
	 * @param exp			- Expected Value in 'int' format
	 * @param act			- Actual Value to be compared with Expected value in 'int' format
	 */
	
	public void verify(int exp,int act){
		
		if (exp == act) {
			test.log(Status.PASS,"Expected : " + String.valueOf(exp) + "<br /> Actual : <b><font color=\"green\">" + String.valueOf(act));
		} else {
			test.log(Status.FAIL,"<br /> Expected: <b>" + String.valueOf(exp) + "<br /> Actual : <b><font color=\"red\">" + String.valueOf(act));
		}
		
		report.flush();
		
	}

	/**
	 * FUNCTION 			- Verify & log step when expected & actual are in string format
	 * @param Description	- Description of the step to be displayed
	 * @param exp			- Expected Value in 'boolean' format
	 * @param act			- Actual Value to be compared with Expected value in 'boolean' format
	 */
	
	public void verify(String Description,boolean exp,boolean act){
	   
//		Description = "<font color='#43C6DB'>" + Description;
//				
//		if (exp == act) {
//			test.log(Status.PASS,"Expected : <b>" + String.valueOf(exp) + "</b><br/> Actual: <b><font color=\"green\">" + String.valueOf(act) +"</font></b>");
//		} else {
//		   test.log(Status.FAIL,"<br /> Expected: <b>" + String.valueOf(exp) + "</b><br/> Actual: <b><font color=\"red\">" + String.valueOf(act) +"</font></b>");
//		}
//		
		Description =  Description;
		
		if (exp == act) {
			test.log(Status.PASS,"Expected : " + String.valueOf(exp) + "Actual: " + String.valueOf(act) +"");
		} else {
		   test.log(Status.FAIL,"Expected: " + String.valueOf(exp) + "" + String.valueOf(act) +"");
		}
		
		report.flush();
		
	}


	/**
	 * FUNCTION 			- Log fail step in test case (font color for description is red) 
	 * @param StepName		- Step name to be displayed in the report in String format
	 * @param Description	- Description of the test step in String format
	 */
	
	public void fail(String Description) {
		test.log(Status.FAIL, "<b><font color='red'>" + Description);
		report.flush();
	}

	public void warn(String Description) {
		test.log(Status.WARNING, "<b><font color='orange'>" + Description);
		report.flush();
	}

	
	/**
	 * FUNCTION 			- Log info step in test case (font color for description is black)
	 * @param Description	- Description of the test step in String format
	 */
	
	public void info(String Description) {
		test.log(Status.INFO, Description);
		report.flush();
	}

	/**
	 * FUNCTION 			- Log pass step in test case (font color for description is Green)
	 * @param Description	- Description of the test step in String format
	 */
	
	public void pass(String Description) {
		
		test.log(Status.PASS, "<b><font color='Green'>" + Description +"</font></B>");
		
		report.flush();
	}
	public void skip(String Description) {
		test.log(Status.SKIP, "<b><font color='orange'>" + Description +"</font></B>");
		report.flush();
	}

	/**
	 * FUNCTION - Generate attribute tag to open mentioned file path in new pop up window 
	 * @return 	- HTML code for pop up window in String format
	 */
	
	public String NewWindowPopUpHTMLCode() {
		return "onclick = \"window.open(this.href,'newwindow', 'width=" + TestExecutor.property.getProperty("ReportPopUpWindowWidth") + ",height=" + TestExecutor.property.getProperty("ReportPopUpWindowHeight") + "');return false;\"";
	}

	/**
	 * FUNCTION 								- Report successful response message
	 * @param currentResponseFileRelativePath	- Path of response file in String format
	 */
	
	public void ReportSuccessResponse(String currentResponseFileRelativePath) {
		
		try {
			pass("Response is verified successfully" + "<div align='right' style='float:right'><a " + NewWindowPopUpHTMLCode() + " target='_blank' href=" + currentResponseFileRelativePath + ">Response Json</a></div>");
		} catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	

	/**
	 * FUNCTION 								- Report failed response message
	 * @param currentResponseFileRelativePath	- Path of response file in String format
	 */
	
	public void ReportFailedResponse(String currentResponseFileRelativePath) {
		
		try {
			fail("Response failed in verification" + "<div align='right' style='float:right'><a " + NewWindowPopUpHTMLCode() + " target='_blank' href=" + currentResponseFileRelativePath + ">Response Json</a></div>");
		} catch(Exception e) {
			e.printStackTrace();
		}
				
	}
}
	

