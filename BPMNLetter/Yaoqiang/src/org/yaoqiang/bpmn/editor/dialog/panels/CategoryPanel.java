package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.artifacts.Category;

/**
 * CategoryPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CategoryPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;

	public CategoryPanel(PanelContainer pc, Category owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idNamePanel.add(Box.createHorizontalGlue());
		idNamePanel.add(namePanel);
		this.add(idNamePanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("value");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getCategoryValues(), "", "categoryValues", columnsToShow, owner.getCategoryValueList(), 350, 150, true,
				false));
	}

	public void saveObjects() {
		namePanel.saveObjects();
		super.saveObjects();
	}

}
