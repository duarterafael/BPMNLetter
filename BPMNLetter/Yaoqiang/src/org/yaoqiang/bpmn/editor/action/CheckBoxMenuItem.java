package org.yaoqiang.bpmn.editor.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.WarningDialog;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.swing.Ruler;
import org.yaoqiang.graph.util.Constants;

/**
 * CheckBoxMenuItem
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CheckBoxMenuItem extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 2794849079170361266L;

	public CheckBoxMenuItem(final BPMNEditor editor, String name, final String key) {
		super(name);
		String defaultValue = "1";
		if (key.equals("showAuxiliary") || key.equals("showWarning") || key.equals("enableSimulation")) {
			defaultValue = "0";
		}
		Constants.SETTINGS.put(key, Constants.SETTINGS.getProperty(key, defaultValue));
		setSelected(Constants.SETTINGS.getProperty(key).equals("1"));

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (key.equals("showWarning")) {
					if (Constants.SETTINGS.getProperty(key).equals("0") && Constants.SETTINGS.getProperty("warningPerformanceIssue", "1").equals("1")) {
						new WarningDialog("warningPerformanceIssue").setVisible(true);					
					}
					Constants.SETTINGS.put(key, Constants.SETTINGS.getProperty(key).equals("1") ? "0" : "1");
					editor.getGraphComponent().validateGraph();
				} else if (key.equals("enableSimulation")) {
					if (isSelected()) {
						((JPanel) editor.getMainPane().getRightComponent()).add(editor.getSimulationPane(), BorderLayout.EAST);
					} else {
						((JPanel) editor.getMainPane().getRightComponent()).remove(editor.getSimulationPane());
					}
					Constants.SETTINGS.put(key, Constants.SETTINGS.getProperty(key).equals("1") ? "0" : "1");
					editor.getMainPane().revalidate();
				} else if (key.equals("showGrid")) {
					editor.getGraphComponent().getGraph().setGridEnabled(isSelected());
					editor.getGraphComponent().setGridVisible(isSelected());
					editor.getGraphComponent().repaint();
					for (GraphComponent com : editor.getGraphComponents().values()) {
						com.setGridVisible(isSelected());
						com.repaint();
					}
					editor.getOrgGraphComponent().getGraph().setGridEnabled(isSelected());
					editor.getOrgGraphComponent().setGridVisible(isSelected());
					editor.getOrgGraphComponent().repaint();
				} else if (key.equals("showRulers")) {
					if (editor.getGraphComponent().getColumnHeader() != null) {
						editor.getGraphComponent().setColumnHeader(null);
						editor.getGraphComponent().setRowHeader(null);
						for (GraphComponent com : editor.getGraphComponents().values()) {
							com.setColumnHeader(null);
							com.setRowHeader(null);
						}
						editor.getOrgGraphComponent().setColumnHeader(null);
						editor.getOrgGraphComponent().setRowHeader(null);
					} else {
						editor.getGraphComponent().setColumnHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_HORIZONTAL));
						editor.getGraphComponent().setRowHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_VERTICAL));
						for (GraphComponent com : editor.getGraphComponents().values()) {
							com.setColumnHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_HORIZONTAL));
							com.setRowHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_VERTICAL));
						}
						editor.getOrgGraphComponent().setColumnHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_HORIZONTAL));
						editor.getOrgGraphComponent().setRowHeaderView(new Ruler(editor.getGraphComponent(), Ruler.ORIENTATION_VERTICAL));
					}
				} else if (key.equals("showAuxiliary")) {
					if (Constants.SETTINGS.getProperty(key).equals("0") && Constants.SETTINGS.getProperty("warningPerformanceIssue", "1").equals("1")) {
						new WarningDialog("warningPerformanceIssue").setVisible(true);					
					}
					Constants.SETTINGS.put(key, Constants.SETTINGS.getProperty(key).equals("1") ? "0" : "1");
				} else if (key.equals("showOutline")) {
					JDialog outline = editor.getGraphOverviewWindow();
					if (outline == null) {
						JSplitPane outter = editor.getMainPane();
						if (outter.getLeftComponent() instanceof JSplitPane) {
							JSplitPane leftPane = (JSplitPane) outter.getLeftComponent();
							int loc = leftPane.getDividerLocation();
							if (loc < 5) {
								leftPane.setDividerLocation(180);
							} else {
								leftPane.setDividerLocation(0);
							}
						} else {
							JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor.getGraphOverviewComponent(), editor.getPalettesPane());
							editor.getMainPane().setLeftComponent(leftPane);
							leftPane.setDividerLocation(180);
							leftPane.setDividerSize(6);
							leftPane.setBorder(null);
						}
					} else {
						outline.setVisible(!outline.isVisible());
					}
				}
			}
		});
	}

}
