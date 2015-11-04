package org.yaoqiang.bpmn.editor.dialog.tree;

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * OrganizationTreeModel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationTreeModel implements TreeModel {

	private OrganizationTreeNode root;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	public OrganizationTreeModel(OrganizationTreeNode root) {
		this.root = root;
	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		return ((OrganizationTreeNode) parent).getChildAt(index);
	}

	public int getChildCount(Object parent) {
		return ((OrganizationTreeNode) parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		return ((OrganizationTreeNode) parent).getIndex((OrganizationTreeNode) child);
	}

	public boolean isLeaf(Object node) {
		return ((OrganizationTreeNode) node).isLeaf();
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

}
