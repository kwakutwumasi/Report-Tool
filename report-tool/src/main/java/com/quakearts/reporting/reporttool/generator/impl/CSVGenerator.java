package com.quakearts.reporting.reporttool.generator.impl;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletOutputStream;

import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.GeneratorBase;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.Report;
import com.quakearts.reporting.reporttool.model.ReportQuery;

public class CSVGenerator extends GeneratorBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3462448166385675736L;
	private static final String STRINGBUILDER = "STRINGBUILDER";
	private static final String FIRST = "FIRST";
	private static final String ROWSTART = "ROWSTART";

	@Override
	public GeneratorState createStateForReport(Report report) throws GeneratorException {
		GeneratorState state = new GeneratorState();
		StringBuilder stringBuilder = new StringBuilder();
		state.put(STRINGBUILDER, stringBuilder);
		state.put(ROWSTART, Boolean.FALSE);
		return state;
	}

	@Override
	protected void prepareReport(ReportQuery reportQuery, GeneratorState state, String header,
			ResultSetMetaData metaData) throws GeneratorException, SQLException {
		if(header == null)
			header = reportQuery.getName();

		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		if(state.containsKey(FIRST)){
			stringBuilder.append("\r\n");
		} else {
			state.put(FIRST, "");
		}
		stringBuilder.append(header).append("\r\n");
		for(int index = 1; index<metaData.getColumnCount(); index++){
			stringBuilder.append(index>1?",":"").append(metaData.getColumnLabel(index));
		}
		stringBuilder.append("\r\n");
	}

	@Override
	protected void writeEmptyCell(GeneratorState state) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append(",");
	}

	@Override
	protected void writeIntCell(GeneratorState state, int value) throws GeneratorException {
		writeValue(state, value);
	}

	@Override
	protected void writeDoubleCell(GeneratorState state, double value) throws GeneratorException {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		writeValue(state, format.format(value));
	}

	@Override
	protected void writeDateCell(GeneratorState state, Date date) throws GeneratorException {
		writeValue(state, new SimpleDateFormat("dd/MM/yyyy").format(date));
	}

	@Override
	protected void writeTimeCell(GeneratorState state, Time time) throws GeneratorException {
		writeValue(state, new SimpleDateFormat("HH:mm:ss").format(time));
	}

	@Override
	protected void writeTimeStampCell(GeneratorState state, Timestamp timestamp) throws GeneratorException {
		writeValue(state, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp));
	}

	@Override
	protected void writeStringCell(GeneratorState state, String value) throws GeneratorException {
		writeValue(state, value);
	}

	private void writeValue(GeneratorState state, Object value) {
		StringBuilder builder = state.get(STRINGBUILDER);
		writeCommaIfNecessary(state, builder);
		builder.append(value);
	}

	private void writeCommaIfNecessary(GeneratorState state, StringBuilder builder) {
		boolean rowStart = state.get(ROWSTART);
		if(rowStart){
			builder.append(",");
		} else {
			state.put(ROWSTART, Boolean.TRUE);
		}
	}

	@Override
	protected void createNewRow(GeneratorState state) {
		state.put(ROWSTART, Boolean.FALSE);
		StringBuilder builder = state.get(STRINGBUILDER);
		builder.append("\r\n");
	}

	@Override
	protected String getContentType() {
		return "text/csv";
	}

	@Override
	protected void writeToStream(GeneratorState state, ServletOutputStream outputStream) throws IOException {
		StringBuilder builder = state.get(STRINGBUILDER);
		outputStream.write(builder.toString().getBytes());
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}
	
	@Override
	protected String getDispositionType() {
		return "attachement";
	}
}
