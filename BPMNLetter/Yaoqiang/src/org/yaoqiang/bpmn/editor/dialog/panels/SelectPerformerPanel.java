package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Container;

import org.w3c.dom.Element;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.tree.OrganizationTreeNode;
import org.yaoqiang.bpmn.editor.dialog.tree.OrganizationTreePanel;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;

import com.mxgraph.model.mxCell;

/**
 * SelectPerformerPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SelectPerformerPanel extends OrganizationTreePanel {

	private static final long serialVersionUID = 3682352971432511756L;

	public SelectPerformerPanel(PanelContainer pc, BPMNEditor owner) {
		super(pc, owner, "performer");
	}

	public void saveObjects() {
		Container parent = getParentPanel();
		if (selectedPath != null && parent instanceof ResourceAssignmentPanel) {
			OrganizationTreeNode node = (OrganizationTreeNode) selectedPath.getLastPathComponent();
			BPMNGraph graph = BPMNEditor.getInstance().getOrgGraphComponent().getGraph();
			mxCell cell = ((OrganizationTreeNode) node).getUserObject();
			String performer = graph.convertValueToString(cell);
			Object value = cell.getValue();
			if (graph.isOrganizationalPerson(cell) && value instanceof Element && ((Element) value).getAttribute("uid").length() > 0) {
				performer = ((Element) value).getAttribute("uid");
			}
			((ResourceAssignmentPanel) parent).insertPerformer(performer);
		}
	}
}
