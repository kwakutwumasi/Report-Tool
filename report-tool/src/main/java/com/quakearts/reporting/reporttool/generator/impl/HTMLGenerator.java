package com.quakearts.reporting.reporttool.generator.impl;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;

import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.GeneratorBase;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.Report;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.webapp.facelets.bootstrap.common.BootHeaderRenderer.Theme;

public class HTMLGenerator extends GeneratorBase {

	private static final String TDCLOSE = "</td>";
	private static final String TDOPEN = "<td>";
	private static final String FIRST = "FIRST";
	private static final String STRINGBUILDER = "STRINGBUILDER";
	/**
	 * 
	 */
	private static final long serialVersionUID = 6429134792564570538L;

	@Override
	public GeneratorState createStateForReport(Report report) throws GeneratorException {
		GeneratorState state = new GeneratorState();
		StringBuilder stringBuilder = new StringBuilder("<!DOCTYPE html>\r\n").append("<html>\r\n").append("<head>\r\n")
				.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\r\n")
				.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\r\n")
				.append("<meta name=\"viewport\"\r\n")
				.append("\tcontent=\"width=device-width, initial-scale=1, maximum-scale=1\" />\r\n")
				.append(getThemeLink()).append("\r\n").append("</head>\r\n").append("<body>\r\n")
				.append("\t<div class=\"container-fluid\" id=\"container\">\r\n");
		
		state.put(STRINGBUILDER, stringBuilder);
		
		return state;
	}
	
	private String getThemeLink() {
		FacesContext context = FacesContext.getCurrentInstance();
		Theme theme = Theme.Default;
		String themeName = context.getExternalContext().getInitParameter("com.quakearts.bootstrap.theme");
		if(themeName != null)
			try {
				theme = Theme.valueOf(themeName);
			} catch (IllegalArgumentException e) {
				//DO NOTHING
			}		
		return theme.getLink().replace("@root", context.getExternalContext().getRequestContextPath());
	}

	@Override
	protected void prepareReport(ReportQuery reportQuery, GeneratorState state, String header,
			ResultSetMetaData metaData) throws GeneratorException, SQLException {
		if(header == null)
			header = reportQuery.getName();
		
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		if(state.containsKey(FIRST)){
			stringBuilder.append("\t\t\t\t</tr>\r\n\t\t\t</tbody>\t\t</table>");
		} else {
			state.put(FIRST, "");
		}
		
		stringBuilder.append("\t\t<table class=\"table\">\r\n\t\t\t<thead>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<th colspan=\"")
			.append(metaData.getColumnCount()).append("\">").append(header).append("</th></tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t");
		for(int index = 1; index<metaData.getColumnCount(); index++){
			stringBuilder.append("<th>").append(metaData.getColumnName(index)).append("</th>");
		}
		stringBuilder.append("\r\n\t\t\t\t</tr>\r\n\t\t\t</thead>\r\n\t\t\t<tbody>\t\t\t\t<tr>");
	}

	@Override
	protected void writeEmptyCell(GeneratorState state) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append("<td />");
	}

	@Override
	protected void writeIntCell(GeneratorState state, int value) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append(TDOPEN).append(value).append(TDCLOSE);
	}

	@Override
	protected void writeDoubleCell(GeneratorState state, double value) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		stringBuilder.append(TDOPEN).append(format.format(value)).append(TDCLOSE);
	}

	@Override
	protected void writeDateCell(GeneratorState state, Date date) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
		stringBuilder.append(TDOPEN).append(format.format(date)).append(TDCLOSE);
	}

	@Override
	protected void writeTimeCell(GeneratorState state, Time time) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		stringBuilder.append(TDOPEN).append(format.format(time)).append(TDCLOSE);
	}

	@Override
	protected void writeTimeStampCell(GeneratorState state, Timestamp timestamp) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		stringBuilder.append(TDOPEN).append(format.format(timestamp)).append(TDCLOSE);
	}

	@Override
	protected void writeStringCell(GeneratorState state, String value) throws GeneratorException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append(TDOPEN).append(value).append(TDCLOSE);
	}

	@Override
	protected void createNewRow(GeneratorState state) {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append("\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>");
	}

	@Override
	protected String getContentType() {
		return "text/html";
	}

	@Override
	protected void writeToStream(GeneratorState state, ServletOutputStream outputStream) throws IOException {
		StringBuilder stringBuilder = state.get(STRINGBUILDER);
		stringBuilder.append("\t\t\t\t</tr>\r\n\t\t\t</tbody>\t\t</table>\r\n\t</div>\r\n").append( 
				"</body>\r\n").append( 
				"</html>");

		outputStream.write(stringBuilder.toString().getBytes());
		outputStream.flush();
	}

	@Override
	protected String getFileExtension() {
		return "html";
	}
	
	@Override
	protected String getDispositionType() {
		return "inline";
	}
}
