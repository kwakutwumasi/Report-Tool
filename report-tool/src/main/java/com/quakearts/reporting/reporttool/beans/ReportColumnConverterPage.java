package com.quakearts.reporting.reporttool.beans;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.cdi.annotation.DataStoreFactoryHandle;
import com.quakearts.webapp.orm.exception.DataStoreException;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.reporting.reporttool.generator.converter.Converter;
import com.quakearts.reporting.reporttool.model.ReportColumnConverter;
import com.quakearts.reporting.reporttool.model.ReportQuery;
import com.quakearts.reporting.reporttool.services.ConverterService;
import com.quakearts.tools.classloaders.hibernate.JarFileEntry;

@Named("reportColumnConverterPage")
@ViewScoped
public class ReportColumnConverterPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1976644476148841812L;

	private static Logger log = Logger.getLogger(ReportColumnConverterPage.class.getName());

	private ReportColumnConverter reportColumnConverter;
	
	@Inject @Named("reportapp")
	private ReportApplicationBean reportapp;
	private transient ReportColumnConverterFinder finder = new ReportColumnConverterFinder();
			
	@Inject @DataStoreFactoryHandle
	private transient DataStoreFactory dataStoreFactory;
	
	@Inject
	private transient ConverterService converterService;
	
	public ReportApplicationBean getReportapp(){
		if(reportapp == null)
			reportapp = new ReportApplicationBean();
			
		return reportapp;
	}
	
	public ReportColumnConverter getReportColumnConverter() {
		if(reportColumnConverter==null){			
			if(hasParameter("reportColumnConverter")){
				setReportColumnConverter(finder.getById(getParameterInt("reportColumnConverter")));
				reportapp.setMode("read");
			} else {
	    		reportColumnConverter = new ReportColumnConverter();
			}
		}
		
		return reportColumnConverter;
	}
    	
	public void setReportColumnConverter(ReportColumnConverter reportColumnConverter) {
		this.reportColumnConverter = reportColumnConverter;
		if(reportColumnConverter != null){
			ReportQuery reportQuery = reportColumnConverter.getReportQuery();
			if(reportQuery!=null){
				getReportQueryDropdownHelper().addToFoundItemsList(reportQuery);
			}
			String converterClassName = reportColumnConverter.getConverterClass();
			if(converterClassName!=null){
				foundClassNames = Arrays.asList(converterClassName);
			}
		}
	}
	
	private ReportQueryDropdownHelper reportQueryDropdownHelper;

	public ReportQueryDropdownHelper getReportQueryDropdownHelper(){
		if(reportQueryDropdownHelper == null)
			reportQueryDropdownHelper = new ReportQueryDropdownHelper();
			
		return reportQueryDropdownHelper;
	}
	
	private List<ReportColumnConverter> reportColumnConverterList;
	
	public List<ReportColumnConverter> getReportColumnConverterList(){
		return reportColumnConverterList;
	}
	
	public void findReportColumnConverter(ActionEvent event){
		CriteriaMapBuilder criteriaBuilder = CriteriaMapBuilder.createCriteria();
		if(reportColumnConverter.getConverterClass() != null && ! reportColumnConverter.getConverterClass().trim().isEmpty()){
			criteriaBuilder.property("converterClass").mustBeLike(reportColumnConverter.getConverterClass());
		}
		if(reportColumnConverter.getPositions() != null && ! reportColumnConverter.getPositions().trim().isEmpty()){
			criteriaBuilder.property("positions").mustBeLike(reportColumnConverter.getPositions());
		}
		if(reportColumnConverter.getReportQuery() != null){
			criteriaBuilder.property("reportQuery").mustBeEqualTo(reportColumnConverter.getReportQuery());
		}
		if(reportColumnConverter.isValid()){
			criteriaBuilder.property("valid").mustBeEqualTo(reportColumnConverter.isValid());
		}
    		
		try {
			reportColumnConverterList = finder.findObjects(criteriaBuilder.finish());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Report Column Converter", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for ReportColumnConverter");
		}		
	}
    	
	public void removeReportColumnConverter(ActionEvent event){
		if(reportColumnConverter!=null && reportColumnConverterList!=null){
			reportColumnConverterList.remove(reportColumnConverter);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(reportapp.getMode());
	}
	
	private transient Validator positionsValidator = new PositionsValidator();
	
	public Validator getPositionsValidator() {
		return positionsValidator;
	}
	
	private String classSuggestion;
	
	public String getClassSuggestion() {
		return classSuggestion;
	}
	
	public void setClassSuggestion(String classSuggestion) {
		this.classSuggestion = classSuggestion;
	}
	
	private List<String> foundClassNames;
	
	public List<String> getFoundClassNames() {
		return foundClassNames;
	}
	
	public void findClassNames(AjaxBehaviorEvent event){
		if(classSuggestion!=null){
			foundClassNames = dataStoreFactory.getDataStore()
					.find(JarFileEntry.class)
					.filterBy("id").withAValueLike("%"+classSuggestion+"%")
					.thenList().stream().map(JarFileEntry::getId)
					.filter(this::isAClassFile)
					.map(this::convertToClassName)
					.collect(Collectors.toList());
		}
	}
	
	private boolean isAClassFile(String id){
		return id.endsWith(".class");
	}
	
	private String convertToClassName(String classFile){
		return classFile.replace(".class", "")
				.replace("/", ".");
	}
	
	private transient Validator converterClassNameValidator = (context,component,value)->{
		if(value == null)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Data", 
					"'Converter Class' is required"));
		if(!converterService.verifyConverterClass(value.toString()))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Data", 
					"'Converter Class' must implement "+Converter.class.getName()));
	};
	
	public Validator getConverterClassNameValidator() {
		return converterClassNameValidator;
	}
	
	private byte[] jarBytes;

	public byte[] getJarBytes() {
		return jarBytes;
	}

	public void setJarBytes(byte[] jarBytes) {
		this.jarBytes = jarBytes;
	}
	
	private String jarFileName;

	public String getJarFileName() {
		return jarFileName;
	}

	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}
	
	public void storeJarFile(ActionEvent event){
		if(jarBytes==null || jarFileName == null){
			addError("Invalid Selection", "File upload is required to proceed", 
					FacesContext.getCurrentInstance());
			return;
		}
		
		try {
			converterService.loadConverter(jarFileName, jarBytes);
		} catch (IOException e) {
			addError("System Error", "File upload is failed to complete: "+e.getMessage(), 
					FacesContext.getCurrentInstance());
			return;
		}
		
		addMessage("Success", "File has been uploaded", FacesContext.getCurrentInstance());
	}
}
