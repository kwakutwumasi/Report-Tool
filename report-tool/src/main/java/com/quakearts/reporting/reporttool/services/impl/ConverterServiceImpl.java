package com.quakearts.reporting.reporttool.services.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.reporting.reporttool.generator.converter.Converter;
import com.quakearts.reporting.reporttool.model.ReportColumnConverter;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.services.ConverterService;
import com.quakearts.tools.classloaders.DBJarClassLoader;
import com.quakearts.tools.classloaders.utils.JarFileStorer;

@Singleton
public class ConverterServiceImpl implements ConverterService {

	private static final Logger log = LoggerFactory.getLogger(ConverterService.class);
	
	private Map<String, Converter> converterCache = new ConcurrentHashMap<>();
	private Map<String, String> reportQueryIndexConverterMapping = new ConcurrentHashMap<>();
	private Set<Integer> loadedReports = new HashSet<>();
	private DBJarClassLoader loader = new DBJarClassLoader();
	private JarFileStorer jarFileStorer = new JarFileStorer();
	
	@Override
	public Converter getConverter(ReportQuery reportQuery, int index) {
		if(reportQuery.getReportColumnConverters().isEmpty())
			return null;
		
		if(!loadedReports.contains(reportQuery.getId())){
			loadReportQueryConverters(reportQuery);
		}
		
		String converterKey = reportQueryIndexConverterMapping.get(reportQuery.getId()+"_"+index);
		if(converterKey==null)
			return null;
		
		return converterCache.get(converterKey);
	}

	private void loadReportQueryConverters(ReportQuery reportQuery) {
		for(ReportColumnConverter reportColumnConverter
				:reportQuery.getReportColumnConverters()){
			if(!converterCache.containsKey(reportColumnConverter.getConverterClass())){
				try {
					Class<?> converterClass = loader.getDBJarClass(reportColumnConverter.getConverterClass());
					if(Converter.class.isAssignableFrom(converterClass)){
						Object converterObj = converterClass.newInstance();
						converterCache.put(reportColumnConverter.getConverterClass(), Converter.class.cast(converterObj));
					} else {
						log.error("Unable to load converter class {} for Report Query {}, ID: {}. Class does not implement {}", 
								reportColumnConverter.getConverterClass(), reportQuery.getName(),
								reportQuery.getId(),
								Converter.class.getName());
					}
				} catch (ReflectiveOperationException e) {
					log.error("Unable to find converter class {} for Report Query {}, ID: {}", 
							reportColumnConverter.getConverterClass(), reportQuery.getName(),
							reportQuery.getId());
					log.error("", e);
				}
			}
			String[] positions = reportColumnConverter.getPositions().split(",");
			for(String position:positions){
				reportQueryIndexConverterMapping.put(reportQuery.getId()+"_"+position, 
						reportColumnConverter.getConverterClass());
			}
		}
	}

	@Override
	public void loadConverter(String fileName, byte[] fileBytes) throws IOException {
		jarFileStorer.storeJarFile(fileBytes, fileName);
	}

	@Override
	public boolean verifyConverterClass(String converterClassName) {
		try {
			Class<?> converterClass = loader.getDBJarClass(converterClassName);
			return Converter.class.isAssignableFrom(converterClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
