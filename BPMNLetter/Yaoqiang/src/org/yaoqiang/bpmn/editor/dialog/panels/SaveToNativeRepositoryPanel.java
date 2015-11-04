package org.yaoqiang.bpmn.editor.dialog.panels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.TextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxXmlUtils;

/**
 * SaveToNativeRepositoryPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SaveToNativeRepositoryPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected int revision;
	protected boolean update;

	protected XMLTextPanel idPanel;
	protected XMLTextPanel namePanel;
	protected TextPanel createDatePanel;
	protected TextPanel modDatePanel;
	protected TextPanel authorPanel;
	protected TextPanel revisionPanel;
	protected XMLMultiLineTextPanel commentPanel;

	public SaveToNativeRepositoryPanel(PanelContainer pc, Definitions owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel definitionsPanel = new JPanel();
		definitionsPanel.setLayout(new BoxLayout(definitionsPanel, BoxLayout.Y_AXIS));
		definitionsPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("definitions")));

		idPanel = new XMLTextPanel(pc, owner.get("id"), false);
		namePanel = new XMLTextPanel(pc, owner.get("name"));

		String createTime = null;
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT CREATE_TIME FROM YAO_DEFINITIONS WHERE REVISION = '1' AND DEF_ID = ?");
			ps.setString(1, idPanel.getText());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				update = true;
				createTime = Constants.DF.format(rs.getTimestamp(1));
				ps = BPMNEditor.dbConn.prepareStatement("SELECT MAX(REVISION) FROM YAO_DEFINITIONS WHERE DEF_ID = ?");
				ps.setString(1, idPanel.getText());
				rs = ps.executeQuery();
				if (rs.next()) {
					revision = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		revision++;

		modDatePanel = new TextPanel(pc, null, "modDate", Constants.DF.format(new Date()), false);
		createDatePanel = new TextPanel(pc, null, "createDate", createTime == null ? modDatePanel.getText() : createTime, false);

		definitionsPanel.add(idPanel);
		definitionsPanel.add(namePanel);
		definitionsPanel.add(createDatePanel);
		definitionsPanel.add(modDatePanel);

		JPanel versionPanel = new JPanel();
		versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.Y_AXIS));
		versionPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("version")));

		revisionPanel = new TextPanel(pc, null, "revision", false, String.valueOf(revision), 50, 27, false);
		authorPanel = new TextPanel(pc, null, "author", System.getProperty("user.name"), true);
		JPanel authorRevPanel = new JPanel();
		authorRevPanel.setLayout(new BoxLayout(authorRevPanel, BoxLayout.X_AXIS));
		authorRevPanel.add(revisionPanel);
		authorRevPanel.add(Box.createHorizontalGlue());
		authorRevPanel.add(authorPanel);

		commentPanel = new XMLMultiLineTextPanel(pc, owner, "comment", 300, 90);
		versionPanel.add(authorRevPanel);
		versionPanel.add(commentPanel);

		this.add(definitionsPanel);
		this.add(versionPanel);
	}

	public void saveObjects() {
		namePanel.saveObjects();
		BPMNEditor.getInstance().setModified(false);
		BPMNEditor.getInstance().setRevision(revision);
		try {
			PreparedStatement ps = BPMNEditor.dbConn
					.prepareStatement("INSERT INTO YAO_DEFINITIONS(DEF_ID,DEF_NAME,DEF_CONTENT,AUTHOR,COMMENT,CREATE_TIME,REVISION,HEAD) VALUES(?,?,?,?,?,?,?,?);");
			ps.setString(1, idPanel.getText());
			ps.setString(2, namePanel.getText());
			ps.setBytes(3, mxXmlUtils.getXml(new BPMNCodec(BPMNEditor.getGraph()).encode().getDocumentElement()).getBytes());
			ps.setString(4, authorPanel.getText());
			ps.setString(5, commentPanel.getText());
			ps.setString(6, modDatePanel.getText());
			ps.setInt(7, revision);
			ps.setInt(8, 1);
			ps.execute();
			if (update) {
				ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_DEFINITIONS SET HEAD = 0 WHERE REVISION = ? AND DEF_ID = ?");
				ps.setInt(1, revision - 1);
				ps.setString(2, idPanel.getText());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BPMNEditor.getInstance().refreshDefinitionsRepositoryPanel("native");
	}

}
