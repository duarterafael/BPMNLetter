package com.bpmn.letter.word.document;

import java.util.Locale;
import java.util.ResourceBundle;

public class DocumentUtils {
	private static DocumentUtils instance = null;
	private Locale currentLocale;  
	private String language;  
	private String country;  
	private ResourceBundle labels;
	
	
	
	public static final String START_EVENT_PART_1 = "START_EVENT_PART_1";
	public static final String START_EVENT_PART_2 = "START_EVENT_PART_2";
	
	private DocumentUtils(){
		language = new String("pt");  
		country = new String("BR");
		
		currentLocale = new Locale(language, country);  

		labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);  

	}

	public static DocumentUtils getInstance(){
		if(instance ==  null){
			instance = new DocumentUtils();
		}
		return instance;
	}
	
	public ResourceBundle getI18NKeys(){
		return (ResourceBundle) labels.getKeys();  
	}
	
	
	public String getI18NLabel(String key){
		return labels.getString(key); 
	}
	
	

	

		
}
