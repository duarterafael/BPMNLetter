package org.yaoqiang.bpmn.editor.dialog.ldaptree;

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * LdapTreeModel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapTreeModel implements TreeModel {

	private LdapTreeNode root;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	public LdapTreeModel(LdapTreeNode root) {
		this.root = root;
	}

	public void setRoot(LdapTreeNode root) {
		this.root = root;
	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		return ((LdapTreeNode) parent).getChildAt(index);
	}

	public int getChildCount(Object parent) {
		return ((LdapTreeNode) parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		return ((LdapTreeNode) parent).getIndex((LdapTreeNode) child);
	}

	public boolean isLeaf(Object node) {
		return ((LdapTreeNode) node).isLeaf();
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
