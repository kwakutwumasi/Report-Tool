package com.quakearts.reporting.reporttool.services.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.naming.NamingException;
import com.quakearts.reporting.reporttool.beans.ReportQueryParameterBean;
import com.quakearts.reporting.reporttool.exception.GeneratorException;
import com.quakearts.reporting.reporttool.generator.Generator;
import com.quakearts.reporting.reporttool.generator.GeneratorState;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.model.ReportQuery.QueryType;
import com.quakearts.reporting.reporttool.services.DataSourceService;
import com.quakearts.reporting.reporttool.services.ReportQueryService;
import com.quakearts.reporting.reporttool.services.UserRoleParameterService;

@Singleton
public class ReportQueryServiceImpl implements ReportQueryService {

	@Inject
	private DataSourceService dataSourceService;
	
	@Inject
	private UserRoleParameterService userRoleParameterService;

	@Override
	public void run(ReportQuery reportQuery, List<ReportQueryParameterBean> parameters, Generator generator,
			GeneratorState state) throws SQLException, NamingException, IOException, GeneratorException {
		try(Connection connection = dataSourceService.getConnection(reportQuery)){
			if(reportQuery.getType() == QueryType.PARAMETERED || reportQuery.getType() == QueryType.PROCEDURE){
				PreparedStatement statement = null;
				try {
					if(reportQuery.getType() == QueryType.PARAMETERED){
						statement = connection.prepareStatement(reportQuery.getSql());
					} else {
						statement = connection.prepareCall("{call "+reportQuery.getSql()+"}");
					}
					if(parameters!=null){
						setParameters(statement, parameters);
					}
					try(ResultSet resultSet = statement.executeQuery()){
						if(resultSet.next())
							generator.generate(reportQuery, parameters, resultSet, state);
					}
				} finally {
					if(statement!=null)
						statement.close();
				}
			} else {
				try(Statement statement = connection.createStatement()){
					try(ResultSet resultSet = statement.executeQuery(reportQuery.getSql())){
						if(resultSet.next())
							generator.generate(reportQuery, parameters, resultSet, state);
					}
				}
			}
		}
	}

	private void setParameters(PreparedStatement statement, List<ReportQueryParameterBean> parameters) throws SQLException {
		for(ReportQueryParameterBean bean:parameters){
			switch (bean.getType()) {
			case INT:
				populateIntegerParameters(statement, bean);
				break;
			case DOUBLE:
				populateDoubleParameters(statement, bean);
				break;
			case DATE:
				populateDateParameters(statement, bean);
				break;
			case DATETIME:
				populateTimestampParameters(statement, bean);
				break;
			case CURRENTUSER:
				populateUsernameParameters(statement, bean);
				break;
			case CURRENTUSERROLE:
				populateRoleParameter(statement, bean);
				break;
			default:
				populateStringParameters(statement, bean);
				break;
			}
		}
	}

	private void populateIntegerParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		for(int position:bean.getPositions()){
			if(bean.isNull()){
				statement.setNull(position, Types.INTEGER);
			} else {
				statement.setInt(position, bean.getAndConvertValue());
			}
		}
	}

	private void populateDoubleParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		for(int position:bean.getPositions()){
			if(bean.isNull()){
				statement.setNull(position, Types.DOUBLE);
			} else {
				statement.setDouble(position, bean.getAndConvertValue());
			}
		}
	}

	private void populateDateParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		for(int position:bean.getPositions()){
			if(bean.isNull()){
				statement.setNull(position, Types.DATE);
			} else {
				statement.setDate(position, bean.getAndConvertValue());
			}
		}
	}
	
	private void populateTimestampParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		for(int position:bean.getPositions()){
			if(bean.isNull()){
				statement.setNull(position, Types.DATE);
			} else {
				statement.setTimestamp(position, bean.getAndConvertValue());
			}
		}
	}

	private void populateUsernameParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		String username = FacesContext.getCurrentInstance()
			.getExternalContext().getRemoteUser();
		for(int position:bean.getPositions()){
			if(username==null){
				statement.setNull(position, Types.VARCHAR);
			} else {
				statement.setString(position, username);
			}
		}
	}

	private void populateRoleParameter(PreparedStatement statement, ReportQueryParameterBean bean) 
			throws SQLException {
		String roleValue = userRoleParameterService.resolve(bean);
		for(int position:bean.getPositions()){
			if(roleValue==null){
				statement.setNull(position, Types.VARCHAR);
			} else {
				statement.setString(position, roleValue);
			}
		}
	}

	private void populateStringParameters(PreparedStatement statement, ReportQueryParameterBean bean)
			throws SQLException {
		for(int position:bean.getPositions()){
			if(bean.isNull()){
				statement.setNull(position, Types.VARCHAR);
			} else {
				statement.setString(position, bean.getAndConvertValue());
			}
		}
	}

}
