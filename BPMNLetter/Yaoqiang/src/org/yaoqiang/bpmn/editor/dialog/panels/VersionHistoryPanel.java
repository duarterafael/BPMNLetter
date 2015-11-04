package org.yaoqiang.bpmn.editor.dialog.panels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.TextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.util.mxResources;

/**
 * VersionHistoryPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class VersionHistoryPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel idPanel;
	protected XMLTextPanel namePanel;
	protected TextPanel createDatePanel;
	protected TextPanel modDatePanel;

	protected TextPanel currentPanel;
	protected JSONComboPanel revisionPanel;
	protected TextPanel authorPanel;
	protected XMLMultiLineTextPanel commentPanel;

	public VersionHistoryPanel(final PanelContainer pc, final Definitions owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel definitionsPanel = new JPanel();
		definitionsPanel.setLayout(new BoxLayout(definitionsPanel, BoxLayout.X_AXIS));
		definitionsPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("definitions")));

		idPanel = new XMLTextPanel(pc, owner.get("id"), false);
		namePanel = new XMLTextPanel(pc, owner.get("name"), false);

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.Y_AXIS));
		idNamePanel.add(idPanel);
		idNamePanel.add(namePanel);
		definitionsPanel.add(idNamePanel);

		List<JSONObject> revisions = new ArrayList<JSONObject>();
		JSONObject item;
		String createTime = null;
		String modTime = null;
		try {
			PreparedStatement ps = BPMNEditor.dbConn
					.prepareStatement("SELECT ID,REVISION,CREATE_TIME,HEAD,AUTHOR,COMMENT FROM YAO_DEFINITIONS WHERE DEF_ID = ? ORDER BY REVISION DESC");
			ps.setString(1, owner.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				item = new JSONObject();
				item.putOnce("id", rs.getInt(1));
				item.putOnce("revision", rs.getInt(2));
				String date = Constants.DF.format(rs.getTimestamp(3));
				item.putOnce("modDate", date);
				if (rs.getInt(2) == 1) {
					createTime = date;
				}
				if (rs.getInt(4) == 1) {
					modTime = date;
				}
				item.putOnce("author", rs.getString(5));
				item.putOnce("comment", rs.getString(6));
				revisions.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		createDatePanel = new TextPanel(pc, null, "createDate", createTime, false);
		modDatePanel = new TextPanel(pc, null, "modDate", modTime, false);

		JPanel datePanel = new JPanel();
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
		datePanel.add(createDatePanel);
		datePanel.add(modDatePanel);
		definitionsPanel.add(datePanel);

		this.add(definitionsPanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("revision");
		columnsToShow.add("modDate");
		columnsToShow.add("author");
		columnsToShow.add("comment");
		this.add(new JSONTablePanel(panelContainer, BPMNEditor.getInstance(), "versionHistory", null, columnsToShow, revisions, 550, 150, false, false));

	}

	public void saveObjects() {

	}

}
