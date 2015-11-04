package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.service.Operation;

import com.mxgraph.util.mxResources;

/**
 * OperationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OperationPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;
	protected XMLPanel implPanel;
	protected XMLComboPanel inMsgPanel;
	protected XMLComboPanel outMsgPanel;

	public OperationPanel(PanelContainer pc, Operation owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		implPanel = new XMLTextPanel(pc, owner.get("implementationRef"));
		inMsgPanel = new XMLComboPanel(pc, owner.get("inMessageRef"), null, BPMNModelUtils.getDefinitions(owner).getMessages(), false, false, true);
		outMsgPanel = new XMLComboPanel(pc, owner.get("outMessageRef"), null, BPMNModelUtils.getDefinitions(owner).getMessages(), true, false, true);

		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);
		this.add(implPanel);
		this.add(inMsgPanel);
		this.add(outMsgPanel);
		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("error");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getErrorRefs(), "", "errorRefs", columnsToShow, owner.getErrorRefList(), 300, 120, true, false));
	}

	public void saveObjects() {
		if (inMsgPanel.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(null, "Operation MUST have one input Message!", mxResources.get("Warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		namePanel.saveObjects();
		implPanel.saveObjects();
		inMsgPanel.saveObjects();
		outMsgPanel.saveObjects();
		super.saveObjects();
	}

}
