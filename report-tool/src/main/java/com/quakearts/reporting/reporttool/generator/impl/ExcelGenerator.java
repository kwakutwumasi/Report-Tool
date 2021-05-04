package com.quakearts.reporting.reporttool.generator.impl;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.servlet.ServletOutputStream;

import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.GeneratorBase;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.Report;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public class ExcelGenerator extends GeneratorBase {

	private static final String TIMESTAMPCELLSTYLE = "TIMESTAMPCELLSTYLE";
	private static final String TIMECELLSTYLE = "TIMECELLSTYLE";
	private static final String DATECELLSTYLE = "DATECELLSTYLE";
	private static final String DOUBLECELLSTYLE = "DOUBLECELLSTYLE";
	private static final String INTCELLSTYLE = "INTCELLSTYLE";
	private static final String COUNTERS = "COUNTERS";
	private static final String CURRENTROW = "CURRENTROW";
	private static final String CURRENTSHEET = "CURRENTSHEET";
	private static final String WORKBOOK = "WORKBOOK";
	/**
	 * 
	 */
	private static final long serialVersionUID = -3779312555939883419L;
	
	class Counters{
		int rowCounter;
		int columnCounter;
	}

	@Override
	public GeneratorState createStateForReport(Report report) throws GeneratorException {
		GeneratorState state = new GeneratorState();
		XSSFWorkbook workbook = new XSSFWorkbook();
		state.put(WORKBOOK, workbook);
		return state;
	}

	@Override
	protected void prepareReport(ReportQuery reportQuery, GeneratorState state, String header,
			ResultSetMetaData metaData) throws GeneratorException, SQLException {
		XSSFWorkbook workbook = state.get(WORKBOOK);
		XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(reportQuery.getName(),' '));
		state.put(CURRENTSHEET, sheet);
		
		if(header == null)
			header = reportQuery.getName();

		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);

		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setUnderline(FontUnderline.SINGLE);
		
		XSSFRichTextString headerString = new XSSFRichTextString(header);
		headerString.applyFont(font);

		cell.setCellValue(headerString);
		
		row = sheet.createRow(3);
		font = workbook.createFont();
		font.setBold(true);
		for(int index = 1; index<metaData.getColumnCount(); index++){
			headerString = new XSSFRichTextString(metaData.getColumnLabel(index));
			headerString.applyFont(font);
			
			cell = row.createCell(index-1);
			cell.setCellValue(headerString);
		}
		
		row = sheet.createRow(4);
		state.put(CURRENTROW, row);
		
		Counters counters = new Counters();
		counters.rowCounter = 4;
		counters.columnCounter = 0;
		state.put(COUNTERS, counters);
	}

	@Override
	protected void writeEmptyCell(GeneratorState state) throws GeneratorException {
		XSSFCell cell = createCell(state);
		cell.setCellValue(" ");
	}

	private XSSFCell createCell(GeneratorState state) {
		XSSFRow row = state.get(CURRENTROW);
		Counters counters = state.get(COUNTERS);
		XSSFCell cell = row.createCell(counters.columnCounter);
		counters.columnCounter++;
		return cell;
	}

	@Override
	protected void writeIntCell(GeneratorState state, int value) throws GeneratorException {
		XSSFCellStyle cellStyle; 
		if(!state.containsKey(INTCELLSTYLE)){
			XSSFWorkbook workbook = state.get(WORKBOOK);
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			cellStyle  = workbook.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("0"));
			state.put(INTCELLSTYLE, cellStyle);
		} else {
			cellStyle = state.get(INTCELLSTYLE);
		}
		XSSFCell cell = createCell(state);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void writeDoubleCell(GeneratorState state, double value) throws GeneratorException {
		XSSFCellStyle cellStyle; 
		if(!state.containsKey(DOUBLECELLSTYLE)){
			XSSFWorkbook workbook = state.get(WORKBOOK);
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			cellStyle  = workbook.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("0.00"));
			state.put(DOUBLECELLSTYLE, cellStyle);
		} else {
			cellStyle = state.get(DOUBLECELLSTYLE);
		}
		XSSFCell cell = createCell(state);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void writeDateCell(GeneratorState state, Date date) throws GeneratorException {
		XSSFCellStyle cellStyle; 
		if(!state.containsKey(DATECELLSTYLE)){
			XSSFWorkbook workbook = state.get(WORKBOOK);
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			cellStyle  = workbook.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("dd-mmm-yyyy"));
			state.put(DATECELLSTYLE, cellStyle);
		} else {
			cellStyle = state.get(DATECELLSTYLE);
		}
		XSSFCell cell = createCell(state);
		cell.setCellValue(date);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void writeTimeCell(GeneratorState state, Time time) throws GeneratorException {
		XSSFCellStyle cellStyle; 
		if(!state.containsKey(TIMECELLSTYLE)){
			XSSFWorkbook workbook = state.get(WORKBOOK);
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			cellStyle  = workbook.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("h:mm:ss AM/PM"));
			state.put(TIMECELLSTYLE, cellStyle);
		} else {
			cellStyle = state.get(TIMECELLSTYLE);
		}
		XSSFCell cell = createCell(state);
		cell.setCellValue(time);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void writeTimeStampCell(GeneratorState state, Timestamp timestamp) throws GeneratorException {
		XSSFCellStyle cellStyle; 
		if(!state.containsKey(TIMESTAMPCELLSTYLE)){
			XSSFWorkbook workbook = state.get(WORKBOOK);
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			cellStyle  = workbook.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("dd-mmm-yyyy h:mm:ss AM/PM"));
			state.put(TIMESTAMPCELLSTYLE, cellStyle);
		} else {
			cellStyle = state.get(TIMESTAMPCELLSTYLE);
		}
		XSSFCell cell = createCell(state);
		cell.setCellValue(timestamp);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void writeStringCell(GeneratorState state, String value) throws GeneratorException {
		XSSFCell cell = createCell(state);
		cell.setCellValue(value);
	}

	@Override
	protected void createNewRow(GeneratorState state) {
		Counters counters = state.get(COUNTERS);
		counters.rowCounter++;
		counters.columnCounter = 0;

		XSSFSheet sheet = state.get(CURRENTSHEET);
		XSSFRow row = sheet.createRow(counters.rowCounter);
		state.put(CURRENTROW, row);
	}

	@Override
	protected String getContentType() {
		return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	}

	@Override
	protected void writeToStream(GeneratorState state, ServletOutputStream outputStream) throws IOException {
		XSSFWorkbook workbook = state.get(WORKBOOK);
		workbook.write(outputStream);
		outputStream.flush();
	}

	@Override
	protected String getFileExtension() {
		return "xlsx";
	}
	
	@Override
	protected String getDispositionType() {
		return "attachement";
	}
}
