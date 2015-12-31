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
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
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
	private static File file;
	private WordDocumentHelper (String documentPath) throws FileNotFoundException{
		//Blank Document
		document= new XWPFDocument(); 
		//Write the Document in file system
	}

	public static WordDocumentHelper getInstance(String documentPath) throws FileNotFoundException{
		if(instance == null){
			instance = new WordDocumentHelper(documentPath);
		}
		file = new File(documentPath);
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
			document.createParagraph().createRun().addPicture(image, XWPFDocument.PICTURE_TYPE_JPEG, "my pic", Units.toEMU(450), Units.toEMU(300));
			XWPFParagraph paragraph = document.createParagraph();
			XWPFRun run=paragraph.createRun();
			run.addCarriageReturn();                 //separate previous text from break
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void finishDocument() throws IOException{
		document.write(new FileOutputStream(file));
	}

	public void insertCapa(String content) {
		//create Paragraph
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run=paragraph.createRun();
		for (int i = 0; i < 10; i++) {
			run.addBreak();
		}
		run.setBold(true);
		run.setFontSize(24);
		run.setText(content);
		run.addCarriageReturn();                 //separate previous text from break
		run.addBreak(BreakType.PAGE);
		run.addBreak(BreakType.TEXT_WRAPPING); 

	}
	
	public void insertText(String content) {
		//create Paragraph
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		XWPFRun run=paragraph.createRun();
//		 if (content.contains("\n")) {
//             String[] lines = content.split("\n");
//             run.setText(lines[0], 0); // set first line into XWPFRun
//             for(int i=1;i<lines.length;i++){
//                 // add break and insert new text
//                 run.addBreak();
//                 run.setText(lines[i]);
//             }
//         } else {
//             run.setText(content, 0);
//         }
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
