import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main {

	 public static void main(String[] args)throws Exception 
	   {
//	   //Blank Document
//	   XWPFDocument document= new XWPFDocument(); 
//	   //Write the Document in file system
//	   FileOutputStream out = new FileOutputStream(
//	   new File("createparagraph.docx"));
//	        
//	   //create Paragraph
//	   XWPFParagraph paragraph = document.createParagraph();
//	   XWPFRun run=paragraph.createRun();
//	   run.setText("Tinoco asdasdAt tutorialspoint.com, we strive hard to " +
//	   "provide quality tutorials for self-learning " +
//	   "purpose in the domains of Academics, Information " +
//	   "Technology, Management and Computer Programming Languages.");
//	   document.write(out);
//	   out.close();
//	   System.out.println("createparagraph.docx written successfully");
		 
		 Locale currentLocale;  
			//ResourceBundle labels;  
			String language;  
			String country;  
	
			if (args.length != 2) {  
				language = new String("pt");  
				country = new String("BR");  
			} else {  
				language = new String(args[0]);  
				country = new String(args[1]);  
			}  
	
			currentLocale = new Locale(language, country);  
	
			ResourceBundle labels =  
					ResourceBundle.getBundle("MessagesBundle", currentLocale);  
			Enumeration bundleKeys = labels.getKeys();  
	
			while (bundleKeys.hasMoreElements()) {  
				String key = (String) bundleKeys.nextElement();  
				String value = labels.getString(key);  
				System.out.println(key + " = " + value);  
			}  
	   }

}
