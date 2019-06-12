package com.quakearts.reporting.reporttool.generator;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;
import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.converter.Converter;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.services.ConverterService;

public abstract class GeneratorBase implements Generator, LogChute, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7917505591106825813L;
	private transient VelocityEngine engine;
	private static final Logger log = LoggerFactory.getLogger(GeneratorBase.class);
	
	public GeneratorBase() {
		engine = new VelocityEngine();
		engine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, this);
		engine.setProperty("resource.loader", "string");
		engine.setProperty("string.loader.description", "String loader");
		engine.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());

		engine.init();
	}
	
	@Inject
	private ConverterService converterService;

	@Override
	public void generate(ReportQuery reportQuery, List<ReportQueryParameterBean> parameters, ResultSet resultSet,
			GeneratorState state) throws SQLException, GeneratorException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		String header = generateHeader(reportQuery, parameters);
		prepareReport(reportQuery, state, header, metaData);
		do {
			for(int index = 1; index<=metaData.getColumnCount(); index++){
				switch (metaData.getColumnType(index)) {
				case Types.INTEGER:
					writeInt(resultSet, state, index);
					break;
				case Types.DECIMAL:
				case Types.DOUBLE:
					writeDouble(resultSet, state, index);
					break;
				case Types.DATE:
					writeDate(resultSet, state, index);
					break;
				case Types.TIME:
					writeTime(resultSet, state, index);
					break;
				case Types.TIMESTAMP:
					writeTimestamp(resultSet, state, index);
					break;
				default:
					writeDefault(resultSet, state, index, reportQuery);
					break;
				}
			}
			createNewRow(state);
		} while (resultSet.next());
	}

	@Override
	public void writeToResponse(GeneratorState state, HttpServletResponse response) throws GeneratorException {
		response.setContentType(getContentType());
		response.setHeader("Content-Disposition", getDispositionType()+ ";filename=Report-"
				+(new SimpleDateFormat("ddd-dd-MM-yyyy-HH-mm-ss").format(new java.util.Date()))+"."+getFileExtension());
		try {
			writeToStream(state, response.getOutputStream());
		} catch (IOException e) {
			throw new GeneratorException("Unable to generate report", e);
		}		
	}
	
	private void writeInt(ResultSet resultSet, GeneratorState state, int index)
			throws SQLException, GeneratorException {
		int intValue = resultSet.getInt(index);
		if(resultSet.wasNull()){
			writeEmptyCell(state);
		} else {
			writeIntCell(state, intValue);
		}
	}

	private void writeDouble(ResultSet resultSet, GeneratorState state, int index)
			throws SQLException, GeneratorException {
		double doubleValue = resultSet.getDouble(index);
		if(resultSet.wasNull()){
			writeEmptyCell(state);
		} else {
			writeDoubleCell(state, doubleValue);
		}
	}

	private void writeDate(ResultSet resultSet, GeneratorState state, int index)
			throws SQLException, GeneratorException {
		Date dateValue = resultSet.getDate(index);
		if(resultSet.wasNull()){
			writeEmptyCell(state);
		} else {
			writeDateCell(state, dateValue);
		}
	}

	private void writeTime(ResultSet resultSet, GeneratorState state, int index)
			throws SQLException, GeneratorException {
		Time timeValue = resultSet.getTime(index);
		if(resultSet.wasNull()){
			writeEmptyCell(state);
		} else {
			writeTimeCell(state, timeValue);
		}
	}

	private void writeTimestamp(ResultSet resultSet, GeneratorState state, int index)
			throws SQLException, GeneratorException {
		Timestamp timestampValue = resultSet.getTimestamp(index);
		if(resultSet.wasNull()){
			writeEmptyCell(state);
		} else {
			writeTimeStampCell(state, timestampValue);
		}
	}

	private void writeDefault(ResultSet resultSet, GeneratorState state, int index, ReportQuery reportQuery)
			throws SQLException, GeneratorException {
		Converter converter = converterService.getConverter(reportQuery, index);
		
		if(converter!=null){
			String stringValue = converter.convertResult(resultSet, index);
			if(stringValue==null){
				writeEmptyCell(state);
			} else {
				writeStringCell(state, stringValue);
			}
		} else {
			String stringValue = resultSet.getString(index);
			if(resultSet.wasNull()){
				writeEmptyCell(state);
			} else {
				writeStringCell(state, stringValue);
			}
		}
	}

	private String generateHeader(ReportQuery reportQuery, List<ReportQueryParameterBean> parameters) throws GeneratorException {
		if(reportQuery.getReport().getHeader()==null)
			return "";
		
		if(StringResourceLoader.getRepository().getStringResource(reportQuery.getName())==null){
			StringResourceLoader.getRepository().putStringResource(reportQuery.getName(), reportQuery.getReport().getHeader());
		}
		
		VelocityContext context = new VelocityContext();
		context.put("reportName", reportQuery.getReport().getName());
		context.put("reportQueryName", reportQuery.getName());
		context.put("timestamp", LocalDateTime.now());
		for(ReportQueryParameterBean bean:parameters){
			context.put(bean.getVariableName(), bean.getValue());
		}
		
		Template template = engine.getTemplate(reportQuery.getName());
		StringWriter writer = new StringWriter();
		try {
			template.merge(context, writer);
		} catch (Exception e) {
			throw new GeneratorException("Unable to generate header", e);
		}
		
		return writer.toString();
	}
	

	@Override
	public void init(RuntimeServices rs) throws Exception {
		log.trace("Initializing logger for GeneratorBase implementation {}",getClass().getName());
	}

	@Override
	public void log(int level, String message) {
		log(level, message, null);
	}

	@Override
	public void log(int level, String message, Throwable t) {
		switch (level) {
		case LogChute.DEBUG_ID:
			if(t!=null)
				log.debug(message);
			else 
				log.debug(message, t);
			break;
		case LogChute.ERROR_ID:
			if(t!=null)
				log.error(message);
			else 
				log.error(message, t);
			break;
		case LogChute.TRACE_ID:
			if(t!=null)
				log.trace(message);
			else 
				log.trace(message, t);
			break;
		case LogChute.WARN_ID:
			if(t!=null)
				log.warn(message);
			else 
				log.warn(message, t);
			break;
		default:
			log.info(message);
			break;
		}
		
	}

	@Override
	public boolean isLevelEnabled(int level) {
		switch (level) {
		case LogChute.DEBUG_ID:
			return log.isDebugEnabled();
		case LogChute.ERROR_ID:
			return log.isErrorEnabled();
		case LogChute.TRACE_ID:
			return log.isTraceEnabled();
		case LogChute.WARN_ID:
			return log.isWarnEnabled();
		default:
			return log.isInfoEnabled();
		}
	}
	
	protected abstract void prepareReport(ReportQuery reportQuery, GeneratorState state, 
			String header, ResultSetMetaData metaData) throws GeneratorException, SQLException;
	protected abstract void writeEmptyCell(GeneratorState state) throws GeneratorException;
	protected abstract void writeIntCell(GeneratorState state, int value) throws GeneratorException;
	protected abstract void writeDoubleCell(GeneratorState state, double value) throws GeneratorException;
	protected abstract void writeDateCell(GeneratorState state, Date date) throws GeneratorException;
	protected abstract void writeTimeCell(GeneratorState state, Time time) throws GeneratorException;
	protected abstract void writeTimeStampCell(GeneratorState state, Timestamp timestamp) throws GeneratorException;
	protected abstract void writeStringCell(GeneratorState state, String value) throws GeneratorException;
	protected abstract void createNewRow(GeneratorState state);
	protected abstract String getContentType();
	protected abstract void writeToStream(GeneratorState state, ServletOutputStream outputStream) throws IOException;
	protected abstract String getFileExtension();

	protected abstract String getDispositionType();
}
