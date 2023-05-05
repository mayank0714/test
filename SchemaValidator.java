package rest.helpers;

import java.io.File;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import io.restassured.response.Response;

public class SchemaValidator {
	
	public Set<ValidationMessage> validateSchemafromSchemaFile(Response response, File schemaFile) {
		Set<ValidationMessage> errors = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4); 
			
		    JsonSchema jsonSchema = factory.getSchema(FileUtils.readFileToString(schemaFile)); 
		    JsonNode jsonNode = objectMapper.readTree(response.getBody().asString()); 
		    errors = jsonSchema.validate(jsonNode);		    
		} catch(Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

}
