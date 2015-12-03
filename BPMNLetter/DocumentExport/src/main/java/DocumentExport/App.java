package DocumentExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	XWPFDocument document = new XWPFDocument();
    	XWPFParagraph tmpParagraph = document.createParagraph();
    	XWPFRun tmpRun = tmpParagraph.createRun();
    	tmpRun.setText("LALALALAALALAAAA");
    	tmpRun.setFontSize(18);
    	try {
			InputStream image = new FileInputStream(new File("C:\\Users\\Rafael\\Desktop\\BPMNLetter\\AAAA.png"));
			document.createParagraph().createRun().addPicture(image, XWPFDocument.PICTURE_TYPE_JPEG, "my pic", 200, 200);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	document.write(new FileOutputStream(new File(":\\Users\\Rafael\\Desktop\\BPMNLetter\\ABC.doc")));
    }
}
