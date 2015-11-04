package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.CheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.ComboPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.RadioPanel;
import org.yaoqiang.bpmn.editor.dialog.SpinnerPanel;
import org.yaoqiang.bpmn.editor.dialog.TextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.swing.MenuBar;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.graph.swing.Ruler;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;

/**
 * PreferencesPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class PreferencesPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected CheckboxPanel outlinePanel;
	protected CheckboxPanel floatingOutlinePanel;
	protected CheckboxPanel rulerPanel;
	protected CheckboxPanel showGridPanel;
	protected SpinnerPanel gridSizePanel;
	protected ComboPanel gridStylePanel;
	protected JLabel gridColorPanel;
	protected CheckboxPanel statusBarPanel;
	protected CheckboxPanel patternsPanel;
	protected CheckboxPanel auxiliaryPanel;
	protected CheckboxPanel warningPanel;
	protected CheckboxPanel conditionPanel;
	protected SpinnerPanel colsPanel;

	protected ComboPanel pageSizePanel;
	protected TextPanel pageWidthPanel;
	protected TextPanel pageHeightPanel;
	protected RadioPanel pageOrientationPanel;
	protected SpinnerPanel pageNumVPanel;
	protected SpinnerPanel pageNumHPanel;

	protected RadioPanel exportResolutionPanel;

	protected RadioPanel layoutDirPanel;
	protected SpinnerPanel nodeDistPanel;
	protected SpinnerPanel levelDistPanel;

	protected CheckboxPanel connectableEdgePanel;
	protected CheckboxPanel autoMergeEdgesPanel;
	protected CheckboxPanel labelWrapPanel;
	protected CheckboxPanel snapToGridPanel;
	protected CheckboxPanel showToolTipsPanel;

	protected boolean showRuler;
	protected boolean showStatusBar;
	protected boolean showOutline;
	protected boolean showFloatingOutline;

	public PreferencesPanel(final PanelContainer pc, BaseElement owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel comPanel = new JPanel();
		comPanel.setLayout(new BoxLayout(comPanel, BoxLayout.Y_AXIS));
		comPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("components")));

		outlinePanel = new CheckboxPanel(pc, owner, "showOutline");
		showOutline = outlinePanel.isSelected();
		outlinePanel.addActionListener(new AbstractAction("showOutline") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (outlinePanel.isSelected()) {
					floatingOutlinePanel.setEnabled(true);
				} else {
					floatingOutlinePanel.setEnabled(false);
					floatingOutlinePanel.setSelected(false);
				}
			}
		});
		floatingOutlinePanel = new CheckboxPanel(pc, owner, "showFloatingOutline");
		floatingOutlinePanel.setSelected(Constants.SETTINGS.getProperty("showFloatingOutline", Constants.OS.startsWith("Windows") ? "1" : "0").equals("1"));
		if (outlinePanel.isSelected()) {
			floatingOutlinePanel.setEnabled(true);
		} else {
			floatingOutlinePanel.setEnabled(false);
			floatingOutlinePanel.setSelected(false);
		}
		showFloatingOutline = floatingOutlinePanel.isSelected();

		patternsPanel = new CheckboxPanel(pc, owner, "showPatterns");
		JPanel r1Panel = new JPanel();
		r1Panel.setLayout(new BoxLayout(r1Panel, BoxLayout.X_AXIS));
		r1Panel.add(outlinePanel);
		r1Panel.add(floatingOutlinePanel);
		r1Panel.add(patternsPanel);
		comPanel.add(r1Panel);

		rulerPanel = new CheckboxPanel(pc, owner, "showRulers");
		showRuler = rulerPanel.isSelected();
		statusBarPanel = new CheckboxPanel(pc, owner, "showStatusBar");
		showStatusBar = statusBarPanel.isSelected();

		colsPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("paletteCols", "4")), 1, 8, 50);
		colsPanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});

		JPanel r2Panel = new JPanel();
		r2Panel.setLayout(new BoxLayout(r2Panel, BoxLayout.X_AXIS));
		r2Panel.add(rulerPanel);
		r2Panel.add(statusBarPanel);
		r2Panel.add(Box.createHorizontalStrut(33));
		r2Panel.add(new JLabel(mxResources.get("paletteCols") + ":"));
		r2Panel.add(colsPanel);
		r2Panel.add(Box.createHorizontalStrut(17));
		comPanel.add(r2Panel);

		this.add(comPanel);

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
		gridPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("grid")));

		showGridPanel = new CheckboxPanel(pc, owner, "showGrid");
		snapToGridPanel = new CheckboxPanel(pc, owner, "snapToGrid", false);
		gridColorPanel = new JLabel();
		final Color initialColor = mxUtils.parseColor(Constants.SETTINGS.getProperty("gridColor", "#c0c0c0"));
		gridColorPanel.setOpaque(true);
		gridColorPanel.setBackground(initialColor);
		gridColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		Dimension textDim = new Dimension(26, 26);
		gridColorPanel.setMinimumSize(new Dimension(textDim));
		gridColorPanel.setMaximumSize(new Dimension(textDim));
		gridColorPanel.setPreferredSize(new Dimension(textDim));
		gridColorPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(getGraphComponent(), mxResources.get("GridColor"), initialColor);
				if (newColor != null) {
					gridColorPanel.setBackground(newColor);
				}
			}
		});

		List<String> choices = new ArrayList<String>();
		choices.add(mxResources.get("dot"));
		choices.add(mxResources.get("cross"));
		choices.add(mxResources.get("line"));
		choices.add(mxResources.get("dashed"));
		gridStylePanel = new ComboPanel(pc, owner, "gridstyle", choices, false, false, true);
		gridStylePanel.getComboBox().setSelectedIndex(Integer.parseInt(Constants.SETTINGS.getProperty("gridstyle", "3")));

		JPanel gr1Panel = new JPanel();
		gr1Panel.setLayout(new BoxLayout(gr1Panel, BoxLayout.X_AXIS));
		gr1Panel.add(showGridPanel);
		gr1Panel.add(gridStylePanel);
		gr1Panel.add(Box.createHorizontalStrut(30));
		gr1Panel.add(new JLabel(mxResources.get("GridColor") + ": "));
		gr1Panel.add(gridColorPanel);
		gr1Panel.add(Box.createHorizontalStrut(11));

		gridPanel.add(gr1Panel);

		gridSizePanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("gridSize", "10")), 1, 99, 50);
		gridSizePanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});

		JPanel gr2Panel = new JPanel();
		gr2Panel.setLayout(new BoxLayout(gr2Panel, BoxLayout.X_AXIS));
		gr2Panel.add(snapToGridPanel);
		gr2Panel.add(new JLabel(mxResources.get("GridSize") + ":"));
		gr2Panel.add(gridSizePanel);
		gr2Panel.add(Box.createHorizontalStrut(225));
		gridPanel.add(gr2Panel);

		this.add(gridPanel);

		choices = new ArrayList<String>();
		choices.add("Letter");
		choices.add("Legal");
		choices.add("A3");
		choices.add("A4");
		choices.add("A5");
		choices.add("Custom");
		pageSizePanel = new ComboPanel(pc, owner, "pageSize", choices, false, false, true);
		pageSizePanel.getComboBox().setSelectedIndex(Integer.parseInt(Constants.SETTINGS.getProperty("pageSize", "3")));
		pageSizePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = pageSizePanel.getSelectedIndex();
				double width = 0;
				double height = 0;
				switch (index) {
				case 0:
					width = 11;
					height = 8.5;
					break;
				case 1:
					width = 14;
					height = 8.5;
					break;
				case 2:
					width = 16.5;
					height = 11.7;
					break;
				case 4:
					width = 8.3;
					height = 5.8;
					break;
				default:
					width = 11.7;
					height = 8.3;
					break;
				}
				pageWidthPanel.setText(String.valueOf(width * 72));
				pageHeightPanel.setText(String.valueOf(height * 72));

				if (index == 5) {
					pageWidthPanel.setEnabled(true);
					pageHeightPanel.setEnabled(true);
				} else {
					pageWidthPanel.setEnabled(false);
					pageHeightPanel.setEnabled(false);
				}
			}
		});

		pageWidthPanel = new TextPanel(pc, owner, "pageWidth", false, Constants.SETTINGS.getProperty("pageWidth", String.valueOf(11.7 * 72)), 50, 26, false);
		pageHeightPanel = new TextPanel(pc, owner, "pageHeight", false, Constants.SETTINGS.getProperty("pageHeight", String.valueOf(8.3 * 72)), 50, 26, false);
		if (pageSizePanel.getSelectedIndex() == 5) {
			pageWidthPanel.setEnabled(true);
			pageHeightPanel.setEnabled(true);
		}

		JPanel pageSetupRow1Panel = new JPanel();
		pageSetupRow1Panel.setLayout(new BoxLayout(pageSetupRow1Panel, BoxLayout.X_AXIS));
		pageSetupRow1Panel.add(pageSizePanel);
		pageSetupRow1Panel.add(pageWidthPanel);
		pageSetupRow1Panel.add(pageHeightPanel);

		choices = new ArrayList<String>();
		choices.add("landscape");
		choices.add("portrait");
		pageOrientationPanel = new RadioPanel(pc, owner, "orientation", choices, choices.get(Integer.parseInt(Constants.SETTINGS.getProperty("pageOrientation",
				"0"))));

		pageNumVPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumV", "1")), 1, 99, 50);
		pageNumVPanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});
		pageNumHPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumH", "1")), 1, 99, 50);
		pageNumHPanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});

		JPanel pvPanel = new JPanel();
		pvPanel.setLayout(new BoxLayout(pvPanel, BoxLayout.X_AXIS));
		pvPanel.add(new JLabel(mxResources.get("dirVertical") + ":"));
		pvPanel.add(pageNumVPanel);

		JPanel phPanel = new JPanel();
		phPanel.setLayout(new BoxLayout(phPanel, BoxLayout.X_AXIS));
		phPanel.add(new JLabel(mxResources.get("dirHorizontal") + ":"));
		phPanel.add(pageNumHPanel);

		JPanel pageNumPanel = new JPanel();
		pageNumPanel.setLayout(new BoxLayout(pageNumPanel, BoxLayout.X_AXIS));
		pageNumPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("numberOfPages")));
		pageNumPanel.add(pvPanel);
		pageNumPanel.add(phPanel);

		JPanel pageSetupRow2Panel = new JPanel();
		pageSetupRow2Panel.setLayout(new BoxLayout(pageSetupRow2Panel, BoxLayout.X_AXIS));

		pageSetupRow2Panel.add(pageOrientationPanel);
		pageSetupRow2Panel.add(pageNumPanel);

		JPanel pageSetupPanel = new JPanel();
		pageSetupPanel.setLayout(new BoxLayout(pageSetupPanel, BoxLayout.Y_AXIS));
		pageSetupPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("pagesetup")));
		pageSetupPanel.add(pageSetupRow1Panel);
		pageSetupPanel.add(pageSetupRow2Panel);

		this.add(pageSetupPanel);

		choices = new ArrayList<String>();
		choices.add("actualSize");
		choices.add("currentScale");
		exportResolutionPanel = new RadioPanel(pc, owner, "exportImageResolution", choices, choices.get(Integer.parseInt(Constants.SETTINGS.getProperty(
				"exportImageResolution", "0"))));
		this.add(exportResolutionPanel);

		JPanel autolayoutPanel = new JPanel();
		autolayoutPanel.setLayout(new BoxLayout(autolayoutPanel, BoxLayout.X_AXIS));
		autolayoutPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("autolayout")));

		choices = new ArrayList<String>();
		choices.add("dirVertical");
		choices.add("dirHorizontal");
		layoutDirPanel = new RadioPanel(pc, owner, "orientation", choices, choices.get(Integer.parseInt(Constants.SETTINGS.getProperty("orientation", "1"))));

		nodeDistPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("nodeDistance", "20")), 5, Integer.MAX_VALUE, 50);
		nodeDistPanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});
		levelDistPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("levelDistance", "40")), 5, Integer.MAX_VALUE, 50);
		levelDistPanel.getSpinner().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		});

		JPanel a1Panel = new JPanel();
		a1Panel.setLayout(new BoxLayout(a1Panel, BoxLayout.X_AXIS));
		a1Panel.add(new JLabel(mxResources.get("nodeDistance") + ":"));
		a1Panel.add(nodeDistPanel);

		JPanel a2Panel = new JPanel();
		a2Panel.setLayout(new BoxLayout(a2Panel, BoxLayout.X_AXIS));
		a2Panel.add(new JLabel(mxResources.get("levelDistance") + ":"));
		a2Panel.add(levelDistPanel);

		JPanel distPanel = new JPanel();
		distPanel.setLayout(new BoxLayout(distPanel, BoxLayout.Y_AXIS));
		distPanel.add(a1Panel);
		distPanel.add(a2Panel);

		autolayoutPanel.add(Box.createHorizontalStrut(5));
		autolayoutPanel.add(distPanel);
		autolayoutPanel.add(layoutDirPanel);
		this.add(autolayoutPanel);

		JPanel miscPanel = new JPanel();
		miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
		miscPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("misc")));
		auxiliaryPanel = new CheckboxPanel(pc, owner, "showAuxiliary", false);
		warningPanel = new CheckboxPanel(pc, owner, "showWarning", false);
		conditionPanel = new CheckboxPanel(pc, owner, "showConditionExpression");
		connectableEdgePanel = new CheckboxPanel(pc, owner, "connectableEdge", false);
		labelWrapPanel = new CheckboxPanel(pc, owner, "labelWrap", false);
		autoMergeEdgesPanel = new CheckboxPanel(pc, owner, "autoMergeEdges");
		showToolTipsPanel = new CheckboxPanel(pc, owner, "showToolTips", false);

		JPanel row1Panel = new JPanel();
		row1Panel.setLayout(new BoxLayout(row1Panel, BoxLayout.X_AXIS));
		row1Panel.add(auxiliaryPanel);
		row1Panel.add(connectableEdgePanel);
		row1Panel.add(labelWrapPanel);
		JPanel row2Panel = new JPanel();
		row2Panel.setLayout(new BoxLayout(row2Panel, BoxLayout.X_AXIS));
		row2Panel.add(warningPanel);
		row2Panel.add(conditionPanel);
		row2Panel.add(autoMergeEdgesPanel);
		JPanel row3Panel = new JPanel();
		row3Panel.setLayout(new BoxLayout(row3Panel, BoxLayout.X_AXIS));
		row3Panel.add(showToolTipsPanel);
		miscPanel.add(row1Panel);
		miscPanel.add(row2Panel);
		miscPanel.add(row3Panel);
		this.add(miscPanel);

		pc.setModified(false);
	}

	public void saveObjects() {
		outlinePanel.saveObjects();
		MenuBar.getOutlineMenuItem().setSelected(outlinePanel.isSelected());
		floatingOutlinePanel.saveObjects();
		rulerPanel.saveObjects();
		MenuBar.getRulersMenuItem().setSelected(rulerPanel.isSelected());
		showGridPanel.saveObjects();
		MenuBar.getGridMenuItem().setSelected(showGridPanel.isSelected());
		statusBarPanel.saveObjects();
		patternsPanel.saveObjects();
		Constants.SETTINGS.put("paletteCols", colsPanel.getValue().toString());

		Constants.SETTINGS.put("pageOrientation", Integer.toString(pageOrientationPanel.getSelectedIndex()));
		Constants.SETTINGS.put("pageNumV", pageNumVPanel.getValue().toString());
		Constants.SETTINGS.put("pageNumH", pageNumHPanel.getValue().toString());
		Constants.SETTINGS.put("pageSize", Integer.toString(pageSizePanel.getSelectedIndex()));
		Constants.SETTINGS.put("pageWidth", pageWidthPanel.getText());
		Constants.SETTINGS.put("pageHeight", pageHeightPanel.getText());

		Constants.SETTINGS.put("exportImageResolution", Integer.toString(exportResolutionPanel.getSelectedIndex()));
		Constants.SETTINGS.put("orientation", Integer.toString(layoutDirPanel.getSelectedIndex()));
		Constants.SETTINGS.put("nodeDistance", nodeDistPanel.getValue().toString());
		Constants.SETTINGS.put("levelDistance", levelDistPanel.getValue().toString());

		Constants.SETTINGS.put("gridSize", gridSizePanel.getValue().toString());
		Constants.SETTINGS.put("gridstyle", Integer.toString(gridStylePanel.getSelectedIndex()));
		Constants.SETTINGS.put("gridColor", "#" + mxUtils.getHexColorString(gridColorPanel.getBackground()).substring(2));
		getGraphComponent().setGridColor(gridColorPanel.getBackground());
		getGraphComponent().setGridStyle(gridStylePanel.getSelectedIndex());
		getGraphComponent().getGraph().setGridSize(Integer.parseInt(gridSizePanel.getValue().toString()));

		auxiliaryPanel.saveObjects();
		MenuBar.getAuxiliaryMenuItem().setSelected(auxiliaryPanel.isSelected());
		warningPanel.saveObjects();
		MenuBar.getWarningMenuItem().setSelected(warningPanel.isSelected());

		conditionPanel.saveObjects();
		connectableEdgePanel.saveObjects();
		autoMergeEdgesPanel.saveObjects();
		labelWrapPanel.saveObjects();
		snapToGridPanel.saveObjects();
		showToolTipsPanel.saveObjects();
		Utils.saveConfigureFile();

		getGraphComponent().setToolTips(showToolTipsPanel.isSelected());
		getGraphComponent().setGridVisible(showGridPanel.isSelected());
		getGraphComponent().getGraph().setGridEnabled(snapToGridPanel.isSelected());
		getGraphComponent().getGraph().setConnectableEdges(connectableEdgePanel.isSelected());
		BPMNEditor.getInstance().getOrgGraphComponent().setGridVisible(showGridPanel.isSelected());
		BPMNEditor.getInstance().getOrgGraphComponent().getGraph().setGridEnabled(snapToGridPanel.isSelected());

		if (showRuler != rulerPanel.isSelected()) {
			if (showRuler) {
				getGraphComponent().setColumnHeader(null);
				getGraphComponent().setRowHeader(null);
			} else {
				getGraphComponent().setColumnHeaderView(new Ruler(getGraphComponent(), Ruler.ORIENTATION_HORIZONTAL));
				getGraphComponent().setRowHeaderView(new Ruler(getGraphComponent(), Ruler.ORIENTATION_VERTICAL));
			}
		}

		if (showStatusBar != statusBarPanel.isSelected()) {
			if (showStatusBar) {
				BPMNEditor.getInstance().getLayout().removeLayoutComponent(BPMNEditor.getInstance().getStatusBar());
			} else {
				BPMNEditor.getInstance().add(BorderLayout.SOUTH, BPMNEditor.getInstance().getStatusBar());
			}
		}

		JDialog outline = BPMNEditor.getInstance().getGraphOverviewWindow();
		if (showOutline != outlinePanel.isSelected()) {
			if (showOutline) {
				if (outline != null) {
					BPMNEditor.getInstance().removeGraphOverviewWindow();
				} else {
					JSplitPane outter = BPMNEditor.getInstance().getMainPane();
					if (outter.getLeftComponent() instanceof JSplitPane) {
						JSplitPane leftPane = (JSplitPane) outter.getLeftComponent();
						leftPane.setDividerLocation(0);
					}
				}
			} else {
				if (floatingOutlinePanel.isSelected()) {
					JSplitPane outter = BPMNEditor.getInstance().getMainPane();
					if (outter.getLeftComponent() instanceof JSplitPane) {
						outter.setLeftComponent(((JSplitPane) outter.getLeftComponent()).getRightComponent());
					}
					BPMNEditor.getInstance().createGraphOverviewWindow((Frame) SwingUtilities.windowForComponent(BPMNEditor.getInstance()),
							BPMNEditor.getInstance().getGraphOverviewComponent());
				} else {
					if (outline != null) {
						BPMNEditor.getInstance().removeGraphOverviewWindow();
					} else {
						JSplitPane outter = BPMNEditor.getInstance().getMainPane();
						if (outter.getLeftComponent() instanceof JSplitPane) {
							JSplitPane leftPane = (JSplitPane) outter.getLeftComponent();
							leftPane.setDividerLocation(200);
						} else {
							JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, BPMNEditor.getInstance().getGraphOverviewComponent(), BPMNEditor
									.getInstance().getPalettesPane());
							BPMNEditor.getInstance().getMainPane().setLeftComponent(leftPane);
							leftPane.setDividerLocation(200);
							leftPane.setDividerSize(6);
							leftPane.setBorder(null);
						}
					}
				}
			}
		} else if (showFloatingOutline != floatingOutlinePanel.isSelected()) {
			if (showFloatingOutline) {
				if (outline != null) {
					BPMNEditor.getInstance().removeGraphOverviewWindow();
				}
				JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, BPMNEditor.getInstance().getGraphOverviewComponent(), BPMNEditor.getInstance()
						.getPalettesPane());
				BPMNEditor.getInstance().getMainPane().setLeftComponent(leftPane);
				leftPane.setDividerLocation(200);
				leftPane.setDividerSize(6);
				leftPane.setBorder(null);
			} else {
				JSplitPane outter = BPMNEditor.getInstance().getMainPane();
				if (outter.getLeftComponent() instanceof JSplitPane) {
					outter.setLeftComponent(((JSplitPane) outter.getLeftComponent()).getRightComponent());
				}
				BPMNEditor.getInstance().createGraphOverviewWindow((Frame) SwingUtilities.windowForComponent(BPMNEditor.getInstance()),
						BPMNEditor.getInstance().getGraphOverviewComponent());
			}
		}

		getGraphComponent().repaint();
	}

}
