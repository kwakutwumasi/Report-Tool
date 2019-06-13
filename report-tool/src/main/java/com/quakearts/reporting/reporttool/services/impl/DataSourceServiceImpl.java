package com.quakearts.reporting.reporttool.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.quakearts.appbase.Main;
import com.quakearts.appbase.internal.properties.ConfigurationPropertyMap;
import com.quakearts.appbase.spi.DataSourceProviderSpi.PropertyNames;
import com.quakearts.appbase.spi.factory.DataSourceProviderSpiFactory;
import com.quakearts.appbase.spi.factory.JavaNamingDirectorySpiFactory;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField;
import com.quakearts.reporting.reporttool.configuration.model.DataSourceConfigField.DataSourceFieldType;
import com.quakearts.reporting.reporttool.enums.DataSourceType;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.services.DataSourceService;
import com.quakearts.reporting.reporttool.services.model.DataSourceDisplay;

@Singleton
public class DataSourceServiceImpl implements DataSourceService {

	@Override
	public Connection getConnection(ReportQuery reportQuery) throws NamingException, SQLException {
		DataSource dataSource = (DataSource) JavaNamingDirectorySpiFactory.getInstance()
				.getJavaNamingDirectorySpi()
				.getInitialContext().lookup(reportQuery.getDataSourceJndiName()!=null
				&& !reportQuery.getDataSourceJndiName().trim().isEmpty()?
						reportQuery.getDataSourceJndiName():reportQuery.getReport().getDataSourceJndiName());
		return dataSource.getConnection();
	}

	@Override
	public List<DataSourceDisplay> listDataSources() {
		NamingEnumeration<Binding> enumeration;
		try {
			List<DataSourceDisplay> list = new ArrayList<>();
			enumeration = JavaNamingDirectorySpiFactory
					.getInstance().getJavaNamingDirectorySpi()
					.getInitialContext().listBindings("java:/jdbc");
			while(enumeration.hasMoreElements()){
				Binding binding = enumeration.next();
				if(binding.getObject() instanceof DataSource){
					addTolist(list, binding);
				}
			}
			return list;
		} catch (NamingException e) {
			//Do nothing. Won't ever be thrown
			return Collections.emptyList();
		}
	}

	private void addTolist(List<DataSourceDisplay> list, Binding binding) {
		DataSourceDisplay dataSourceDisplay = new DataSourceDisplay();
		dataSourceDisplay.setJndiName((binding.isRelative()?"java:/jdbc/":"")+binding.getName());
		try{
			AtomikosDataSourceBean dataSource = (AtomikosDataSourceBean) binding.getObject();
			Properties properties = dataSource.getXaProperties();
			StringBuilder builder = new StringBuilder();
			for(DataSourceConfigField field:DataSourceType.fromClassName(dataSource.getXaDataSourceClassName())
					.createDataSourceConfigurator().getCommonConfigFields()){
				if(field.getType()!=DataSourceFieldType.PASSWORD){
					builder.append(field.getDisplayName())
						.append("=")
						.append(properties.get(field.getPropertyName()))
						.append("<br />");
				}
			}
			dataSourceDisplay.setDisplayName(builder.toString());
		} catch (ClassCastException | UnsupportedOperationException e) {
			dataSourceDisplay.setDisplayName("Unavailable: "+e.getMessage());
		}
		list.add(dataSourceDisplay);
	}

	@Override
	public void createDataSource(String configJson, String fileName, boolean inReplaceMode) throws IOException {
		File datasourceDirectory = new File("atomikos"+File.separator+"datasources");
		File datasourceFile = new File(datasourceDirectory, fileName);
		
		try(FileOutputStream outstream = new FileOutputStream(datasourceFile)){
			outstream.write(configJson.getBytes());
			outstream.flush();
		}
		
		ByteArrayInputStream instream = new ByteArrayInputStream(configJson.getBytes());
		ConfigurationPropertyMap configurationPropertyMap = Main.getAppBasePropertiesLoader()
			.loadParametersFromReader(fileName, new InputStreamReader(instream));
		
		if(inReplaceMode){
			try {
				InitialContext context = JavaNamingDirectorySpiFactory
						.getInstance().getJavaNamingDirectorySpi()
						.getInitialContext();
				String jndiName = "java:/jdbc/"+configurationPropertyMap.getString(PropertyNames.NAME.getPropertyName());
				AtomikosDataSourceBean dataSourceBean = (AtomikosDataSourceBean) context.lookup(jndiName);
				dataSourceBean.close();
				context.unbind(jndiName);
			} catch (NamingException e) {
				//not named. continue
			}
		}
		
		DataSourceProviderSpiFactory.getInstance().getDataSourceProviderSpi()
			.getDataSource(configurationPropertyMap);
	}

}
