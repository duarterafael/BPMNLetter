package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.core.service.Interface;

import com.mxgraph.util.mxResources;

/**
 * InterfacePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class InterfacePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel namePanel;

	protected XMLPanel implPanel;

	public InterfacePanel(PanelContainer pc, Interface owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		implPanel = new XMLTextPanel(pc, owner.get("implementationRef"));
		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idNamePanel.add(namePanel);
		idNamePanel.add(implPanel);
		this.add(idNamePanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("inMessageRef");
		columnsToShow.add("outMessageRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getOperations(), "", "operations", columnsToShow, owner.getOperationList(), 500, 120, true,
				false));
	}

	public void saveObjects() {
		if (namePanel.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(null, "Interface Name is required!", mxResources.get("Warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (((Interface)owner).getOperationList().size() == 0) {
			JOptionPane.showMessageDialog(null, "Interface MUST have at least one Operation!", mxResources.get("Warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		namePanel.saveObjects();
		implPanel.saveObjects();
		super.saveObjects();
	}

}
