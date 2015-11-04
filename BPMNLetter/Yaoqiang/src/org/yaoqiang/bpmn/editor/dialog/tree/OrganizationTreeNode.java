package org.yaoqiang.bpmn.editor.dialog.tree;

import java.util.Vector;

import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;

import com.mxgraph.model.mxCell;

/**
 * OrganizationTreeNode
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationTreeNode {

	protected BPMNGraph graph;

	protected mxCell userObject;

	protected Vector<OrganizationTreeNode> childOrgs = new Vector<OrganizationTreeNode>();

	public OrganizationTreeNode(mxCell userObject, BPMNGraph graph, String type) {
		this.graph = graph;
		this.userObject = userObject;
		BPMNEditorUtils.getChildOrganizations(graph, userObject, type, childOrgs);
	}

	public OrganizationTreeNode getChildAt(int i) {
		return childOrgs.elementAt(i);
	}

	public int getIndex(OrganizationTreeNode child) {
		return childOrgs.indexOf(child);
	}

	public int getChildCount() {
		return childOrgs.size();
	}

	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	public String getId() {
		if (userObject instanceof mxCell) {
			return userObject.getId();
		}
		return "";
	}

	public mxCell getUserObject() {
		return userObject;
	}

	public BPMNGraph getGraph() {
		return graph;
	}

	public String toString() {
		return graph.convertValueToString(userObject);
	}

}
