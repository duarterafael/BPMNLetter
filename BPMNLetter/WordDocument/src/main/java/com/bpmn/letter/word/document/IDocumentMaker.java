package com.bpmn.letter.word.document;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IDocumentMaker {
	
	
		
	public void insertCapa(String content);
	public void insertText(String content);
	public void insertParagraph(String content) throws IOException;
	public void insertImage(String imagePaht) throws FileNotFoundException;
	public void finishDocument() throws IOException;
	
}
