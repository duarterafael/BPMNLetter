package org.yaoqiang.bpmn.editor.dialog.ldaptree;

import java.awt.Component;
import java.awt.Image;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;

/**
 * LdapTreeCellRenderer
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -5145806787711893385L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		LdapTreeNode node = (LdapTreeNode) value;
		Attributes userObj = node.getUserObject();
		if (userObj == null) {
			return this;
		}
		Attribute attr = userObj.get("objectclass");
		
		ImageIcon icon = null;
		if (attr.contains("inetOrgPerson") || attr.contains("organizationalPerson") || attr.contains("person")) {
			icon = new ImageIcon(BPMNEditorConstants.USER_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (attr.contains("groupOfNames")) {
			icon = new ImageIcon(BPMNEditorConstants.GROUP_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (attr.contains("organization")) {
			if (node.getParent() == null) {
				icon = new ImageIcon(BPMNEditorConstants.LDAP_ROOT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			} else {
				icon = new ImageIcon(BPMNEditorConstants.COMPANY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			}
		} else if (attr.contains("organizationalRole")) {
			icon = new ImageIcon(BPMNEditorConstants.ROLE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else {
			icon = new ImageIcon(BPMNEditorConstants.OU_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		}
		setIcon(icon);

		return this;
	}

}
