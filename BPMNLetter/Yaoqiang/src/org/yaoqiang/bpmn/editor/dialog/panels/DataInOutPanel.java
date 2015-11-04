package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.model.elements.activities.Activity;

/**
 * DataInOutPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataInOutPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	public DataInOutPanel(PanelContainer pc, Activity owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("type");
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("isCollection");
		columnsToShow.add("itemSubjectRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getIoSpecification(), "dataInputOutputs", null, columnsToShow, owner.getDataInOuts(), 500, 100, true, false));

		columnsToShow = new ArrayList<String>();
		columnsToShow.add("type");
		columnsToShow.add("id");
		columnsToShow.add("targetRef");
		columnsToShow.add("sourceRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner, "dataAssociations", "dataAssociations", columnsToShow, owner.getDataInOutAssociations(), 500, 100, true, false));

	}

	public void saveObjects() {

	}

}