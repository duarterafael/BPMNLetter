package com.bpmn.letter.word.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public class WordDocumentHelper implements IDocumentMaker{
	private static WordDocumentHelper instance = null;
	private XWPFDocument document = null;
	private FileOutputStream out;
	private WordDocumentHelper (String documentPath) throws FileNotFoundException{
		//Blank Document
		document= new XWPFDocument(); 
		//Write the Document in file system
		out = new FileOutputStream(new File(documentPath));


	}

	public static WordDocumentHelper getInstance(String documentPath) throws FileNotFoundException{
		if(instance == null){
			instance = new WordDocumentHelper(documentPath);
		}
		return instance;
	}

	public void insertParagraph(String content) throws IOException{
		//create Paragraph
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run=paragraph.createRun();
		run.setText(content);
	}

	public void insertImage(String imagePaht) throws FileNotFoundException{
		try {
			InputStream image = new FileInputStream(new File(imagePaht));
			document.createParagraph().createRun().addPicture(image, XWPFDocument.PICTURE_TYPE_JPEG, "my pic", Units.toEMU(500), Units.toEMU(500));
			changeOrientation(document, "landscape");
			XWPFParagraph paragraph = document.createParagraph();
			XWPFRun run=paragraph.createRun();
			run.addCarriageReturn();                 //separate previous text from break
			run.addBreak(BreakType.PAGE);
			run.addBreak(BreakType.TEXT_WRAPPING); 
			changeOrientation(document, "portrate");

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void finishDocument() throws IOException{
		document.write(out);
		out.close();
	}

	public void insertCapa(String content) {
		//create Paragraph
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run=paragraph.createRun();
		run.setText(content);
		run.addCarriageReturn();                 //separate previous text from break
		run.addBreak(BreakType.PAGE);
		run.addBreak(BreakType.TEXT_WRAPPING); 

	}
	
	private static void changeOrientation(XWPFDocument doc, String orientation){
	    CTDocument1 document=doc.getDocument();
	    CTBody body=document.getBody();
	    if (!body.isSetSectPr()) {
	         body.addNewSectPr();
	    }
	    CTSectPr section=body.getSectPr();
	    if(!section.isSetPgSz()) {
	        section.addNewPgSz();
	    }
	    CTPageSz pageSize=section.getPgSz();
	    if(orientation.equals("landscape")){
	        pageSize.setOrient(STPageOrientation.LANDSCAPE);
	        pageSize.setW(BigInteger.valueOf(842 * 20));
	        pageSize.setH(BigInteger.valueOf(595 * 20));
	    }
	    else{
	        pageSize.setOrient(STPageOrientation.PORTRAIT);
	        pageSize.setH(BigInteger.valueOf(842 * 20));
	        pageSize.setW(BigInteger.valueOf(595 * 20));
	    }
	}

}
