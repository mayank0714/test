package rest.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import rest.executors.TestExecutor;

public class Excel {

	public static File file;
	public static Workbook wb;
	public static Sheet sheet;

	public Excel() {
	}

	/** Return sheet based on sheet name
	 @param fileName  - Complete file path of Excel in String format
	 @param sheetName - Name of sheet present in filename excel */

	public Sheet getSheet(String fileName, String sheetName) throws IOException {

		file = new File(fileName);
		FileInputStream inputStream = new FileInputStream(file);
		wb = new XSSFWorkbook(inputStream);
		sheet = wb.getSheet(sheetName);

		return sheet;
	}

	/** Return sheet based on sheet no(index)
	 @param fileName - Folder path of Excel in String format(folder location where excel is present)
	 @param fileName - File name of excel
	 @param sheetNo  - Index of sheet */

	public Sheet getSheet(String filePath, String fileName, int sheetNo) throws IOException {

		File file = new File(filePath + "\\" + fileName);
		FileInputStream inputStream = new FileInputStream(file);
		wb = new XSSFWorkbook(inputStream);
		sheet = wb.getSheetAt(sheetNo);

		return sheet;

	}

	/** Get rows count of excel sheet 
	 @param sheet - Excel sheet */

	public int getRowCount(Sheet sheet) {
		return sheet.getLastRowNum();
	}

	/** Get total columns of specific row of specific sheet
	 @param sheet - Excel sheet
	 @param rowNo - Row number of excel sheet */

	public int getColCount(Sheet sheet, int rowNo) {
		return sheet.getRow(rowNo).getLastCellNum();
	}

	/** Get Cell data based on row no and column no
	 @param sheet - Excel sheet
	 @param rowNo - Row number of excel sheet
	 @param colNo - Column number of excel sheet */

	public String getCellData(Sheet sheet, int rowNo, int colNo) {

		String cellVal = "";

		try {

			Cell col = sheet.getRow(rowNo).getCell(colNo);
			int cell_Type = col.getCellType(); // Get cell type

			switch (cell_Type) {

			case 0: // When cell type is Date

				cellVal = new DataFormatter().formatCellValue(col);
				break;

			case 1: // When cell type is Text

				cellVal = col.getStringCellValue();
				break;

			case 2: // When cell type is Formula

				switch (col.getCachedFormulaResultType()) {

				case Cell.CELL_TYPE_NUMERIC:

					cellVal = new BigDecimal(col.getNumericCellValue()).toString();
					break;

				case Cell.CELL_TYPE_STRING:

					cellVal = col.getStringCellValue();
					break;

				default:

					TestExecutor.log.LogMessage(Level.ERROR,
							"Invalid cell type formula (only numberic and text cell format are allowed) : "
									+ col.getCachedFormulaResultType());
					break;
				}

				break;

			case 3: // When cell type is blank

				cellVal = "";
				break;

			case 4: // When cell type is boolean

				cellVal = Boolean.toString(col.getBooleanCellValue());
				break;

			case 5: // When cell type is error

				cellVal = Byte.toString(col.getErrorCellValue());
				break;

			default:
				TestExecutor.log.LogMessage(Level.ERROR, "Invalid cell type : " + cell_Type);
				break;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			TestExecutor.log.LogMessage(Level.ERROR,
					"rowNo - " + String.valueOf(rowNo) + ", colNo - " + String.valueOf(colNo));
		}

		return cellVal;
	}

	public String getCellData(Cell col) {

		String cellVal = "";

		try {

			int cell_Type = col.getCellType(); // Get cell type

			switch (cell_Type) {

			case 0: // When cell type is Date

				cellVal = new DataFormatter().formatCellValue(col);
				break;

			case 1: // When cell type is Text

				cellVal = col.getStringCellValue();
				break;

			case 2: // When cell type is Formula

				switch (col.getCachedFormulaResultType()) {

				case Cell.CELL_TYPE_NUMERIC:

					cellVal = new BigDecimal(col.getNumericCellValue()).toString();
					break;

				case Cell.CELL_TYPE_STRING:

					cellVal = col.getStringCellValue();
					break;

				default:

					TestExecutor.log.LogMessage(Level.ERROR,
							"Invalid cell type formula (only numberic and text cell format are allowed) : "
									+ col.getCachedFormulaResultType());
					break;
				}

				break;

			case 3: // When cell type is blank

				cellVal = "";
				break;

			case 4: // When cell type is boolean

				cellVal = Boolean.toString(col.getBooleanCellValue());
				break;

			case 5: // When cell type is error

				cellVal = Byte.toString(col.getErrorCellValue());
				break;

			default:
				TestExecutor.log.LogMessage(Level.ERROR, "Invalid cell type : " + cell_Type);
				break;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			TestExecutor.log.LogMessage(Level.ERROR,e.getLocalizedMessage());
		}

		return cellVal;
	}
	/** Get column value based on header
	 @param sheet   - Excel sheet
	 @param rowNo   - Row number of excel sheet
	 @param colName - Column name of first record(header) excel sheet */

	public String getCellData(Sheet sheet, int rowNo, String colName) {
		int colNo = getColHeaderNo(sheet, colName);
		return getCellData(sheet, rowNo, colNo);
	}

	/** Return the header column number of given column name
	 @param sheet   - Excel sheet
	 @param colName - Column name of first record(header) excel sheet */

	public int getColHeaderNo(Sheet sheet, String colName) {

		int colNo = -1;

		try {

			int totCol = getColCount(sheet, 0);
			totCol = 25;
			int currentColNo;

			for (currentColNo = 0; currentColNo < totCol; currentColNo++) {

				if (getCellData(sheet, 0, currentColNo).equalsIgnoreCase(colName)) {
					return currentColNo;
				} else if (currentColNo == totCol - 1) {
					TestExecutor.log.LogMessage(Level.ERROR, "Column header not found in excel file: " + colName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Unable to get column number of '" + colName + "' column in sheet '" + sheet.getSheetName() + "'");
		}

		return colNo;
	}

	/** Write value in any specific cell
	 @param sheetNo    - Index of sheet
	 @param rowNo      - Row number of excel sheet
	 @param colNo      - Column number of excel sheet
	 @param valToWrite - Value to be written in cell */

	public void setCellData(String filepathWithName, int sheetNo, int rowNo, int colNo, String valToWrite) {

		try {

			Workbook workbook;
			Sheet sheet;

			FileInputStream file = new FileInputStream(filepathWithName);

			if (filepathWithName.endsWith("xlsx")) {
				workbook = new XSSFWorkbook(file);
			} else {
				workbook = new HSSFWorkbook(file);
			}

			sheet = workbook.getSheetAt(0);
			Cell cell = null;
			cell = sheet.getRow(rowNo).getCell(colNo); // Update value of cell
			cell.setCellType(2);
			cell.setCellValue(valToWrite);

			file.close();
			FileOutputStream outFile = new FileOutputStream(new File(filepathWithName));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unexpected Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/** Write value in specific column
	 @param sheet      - Excel sheet
	 @param rowNo      - Row number of excel sheet
	 @param colName    - Column name of first record(header) excel sheet
	 @param valToWrite - Value to be written in cell */

	public void setCellData(Sheet sheet, int rowNo, String colName, String valToWrite) {

		int colNo = getColHeaderNo(sheet, colName);

		sheet.getRow(rowNo).getCell(colNo).setCellType(1);
		sheet.getRow(rowNo).getCell(colNo).setCellValue(valToWrite);

	}

	/** Write value in any specific cell
	 @param sheetNo    - Index of sheet
	 @param rowNo      - Row number of excel sheet
	 @param colNo      - Column number of excel sheet
	 @param valToWrite - Value to be written in cell */

	public void setCellData(String filepathWithName, String sheetName, int rowNo, String colName, String valToWrite) {

		try {

			Workbook workbook;
			Sheet sheet;

			FileInputStream file = new FileInputStream(filepathWithName);

			if (filepathWithName.endsWith("xlsx")) {
				workbook = new XSSFWorkbook(file);
			} else {
				workbook = new HSSFWorkbook(file);
			}

			sheet = workbook.getSheet(sheetName);
			int colNo = getColHeaderNo(sheet, colName);
			Cell cell = null;
			cell = sheet.getRow(rowNo).getCell(colNo); // Update value of cell

			cell.setCellType(2);
			cell.setCellValue(valToWrite);

			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(filepathWithName));

			workbook.write(outFile);

			outFile.close();

		} catch (Exception e) {
			TestExecutor.log.LogMessage(Level.ERROR, "Unexpected Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public HashMap<String,String>[] getHashMapData(String filepath) {
		HashMap<String,String>[] excelData;
		HashMap<String,String>[] finalData;
		int testCount = 0;
		try {

			// Read Excel Sheet
			String executionSheetName = Constants.ExcelExecutionSheetName;
			Sheet executionSheet = getSheet(filepath, executionSheetName);
			for(int executionSheetRow=1;executionSheetRow<executionSheet.getLastRowNum();executionSheetRow++) {
				Row currentRow = executionSheet.getRow(executionSheetRow);
				String currentModuleName = currentRow.getCell(0).getStringCellValue();
				String currentModuleExecution = currentRow.getCell(2).getStringCellValue();
				Boolean isCurrentModuleExecutable = currentModuleExecution.equalsIgnoreCase("YES");
				if(isCurrentModuleExecutable) {
					Sheet moduleSheet = this.getSheet(filepath, currentModuleName);
					testCount = testCount+moduleSheet.getLastRowNum();
				}
			}
			excelData = new HashMap[testCount];
			testCount=0;
			for(int executionSheetRow=1;executionSheetRow<executionSheet.getPhysicalNumberOfRows();executionSheetRow++) {
				Row currentRow = executionSheet.getRow(executionSheetRow);
				String currentModuleName = currentRow.getCell(0).getStringCellValue();
				String currentModuleExecution = currentRow.getCell(2).getStringCellValue();
				Boolean isCurrentModuleExecutable = currentModuleExecution.equalsIgnoreCase("YES");
				if(isCurrentModuleExecutable) {
					Sheet moduleSheet = this.getSheet(filepath, currentModuleName);
					Row FirstRow = moduleSheet.getRow(0);
					for(int moduleSheetRow=1;moduleSheetRow<moduleSheet.getPhysicalNumberOfRows();moduleSheetRow++) {
						Row currentModuleCurrentRow = moduleSheet.getRow(moduleSheetRow);
						String currentModulecurrentTestExecution = currentModuleCurrentRow.getCell(2).getStringCellValue();
						Boolean isCurrentTestExecutable = currentModulecurrentTestExecution.equalsIgnoreCase("YES");
						if(isCurrentTestExecutable) {
							excelData[testCount] = new HashMap<String, String>();
							excelData[testCount].put("Module Name", currentModuleName);
							for(int currentColumnNumber=0;currentColumnNumber<currentModuleCurrentRow.getPhysicalNumberOfCells();currentColumnNumber++) {
								excelData[testCount].put(getCellData(FirstRow.getCell(currentColumnNumber)),getCellData(currentModuleCurrentRow.getCell(currentColumnNumber)));
							}
							testCount++;
						}
					}
				}
			}
			
		} catch(Exception e) {
			excelData=null;
			e.printStackTrace();
		}
		
		return excelData;
	}
}
