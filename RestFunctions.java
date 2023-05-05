package rest.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.util.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestFunctions {
	
	DateFormat dateFormat	= new SimpleDateFormat("yyyy-MM-dd");
		
	public RestFunctions() {

	}

	// Global Setup Variables

	public String path; // Rest request path

	/**
	 * FUNCTION - Sets Base URI: Before starting the test, we should set the RestAssured.baseURI
	 * @param baseURI - Base URI to be set in String format
	 */
	
	public void setBaseURI(String baseURI) {
		System.out.println("Base URI - " + baseURI);
		RestAssured.baseURI = null;
		RestAssured.baseURI = baseURI;
	}

	/**
	 * FUNCTION - Sets base path: Before starting the test, we should set the
	 * RestAssured.basePath
	 * 
	 * @param basePathTerm - Base Path to be used in String format
	 */
	
	public void setBasePath(String basePathTerm) {
		RestAssured.basePath = basePathTerm;
	}

	/**
	 * Reset Base URI (after test): After the test, we should reset the RestAssured.baseURI
	 */
	
	public void resetBaseURI() {
		RestAssured.baseURI = null;
	}

	/**
	 * Reset base path (after test): After the test, we should reset the RestAssured.basePath
	 */
	
	public void resetBasePath() {
		RestAssured.basePath = null;
	}

	/**
	 * FUNCTION - Sets ContentType: We should set content type as JSON or XML before starting the test
	 * @param Type - Content type to be used in String format
	 */

	public void setContentType(String Type) {
		RestAssured.given().contentType(Type);
	}

	/**
	 * FUNCTION 	- Delete
	 * @param body	- body for delete request
	 * @return		- response
	 */
	
	public Response getDeleteResponse(String body,RequestSpecification req) {
		return RestAssured.given().spec(req).body(body).when().delete().then().extract().response();
	}
	
	/**
	 * FUNCTION 	- Delete
	 * @param body	- body for delete request
	 * @return		- response
	 */
	
	public Response getDeleteResponse(String param, String value, String body,RequestSpecification req) {
		String[] parameterNameArray 	= param.split(",");
		String[] parameterValueArray 	= value.split(",");
		
		

		Map<String, String> parameterNameValuePairs = new LinkedHashMap<>();

		for (int i = 0; i < parameterNameArray.length; i++) {
			
			if (parameterNameArray[i].trim().length() > 0) {
				parameterNameValuePairs.put(parameterNameArray[i], parameterValueArray[i]);
			}
			//System.out.println(parameterNameValuePairs);
		}
		return RestAssured.given().spec(req).params(parameterNameValuePairs).body(body).when().delete().then().extract().response();
	}

	
	
	/**
	 * FUNCTION - Returns response
	 * @return - We send "path" as a parameter to the Rest Assured'a "get" method and "get" method returns response of API
	 */
	
	public Response getResponse() {
		return RestAssured.given().when().get().then().extract().response();
	}

	/**
	 * FUNCTION - Returns response
	 * @return	- Response for the request sent
	 */
	
	public Response getQueryResponse() {
		return RestAssured.given().get();
	}

	/**
	 * FUNCTION 	- Returns response
	 * @param req 	- Request specification for the request 
	 * @return		- Response for the request sent
	 */
	
	public Response getQueryResponse(RequestSpecification req, String path) {
		//RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		return RestAssured.given().spec(req).get(URI.create(path));
	}


	/**
	 * FUNCTION 				- Returns response
	 * @param headers			- Headers to be set for the request
	 * @param ParameterName		- Parameter name to be set for the request
	 * @param ParameterValue	- Parameter value to be set for the parameter
	 * @return 					- Response for the request sent
	 */
	
	public Response getQueryResponse(HashMap<String, String> headers, String ParameterName, String ParameterValue) {

		if (ParameterName.trim().length() > 0)
			return RestAssured.given().headers(headers).queryParam(ParameterName, ParameterValue).when().get().then().extract().response();
		else
			return RestAssured.given().headers(headers).when().get().then().extract().response();

	}

	/**
	 * FUNCTION					- Returns response
	 * @param ParameterName2	- Parameter names as ',' separated values
	 * @param ParameterValue2	- Parameter values as ',' separated values
	 * @return					- Response for the request sent
	 */
	
	public Response getQueryResponse(String ParameterName2, String ParameterValue2, RequestSpecification req) {

		String[] parameterNameArray 	= ParameterName2.split(",");
		String[] parameterValueArray 	= ParameterValue2.split(",");
		
		Boolean hasQueryParam = false;

		Map<String, String> parameterNameValuePairs = new LinkedHashMap<>();

		for (int i = 0; i < parameterNameArray.length; i++) {
			
			if(parameterValueArray[i].startsWith("xmlAttributeValue")) {
				parameterValueArray[i] = parameterValueArray[i].replace("xmlAttributeValue(", "");
				parameterValueArray[i] = parameterValueArray[i].replace(")", "");
				String []temp = parameterValueArray[i].split(";");
				try {
					parameterValueArray[i] = new FunctionClass().getAttributeValue(temp[0], temp[1], temp[2],temp[3]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (parameterNameArray[i].trim().length() > 0) {
				if(parameterNameArray[i].contains("<PathParameter>")) {
					String basepath = RestAssured.basePath;
					String baseURI = RestAssured.baseURI;
					basepath=basepath+"/"+parameterValueArray[i];
					System.out.println("Path Parameter added basepath - " + basepath);
					RestAssured.baseURI=baseURI;
					RestAssured.basePath=basepath;
				}else {
					hasQueryParam = true;
					parameterNameValuePairs.put(parameterNameArray[i], parameterValueArray[i]);
				}
				
			}
			//System.out.println(parameterNameValuePairs);
		}

		if(!hasQueryParam) {
			System.out.println("No Query Parameter found - "+ RestAssured.basePath);
			return getQueryResponse(req,RestAssured.baseURI+RestAssured.basePath);
			//return RestAssured.given().spec(req).when().get().then().extract().response();
		} else {
			return RestAssured.given().relaxedHTTPSValidation().
					spec(req).params(parameterNameValuePairs).when().get().then().extract().response();
		}
		

	}

	/**
	 * FUNCTION 				- Returns response
	 * @param headers			- Headers to be set for the request
	 * @param ParameterName1	- Parameter name to be set for the request
	 * @param ParameterValue1	- Parameter name to be set for the request
	 * @param ParameterName2	- Parameter names as ',' separated values
	 * @param ParameterValue2	- Parameter values for parameter names as ',' separated values
	 * @return					- Response for the request sent
	 */
	
	public Response getQueryResponse(HashMap<String, String> headers, String ParameterName1, String ParameterValue1,
			String ParameterName2, String ParameterValue2) {

		String[] parameterNameArray		= ParameterName2.split(",");
		String[] parameterValueArray	= ParameterValue2.split(",");
		Map<String, String> parameterNameValuePairs = new LinkedHashMap<>();

		parameterNameValuePairs.put(ParameterName1, ParameterValue1);

		for (int i = 0; i < parameterNameArray.length; i++) {
			parameterNameValuePairs.put(parameterNameArray[i], parameterValueArray[i]);
		}

		return RestAssured.given().headers(headers).queryParams(parameterNameValuePairs).when().get().then().extract()
				.response();

	}

	/**
	 * FUNCTION 	- Get the node value when path and response is provided
	 * @param Resp	- Response from which node value is to be extracted
	 * @param Path	- Path of the node
	 * @return		- Node value
	 */
	
	public String getStringNodeValue(Response Resp, String Path) {
		return Resp.path(Path);
	}

	/**
	 * FUNCTION 	- Get the node value when path and response is provided
	 * @param Resp	- Response from which node is to be extracted
	 * @param Path	- Path of the node
	 * @return		- Node value
	 */
	
	public int getIntNodeValue(Response Resp, String Path) {
		return Resp.path(Path);
	}

	/**
	 * FUNCTION			- Get the response for post request
	 * @param datamap	- Post body as Map
	 * @return			- Response for the request sent
	 */
	
	public Response getPostResponse(Map<String, Object> datamap) {
		return RestAssured.given().relaxedHTTPSValidation()
				.body(datamap).when().post();
	}

	/**
	 * FUNCTION 	- Get the response for post request
	 * @param body	- Post body in String format
	 * @return		- Response for the request sent
	 */
	
	public Response getPostResponse(String body) {

		return RestAssured.given().contentType("application/json").relaxedHTTPSValidation().body(body).when().post().then().extract().response();
	}
	
	/**
	 * Function 					- Get the response from put request
	 * @param body					- Put body in String format
	 * @param req					- Request specifications for the request to be sent
	 * @param currentGetHeaderVal	- Header Value from excel sheet
	 * @return						- Response from the request sent
	 */
	
	public Response getPutResponse(String body, RequestSpecification req,String currentGetHeaderVal) {
		Response response = null;
		System.out.println("Body - " + body);
		if (body.contains("formdata(")) {
			
			FunctionClass fbTemp = new FunctionClass();
			req = fbTemp.setformdata(body,req);
			response = RestAssured.given().spec(req).when().post().then().extract().response();
			/*
			 * String jsession = response.getCookie("JSESSIONID"); try { new
			 * Notepad().setRuntimeValue("JSessionId", jsession); } catch (IOException e) {
			 * e.printStackTrace(); }
			 */
		}
		else {
			if(body.contains("now()")) {
				
				body = body.replace("now()",dateFormat.format(new Date()));
			}
			
			//System.out.println("Body is: "+body);
			response = RestAssured.given().spec(req).body(body).when().put().then().extract().response();
			
		}
		if(currentGetHeaderVal!=null && currentGetHeaderVal.length()>0) {
			String []responseHeaders = currentGetHeaderVal.split(",");
			for(int i=0;i<responseHeaders.length;i++) {
				String []temp = responseHeaders[i].split(":");
				try {
					switch(temp[0]) {
					case "Cookie":
							new Notepad().setRuntimeValue(temp[2], response.getCookie(temp[1]));
							break;
					
					case "SessionId":
						new Notepad().setRuntimeValue(temp[2], response.getSessionId());
						break;
					case "ContentType":
						new Notepad().setRuntimeValue(temp[2], response.getContentType());
					default:
						System.out.println("No such response header found");
						break;
					} 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return response;
	}
	
	
	

	/**
	 * FUNCTION 	- Get response for the post request
	 * @param body	- Body for the post request
	 * @param req	- Request specifications for the request to be sent
	 * @return		- Response for the request sent
	 */
	
	public Response getPostResponse(String body, RequestSpecification req,String currentGetHeaderVal) {
		Response response = null;
		System.out.println("Body - " + body);
		if(body==null || body.equalsIgnoreCase("") || body.isEmpty() || body.equals("") || body.equalsIgnoreCase("")) {
			response = RestAssured.given().relaxedHTTPSValidation().spec(req).when().post().then().extract().response();
		} else if (body.contains("formdata(")) {
			
			FunctionClass fbTemp = new FunctionClass();
			req = fbTemp.setformdata(body,req);
			response = RestAssured.given().spec(req).when().post().then().extract().response();
			/*
			 * String jsession = response.getCookie("JSESSIONID"); try { new
			 * Notepad().setRuntimeValue("JSessionId", jsession); } catch (IOException e) {
			 * e.printStackTrace(); }
			 */
		} else {
			if(body.contains("now()")) {
				
				body = body.replace("now()",dateFormat.format(new Date()));
			}
			
			//System.out.println("Body is: "+body);
			response = RestAssured.given().spec(req).body(body).when().post().then().extract().response();
		}
		if(currentGetHeaderVal!=null && currentGetHeaderVal.length()>0) {
			String []responseHeaders = currentGetHeaderVal.split(",");
			for(int i=0;i<responseHeaders.length;i++) {
				String []temp = responseHeaders[i].split(":");
				try {
					switch(temp[0]) {
					case "Cookie":
							new Notepad().setRuntimeValue(temp[2], response.getCookie(temp[1]));
							break;
					
					case "SessionId":
						new Notepad().setRuntimeValue(temp[2], response.getSessionId());
						break;
					case "ContentType":
						new Notepad().setRuntimeValue(temp[2], response.getContentType());
					default:
						System.out.println("No such response header found");
						break;
					} 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	/**
	 * FUNCTION 	- Returns JsonPath object
	 * @param res	- Response
	 * @return 		- Json path of the response
	 */
	
	public JsonPath getJsonPath(Response res) {

		String json = res.asString();

		return new JsonPath(json);
	}

	/**
	 * FUNCTION 				- Set the path parameter
	 * @param Criteria			- Criteria in String format
	 * @param Criteria_value	- Creteria value for the criteria in String format 
	 */
	
	
	public static void pathparamater(String Criteria, String Criteria_value) {
		RestAssured.given().pathParam(Criteria, Criteria_value);
	}
	
	/**
	 * FUNCTION 				- Set query parameter	 
	 * @param Criteria			- Criteria in String format
	 * @param Criteria_value	- Criteria value in String format
	 */
	
	public static void queryparamater(String Criteria, String Criteria_value) {
		RestAssured.given().queryParam(Criteria, Criteria_value);
	}

	/**
	 * FUNCTION		- Get value from a response
	 * @param resp	- Response object
	 * @param path	- Path of the value to be returned
	 * @return		- Value at the path
	 */
	
	public int getResponse_int(Response resp, String path) {
		return resp.path(path);
	}

	/**
	 * FUNCTION			- Save response in a file
	 * @param res		- Response object
	 * @param filePath	- Path of the file where response is to be stored
	 */

	public void saveResponseToFile(Response res, String filePath) {


	        try {


	            String responseBody = res.getBody().asString();
	            String jsonformattedData = beautifyJson(responseBody);
	            InputStream input = new ByteArrayInputStream(jsonformattedData.getBytes());
	            byte[] SWFByteArray = IOUtils.toByteArray(input);
	            FileOutputStream fos = new FileOutputStream(new File(filePath));


	            fos.write(SWFByteArray);
	            fos.flush();
	            fos.close();


	        } catch (Exception e) {
	            e.printStackTrace();
	        }


	    }

    public String beautifyJson(String JsonData) {
       
        try {
           
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(JsonData, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return JsonData;
    }








}