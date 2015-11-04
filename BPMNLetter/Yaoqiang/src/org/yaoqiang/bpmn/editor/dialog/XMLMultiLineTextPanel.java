package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Dimension;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;

import poli.mestrado.parser.uml2use.GenereteSerializable;
import poli.mestrado.parser.uml2use.UmlFileManager;
import poli.mestrado.parser.uml2use.tag.AssociationClassTag;
import poli.mestrado.parser.uml2use.tag.AttributeTag;
import poli.mestrado.parser.uml2use.tag.ClassTag;
import poli.mestrado.parser.uml2use.tag.EnumerationLiteralTag;
import poli.mestrado.parser.uml2use.tag.EnumerationTag;
import poli.mestrado.parser.uml2use.tag.ModelTag;
import poli.mestrado.parser.util.Constants;

import com.mxgraph.util.mxResources;


/**
 * XMLMultiLineTextPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLMultiLineTextPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected RSyntaxTextArea jta;

	public XMLMultiLineTextPanel(PanelContainer pc, XMLElement owner, String title, int width, int height) {
		this(pc, owner, title, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, width, height);
	}

	public XMLMultiLineTextPanel(PanelContainer pc, XMLElement owner, String title, String style, int width, int height) {
		super(pc, owner);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if (title != null && title.length() != 0) {
			this.setBorder(BorderFactory.createTitledBorder(mxResources.get(title)));
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}

		Dimension dim = new Dimension(width, height);
		jta = new RSyntaxTextArea();
		jta.setEditable(true);
		jta.setSyntaxEditingStyle(style);
		jta.setCodeFoldingEnabled(true);
		jta.setAntiAliasingEnabled(true);
		if (owner != null) {
			jta.setText(owner.toValue());
		}
		jta.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				getPanelContainer().panelChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
		});

		//		-----------------------------------------------------------------------------
		jta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		jta.setCodeFoldingEnabled(true);
		jta.setAntiAliasingEnabled(true);

		CompletionProvider provider = createCompletionProvider();

		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(jta);


		//		-----------------------------------------------------------------------------




		RTextScrollPane sp = new RTextScrollPane(jta);
		sp.setFoldIndicatorEnabled(true);
		sp.setPreferredSize(dim);
		this.add(sp);
	}

	private CompletionProvider createCompletionProvider() {


		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		
		String path =  	UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath()
				.substring(0, UmlFileManager.getInstance().getExportXmiFile().
				getAbsolutePath().lastIndexOf(File.separator)+1)+Constants.CLASS_DIAGRAM_SERIALIZABEL_FILE_NAME;
//		System.out.println("===========>"+path);

		ModelTag model = null;
		try {
			model = (ModelTag) GenereteSerializable.retryveModel(path);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkedList<String> lst = new LinkedList<String>();
		if(model != null){
			for (ClassTag classTag : model.getClassList()) {
					lst.add(classTag.getName());
					for(AttributeTag attTag : classTag.getAttributesList()){
						lst.add(attTag.getName());
					}
			}
			for (EnumerationTag enumTag : model.getEnumList()) {
				lst.add(enumTag.getName());
				for (EnumerationLiteralTag literalTag : enumTag.getEnumerationLiteralList()) {
					lst.add(literalTag.getName());
				}
			}
			
		}
		
		// Add completions for all Java keywords. A BasicCompletion is just
		// a straightforward word completion.
		for (String string : lst) {
			provider.addCompletion(new BasicCompletion(provider, string));
		}
		
		 provider.addCompletion(new BasicCompletion(provider, "criar"));
		 provider.addCompletion(new BasicCompletion(provider, "deletar"));
		 provider.addCompletion(new BasicCompletion(provider, "atribuir"));
		 provider.addCompletion(new BasicCompletion(provider, "atribuir_aleatorio"));
		 provider.addCompletion(new BasicCompletion(provider, "associar"));
		 provider.addCompletion(new BasicCompletion(provider, "desassociar"));

		 provider.addCompletion(new ShorthandCompletion(provider, "criar", "<idObj>:criar(<classe>);", "<idObj>:criar(<classe>);"));
		 provider.addCompletion(new ShorthandCompletion(provider, "criar", "<idObj>:criar(<nome_classe>)entre(<idObj>,<idObjj>);", "<idObj>:criar(<nome_classe>)entre(<idObj>,<idObjj>);"));
		 provider.addCompletion(new ShorthandCompletion(provider, "deletar", "<idObj>:deletar;", "<idObj>:deletar;"));
		 provider.addCompletion(new ShorthandCompletion(provider, "atribuir", "<idObj>:atribuir(<attibuto>,<valor>);", "<idObj>:atribuir(<attibuto>,<valor>);"));
		 provider.addCompletion(new ShorthandCompletion(provider, "atribuir_aleatorio", "<idObj>:atribuir_aleatorio(<nome_classe>);",  "<idObj>:atribuir_aleatorio(<nome_classe>);"));
		 provider.addCompletion(new ShorthandCompletion(provider, "associar", "<idObj>:associar(<idObj>,<idObj>);",  "<idObj>:associar(<idObj>,<idObj>);"));
		 provider.addCompletion(new ShorthandCompletion(provider, "desassociar", "<idObj>:desassociar(<idObj>,<idObj>);",  "<idObj>:desassociar(<idObj>,<idObj>);"));
		 
		

		return provider;

	}



	public void saveObjects() {
		getOwner().setValue(getText());
		if (owner instanceof XMLExtensionElement && getOwner().toValue().length() == 0) {
			((XMLExtensionElement) getOwner().getParent()).removeChildElement((XMLExtensionElement) owner);
		}
	}

	public String getText() {
		return jta.getText().trim();
	}

	public RSyntaxTextArea getTextArea() {
		return jta;
	}

	public void setText(String text) {
		jta.setText(text);
	}

	public void appendText(String txt) {
		int cp = jta.getCaretPosition();
		String ct = jta.getText();
		String text = ct.substring(0, cp) + txt + ct.substring(cp);
		jta.setText(text);
		jta.setCaretPosition(cp + txt.length());
		jta.requestFocus();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jta.setEditable(b);
		jta.setEnabled(b);
	}

}
