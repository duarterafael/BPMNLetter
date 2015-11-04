package DocumentExport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    	document.write(new FileOutputStream(new File("yourpathhere")));
    }
}
