package org.yaoqiang.bpmn.editor.dialog.ldaptree;

import java.util.Vector;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * LdapTreeNode
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapTreeNode {

	protected LdapTreeNode parent;

	protected String name;
	protected transient Attributes userObject;

	protected Vector<LdapTreeNode> children = new Vector<LdapTreeNode>();

	public LdapTreeNode(String name, Attributes userObject) {
		this.name = name;
		this.userObject = userObject;
	}

	public LdapTreeNode getChildAt(int i) {
		return children.elementAt(i);
	}

	public int getIndex(LdapTreeNode child) {
		return children.indexOf(child);
	}

	public int getChildCount() {
		return children.size();
	}

	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	public boolean isPerson() {
		if (userObject == null) {
			return false;
		}
		Attribute attr = userObject.get("objectclass");
		if (attr.contains("inetOrgPerson") || attr.contains("organizationalPerson") || attr.contains("person")) {
			return true;
		} else {
			return false;
		}
	}

	public void addChild(LdapTreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public LdapTreeNode getParent() {
		return parent;
	}

	public void setParent(LdapTreeNode parent) {
		this.parent = parent;
	}

	public Attributes getUserObject() {
		return userObject;
	}

	public LdapTreeNode[] getPath() {
		return getPathToRoot(this, 0);
	}

	protected LdapTreeNode[] getPathToRoot(LdapTreeNode node, int depth) {
		LdapTreeNode[] retNodes;

		if (node == null) {
			if (depth == 0)
				return null;
			else
				retNodes = new LdapTreeNode[depth];
		} else {
			depth++;
			retNodes = getPathToRoot(node.getParent(), depth);
			retNodes[retNodes.length - depth] = node;
		}
		return retNodes;
	}

	public String toString() {
		return name;
	}
}
