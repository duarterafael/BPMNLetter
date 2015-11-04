package org.yaoqiang.bpmn.editor.dialog.tree;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;

/**
 * OrganizationTreeCellRenderer
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -5145806787711893385L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		OrganizationTreeNode node = (OrganizationTreeNode) value;
		Object userObj = node.getUserObject();
		ImageIcon icon = null;
		if (node.getGraph().isOrganizationalPerson(userObj)) {
			icon = new ImageIcon(BPMNEditorConstants.USER_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (node.getGraph().isOrganizationalGroup(userObj)) {
			icon = new ImageIcon(BPMNEditorConstants.GROUP_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (node.getGraph().isOrganizationRoot(userObj)) {
			icon = new ImageIcon(BPMNEditorConstants.LDAP_ROOT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (node.getGraph().isOrganizationalUnit(userObj)) {
			icon = new ImageIcon(BPMNEditorConstants.OU_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else if (node.getGraph().isOrganizationalRole(userObj)) {
			icon = new ImageIcon(BPMNEditorConstants.ROLE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} else {
			icon = new ImageIcon(BPMNEditorConstants.COMPANY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));			
		}
		setIcon(icon);
		
		return this;
	}

}
