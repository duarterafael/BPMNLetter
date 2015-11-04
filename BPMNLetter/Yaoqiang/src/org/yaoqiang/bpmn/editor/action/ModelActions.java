package org.yaoqiang.bpmn.editor.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.naming.directory.Attributes;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.BaseDialog;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.simulation.MainPanel;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.DefaultFileFilter;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.editor.util.LdapUtils;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelConstants;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.XMLCollection;
import org.yaoqiang.bpmn.model.elements.XMLComplexElement;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.LoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.MultiInstanceLoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.ReceiveTask;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.choreographyactivities.ChoreographyActivity;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElementsContainer;
import org.yaoqiang.bpmn.model.elements.core.common.FlowNode;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataObjectReference;
import org.yaoqiang.bpmn.model.elements.data.DataOutput;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.events.CatchEvent;
import org.yaoqiang.bpmn.model.elements.events.EndEvent;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.bpmn.model.elements.events.IntermediateCatchEvent;
import org.yaoqiang.bpmn.model.elements.events.IntermediateThrowEvent;
import org.yaoqiang.bpmn.model.elements.events.LinkEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.StartEvent;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;
import org.yaoqiang.bpmn.model.elements.gateways.Gateway;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.bpmn.model.elements.process.GlobalTask;
import org.yaoqiang.bpmn.model.elements.process.Lane;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.canvas.SvgCanvas;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.Constants;
import org.yaoqiang.graph.util.GraphUtils;

import poli.mestrado.document.management.DocumentHelper;
import poli.mestrado.parser.bpmn2use.ParserBPMNHelper;
import poli.mestrado.parser.bpmn2use.graph.Graph;
import poli.mestrado.parser.bpmn2use.graph.GraphHelper;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;
import poli.mestrado.parser.bpmn2use.tag.task.AbstractTaskElement;
import poli.mestrado.parser.uml2use.UmlFileManager;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.sun.beans.decoder.DocumentHandler;

/**
 * ModelActions
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ModelActions extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public static final String NEW = "new";
	public static final String OPEN = "open";
	public static final String OPEN_URL = "openURL";
	public static final String SAVE = "save";
	public static final String RELOAD = "reload";
	public static final String EXIT = "exit";

	public static final String EXPORT_USE = "exportUse";

	public static final String DEFINITIONS = "definitions";
	public static final String RESOURCES = "resources";
	public static final String NAMESPACES = "namespaces";
	public static final String CATEGORIES = "categories";
	public static final String DATASTORES = "dataStores";
	public static final String GLOBAL_TASKS = "globalTasks";
	public static final String IMPORTS = "imports";
	public static final String ITEM_DEFINITIONS = "itemDefinitions";
	public static final String MESSAGES = "messages";
	public static final String ERRORS = "errors";
	public static final String SIGNALS = "signals";
	public static final String ESCALATIONS = "escalations";
	public static final String INTERFACES = "interfaces";
	public static final String PARTNERS = "partners";
	public static final String DEF_EVENT_DEFINITIONS = "defEventDefinitions";

	public static final String CONDITION_EXPRESSION = "conditionExpression";
	public static final String DATA_STATE = "dataState";
	public static final String DOCUMENTATION = "documentation";
	public static final String EVENT_DEFINITION = "eventDefinition";
	public static final String EVENT_DEFINITIONS = "eventDefinitions";
	public static final String LOOP_CHARACTERISTICS = "loopCharacteristics";
	public static final String PROCESS_PROPERTIES = "processProperties";
	public static final String DATA_PROPERTIES = "dataProperties";
	public static final String DATA_OBJECTS = "dataobjects";
	public static final String DATA_INOUT = "dataInOut";
	public static final String RESOURCE_ASSIGNMENT = "resourceAssignment";

	public static final String ADDON = "addon";
	public static final String ELEMENT = "element";
	public static final String DIAGRAM_NAME = "diagramName";
	public static final String OPEN_DIAGRAM = "openDiagram";

	public static final String SAVE_TO_REPO = "saveToNativeRepo";
	public static final String VERSION_HISTORY = "versionHistory";

	public static final String DEPLOY = "deploy";
	public static final String ENGINE_CONNECTIONS = "engineConnections";

	public static final String CONFIG_SIMULATION = "configSimulation";
	public static final String CLOSE_SIMULATION = "closeSimulation";
	public static final String RUN_SIMULATION = "runSimulation";
	public static final String PAUSE_SIMULATION = "pauseSimulation";
	public static final String STOP_SIMULATION = "stopSimulation";
	public static final String SIMULATION_SPEED = "simulationSpeed";

	public static final String ORGANIZATIONS = "organizations";
	public static final String OPEN_ORGANIZATION = "openOrganization";
	public static final String SAVE_ORGANIZATION = "saveOrganization";
	public static final String ADD_ORGANIZATION = "addOrganization";
	public static final String EXPORT_ORGANIZATION = "exportOrganization";

	public static final String LDAP_CONNECTIONS = "ldapConnections";
	public static final String EXPORT_LDIF = "exportLdif";
	public static final String IMPORT_LDIF = "importLdif";
	public static final String CACHE_LDAP = "cacheLdap";

	public static final String USERS = "users";

	public static final String CHANGE_FLOW_ELEMENT_TYPE = "changeFlowElementType";
	public static final String CHANGE_ACTIVITY_LOOP_TYPE = "changeLoopType";
	public static final String CHANGE_ACTIVITY_COMPENSATION_TYPE = "changeCompensationType";
	public static final String CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE = "changeReceiveTaskInstantiateType";
	public static final String CHANGE_EVENT_TYPE = "changeEventType";
	public static final String CHANGE_DATA_COLLECTION_TYPE = "changeDataCollectionType";
	public static final String CHANGE_DATA_TYPE = "changeDataType";
	public static final String DEFAUT_SF = "defaultSequenceFlow";

	public static final String ELEMENT_STYLES = "elementStyles";
	public static final String PREFERENCES = "preferences";

	private String type = "";

	private String stringValue = "";

	private boolean booleanValue;

	private Object objectValue;

	public ModelActions(String type) {
		this.type = type;
	}

	public static ModelActions getAction() {
		return new ModelActions(ELEMENT);
	}

	public static ModelActions getSaveAction() {
		return new ModelActions(SAVE).setStringValue("0");
	}

	public static ModelActions getSaveAsAction() {
		return new ModelActions(SAVE).setStringValue("1");
	}

	public static ModelActions getSaveAsFragment() {
		return new ModelActions(SAVE).setStringValue("2");
	}

	public static ModelActions getSaveAsPNG() {
		return new ModelActions(SAVE).setStringValue("3");
	}

	public static ModelActions getSaveAsODT() {
		return new ModelActions(SAVE).setStringValue("4");
	}

	public static ModelActions getExportOrganizationAction() {
		return new ModelActions(SAVE).setStringValue("5");
	}

	public static ModelActions getExportUse() {
		return new ModelActions(EXPORT_USE);
	}

	public static ModelActions getOpenAction(String filepath) {
		return new ModelActions(OPEN).setStringValue(filepath);
	}

	public static ModelActions getOpenAction(Object object) {
		return new ModelActions(OPEN).setObjectValue(object);
	}

	public static ModelActions getOpenOrganizationAction(String orgId) {
		return new ModelActions(OPEN_ORGANIZATION).setStringValue(orgId);
	}

	public static ModelActions getSaveOrganizationAction() {
		return new ModelActions(SAVE_ORGANIZATION);
	}

	public static ModelActions getAddOrganizationAction(String orgType) {
		return new ModelActions(ADD_ORGANIZATION).setStringValue(orgType);
	}

	public static ModelActions getExportLdifAction() {
		return new ModelActions(EXPORT_LDIF);
	}

	public static ModelActions getImportLdifAction() {
		return new ModelActions(IMPORT_LDIF);
	}


	public static ModelActions getCacheLdapAction() {
		return new ModelActions(CACHE_LDAP);
	}

	public static ModelActions getAddonAction() {
		return new ModelActions(ADDON);
	}

	public static ModelActions getAddonAction(String name) {
		return new ModelActions(ADDON).setStringValue(name);
	}

	public static ModelActions getOpenDiagramAction(String diagramId) {
		return new ModelActions(OPEN_DIAGRAM).setStringValue(diagramId);
	}

	public static ModelActions getChangeFlowElementTypeAction(String type) {
		return new ModelActions(CHANGE_FLOW_ELEMENT_TYPE).setStringValue(type);
	}

	public static ModelActions getChangeActivityLoopTypeAction(String type, boolean isSequential) {
		return new ModelActions(CHANGE_ACTIVITY_LOOP_TYPE).setStringValue(type).setBooleanValue(isSequential);
	}

	public static ModelActions getChangeActivityCompensationTypeAction(boolean isForCompensation) {
		return new ModelActions(CHANGE_ACTIVITY_COMPENSATION_TYPE).setBooleanValue(isForCompensation);
	}

	public static ModelActions getChangeReceiveTaskInstantiateTypeAction(boolean instantiate) {
		return new ModelActions(CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE).setBooleanValue(instantiate);
	}

	public static ModelActions getChangeEventTypeAction(String type) {
		return new ModelActions(CHANGE_EVENT_TYPE).setStringValue(type);
	}

	public static ModelActions getChangeDataCollectionType(boolean isCollection) {
		return new ModelActions(CHANGE_DATA_COLLECTION_TYPE).setBooleanValue(isCollection);
	}

	public static ModelActions getChangeDataType(String type) {
		return new ModelActions(CHANGE_DATA_TYPE).setStringValue(type);
	}

	public static ModelActions getDefaultSequenceFlowAction(String edge) {
		return new ModelActions(DEFAUT_SF).setStringValue(edge);
	}

	public static ModelActions getAction(String type) {
		return new ModelActions(type);
	}

	public void actionPerformed(ActionEvent e) {
		final BPMNEditor editor = getEditor(e);
		BaseDialog dialog = editor.getDialog();
		BPMNGraphComponent graphComponent = editor.getGraphComponent();
		BPMNGraph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();

		mxCell cell = (mxCell) graph.getSelectionCell();
		if (graph.isChoreography(cell) || graph.isSubChoreography(cell)) {
			cell = GraphUtils.getChoreographyActivity(model, cell);
		}
		XMLElement el = null;
		if (cell != null && model.getValue(cell) instanceof XMLElement) {
			el = (XMLElement) model.getValue(cell);
		}

		Definitions bpmnModel = graph.getBpmnModel();

		if (NEW.equals(type)) {
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, mxResources.get("saveChanges"), mxResources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				model.clear();
				graph.clearBpmnModel();
				graph.getBpmnModel().setId("_" + System.currentTimeMillis());
				graph.getBpmnModel().setTargetNamespace(BPMNModelConstants.BPMN_TARGET_MODEL_NS + graph.getBpmnModel().getId());
				graph.getBpmnModel().getNamespaces().put(graph.getBpmnModel().getTargetNamespace(), "tns");
				editor.activateAddons();
				editor.setModified(false);
				editor.setCurrentFile(null);
				editor.closeGraphComponents();
				editor.resetDiagramName();
				BPMNEditor.configSimulation = false;
				((JPanel) editor.getMainPane().getRightComponent()).remove(editor.getSimulationPane());
				editor.getMainPane().revalidate();
				BPMNEditorUtils.refreshDiagramList(editor, null);
				graphComponent.getViewport().setOpaque(true);
				graphComponent.getViewport().setBackground(Color.WHITE);
				model.setBackgroundColor(Color.WHITE);
				graphComponent.setVerticalPageCount(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumV", "1")));
				graphComponent.setHorizontalPageCount(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumH", "1")));
				model.setPageCount(graphComponent.getVerticalPageCount());
				model.setHorizontalPageCount(graphComponent.getHorizontalPageCount());
				Constants.SWIMLANE_WIDTH = (int) (model.getPageFormat().getWidth() * 1.25 + (model.getHorizontalPageCount() - 1)
						* (Constants.SWIMLANE_START_POINT + model.getPageFormat().getWidth() * 1.25));

				Constants.SWIMLANE_HEIGHT = (int) (model.getPageFormat().getHeight() * 1.2 + (model.getPageCount() - 1)
						* (Constants.SWIMLANE_START_POINT + model.getPageFormat().getHeight() * 1.2));
				graphComponent.zoomAndCenter();
				editor.getBpmnView().refreshView(graph);
				editor.getUndoManager().clear();
			}
		} else if(EXPORT_USE.equals(type)){
			try {
				String path = 
//						"C:/Users/Rafael/Desktop/Nova pasta (3)/";
						UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().substring(0, UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().lastIndexOf(File.separator)+1);
				File bpmnFile = new File(path+"bpmnmodel.bpmn");
				if(bpmnFile.exists()){
					bpmnFile.delete();
				}

				BPMNCodec codec = new BPMNCodec(graph);
				bpmnModel.setExporter("Yaoqiang BPMN Editor");
				bpmnModel.setExporterVersion(BaseEditor.VERSION);
				mxUtils.writeFile(mxXmlUtils.getXml(codec.encode().getDocumentElement()), bpmnFile.getAbsolutePath());

				editor.setModified(false);
				editor.setCurrentFile(bpmnFile);
				BPMNEditorUtils.addRecentFiletoList(editor, bpmnFile.getAbsolutePath());
				System.out.println("O arquivo BPMN foi salvo com sucesso!");
//------------------------------------Comente daqui
				Thread.sleep(3000);

				ProcessDiagram processDiagram = ParserBPMNHelper.getInstance().getProcessDiagram();
				System.out.println("\n--------------------------------resultado do parser------------------------------");
				System.out.println(processDiagram.toString());
				
//				DocumentHelper.getInstance().geneareDocument(processDiagram);

				Graph graph1 = GraphHelper.getInstance().generateGraph(processDiagram);
				System.out.println("\n______________________________Gerando o grafo__________________________________________________________");
				System.out.println(graph1.toString());


				List<List<Vertice>>  pahts = GraphHelper.getInstance().generatePaths();
				System.out.println("\n--------------------------------Camhinhos gerados--------------------------------------------------------");
				int i = 1;
				for (List<Vertice> list : pahts) {
					System.out.println("\n-----------------------------Path"+i+"-------------------------------\n");
					for (Vertice vertice : list) {
//						if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
//							System.out.print(vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+") > ");
//							System.out.print(vertice.getVeriticeElement().getName().replace("\n", " ")+" > ");
//						}
//						else{
//							System.out.print(vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+") > ");
//						}
						System.out.print(vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+")"+"->");
					}
					i++;
				}
				
//				String path1 = UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().substring(0, UmlFileManager.getInstance().getExportXmiFile().getAbsolutePath().lastIndexOf(File.separator)+1)+"bpmnPathsModel.serializabe";
//			        GenereteSerializable.saveModel(path1, pahts);
//				
//				if(poli.mestrado.parser.util.Constants.onlyMainInAsslScript){
//					GenerateAsslScriptsManager.getInstance().generateAsslScriptOnlyMain(pahts);
//				}else{
//					GenerateAsslScriptsManager.getInstance().generateAsslScriptsManiAndMethod(pahts);
//				}
				//------------------------------------ATE aqui Comente daqui
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(graphComponent, ex.toString(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
			}

		}else if (SAVE.equals(type)) {
			FileFilter selectedFilter = null;
			FileFilter defaultFilter = new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + mxResources.get("file") + " (.bpmn)");
			FileFilter activitiFilter = new DefaultFileFilter(".bpmn20.xml", "OMG BPMN 2.0 " + mxResources.get("file") + " for Activiti (.bpmn20.xml)");
			FileFilter activitiBarFilter = new DefaultFileFilter(".bar", "Activiti business archive " + mxResources.get("file") + " (.bar)");
			FileFilter odtFileFilter = new DefaultFileFilter(".odt", "OpenDocument Text " + mxResources.get("file") + " (.odt)");
			FileFilter svgFileFilter = new DefaultFileFilter(".svg", "SVG " + mxResources.get("file") + " (.svg)");
			FileFilter vmlFileFilter = new DefaultFileFilter(".html", "VML " + mxResources.get("file") + " (.html)");
			FileFilter htmlFileFilter = new DefaultFileFilter(".html", "HTML " + mxResources.get("file") + " (.html)");
			String filename = null;
			boolean dialogShown = false;

			if ("5".equals(stringValue) || "1".equals(stringValue) || "4".equals(stringValue) || !"2".equals(stringValue) && BPMNEditor.getCurrentFile() == null) {
				String wd;

				if (EditorConstants.LAST_OPEN_DIR != null) {
					wd = EditorConstants.LAST_OPEN_DIR;
				} else if (BPMNEditor.getCurrentFile() != null) {
					wd = BPMNEditor.getCurrentFile().getParent();
				} else {
					wd = System.getProperty("user.dir");
				}

				JFileChooser fc = new JFileChooser(wd);
				fc.setAcceptAllFileFilterUsed(false);
				if (BPMNEditor.getCurrentFile() != null) {
					String name = BPMNEditor.getCurrentFile().getName();
					if (name.endsWith(".bpmn20.xml")) {
						name = name.substring(0, name.lastIndexOf(".bpmn20.xml"));
					} else {
						name = name.substring(0, name.lastIndexOf("."));
					}
					fc.setSelectedFile(new File(name));
				}

				if ("3".equals(stringValue)) {
					fc.setFileFilter(new DefaultFileFilter(".png", "PNG " + mxResources.get("file") + " (.png)"));
				} else if ("4".equals(stringValue)) {
					fc.setFileFilter(new DefaultFileFilter(".odt", "OpenDocument Text " + mxResources.get("file") + " (.odt)"));
				} else {
					fc.addChoosableFileFilter(defaultFilter);
					fc.addChoosableFileFilter(activitiFilter);
					if (BPMNEditor.getCurrentFile() != null) {
						fc.addChoosableFileFilter(activitiBarFilter);
					}
					fc.addChoosableFileFilter(odtFileFilter);
					fc.addChoosableFileFilter(svgFileFilter);
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(htmlFileFilter);

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();
					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();
					for (int i = 0; i < imageFormats.length; i++) {
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}
					imageFormats = formats.toArray();
					for (int i = 0; i < imageFormats.length; i++) {
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("." + ext, ext.toUpperCase() + " " + mxResources.get("file") + " (." + ext + ")"));
					}
					fc.setFileFilter(defaultFilter);
				}

				int rc = fc.showDialog(null, "3".equals(stringValue) ? mxResources.get("savePNGfile") : mxResources.get("save"));
				dialogShown = true;
				if (rc != JFileChooser.APPROVE_OPTION) {
					return;
				} else {
					EditorConstants.LAST_OPEN_DIR = fc.getSelectedFile().getParent();
					Utils.saveToConfigureFile("lastOpenDir", EditorConstants.LAST_OPEN_DIR);
				}
				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();
				if (selectedFilter instanceof DefaultFileFilter) {
					String ext = ((DefaultFileFilter) selectedFilter).getExtension();
					if (!filename.toLowerCase().endsWith(ext)) {
						filename += ext;
					}
				} else if ("3".equals(stringValue)) {
					if (!filename.toLowerCase().endsWith(".png")) {
						filename += ".png";
					}
				}
				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(graphComponent, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
					return;
				}
			}else if ("2".equals(stringValue)) {
				if (!editor.getGraphComponents().isEmpty()) {
					JOptionPane.showMessageDialog(graphComponent, mxResources.get("cannotSaveMultiDiagramAsFragment"), mxResources.get("Warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				final String fdir = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR;
				FileSystemView fsv = new FileSystemView() {
					public File createNewFolder(File containingDir) throws IOException {
						return null;
					}

					public File getHomeDirectory() {
						return createFileObject(fdir);
					}

					public Boolean isTraversable(File f) {
						return f.getAbsolutePath().equals(fdir);
					}

					public File[] getRoots() {
						return new File[] { new File(fdir) };
					}

				};
				JFileChooser fc = new JFileChooser(fdir, fsv);
				fc.setAcceptAllFileFilterUsed(false);
				FileFilter fragmentFilter = new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + mxResources.get("file") + " (.bpmn)");
				fc.setFileFilter(fragmentFilter);
				int rc = fc.showDialog(null, mxResources.get("save"));
				dialogShown = true;
				if (rc != JFileChooser.APPROVE_OPTION) {
					return;
				}
				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();
				if (selectedFilter instanceof DefaultFileFilter) {
					String ext = ((DefaultFileFilter) selectedFilter).getExtension();
					if (!filename.toLowerCase().endsWith(ext)) {
						filename += ext;
					}
				}
				if (new File(filename).exists()) {
					JOptionPane.showMessageDialog(graphComponent, mxResources.get("cannotOverwriteExistingFragment"), mxResources.get("Warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else {
				String ext = "";
				if ("3".equals(stringValue)) {
					ext = ".png";
				}
				filename = BPMNEditor.getCurrentFile().getAbsolutePath();
				if (!filename.toLowerCase().endsWith(ext)) {
					if (filename.endsWith(".bpmn20.xml")) {
						filename = filename.substring(0, filename.lastIndexOf(".bpmn20.xml")) + ext;
					} else {
						filename = filename.substring(0, filename.lastIndexOf(".")) + ext;
					}
					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
						return;
					}
				}
			}

			try {
				String ext = filename.substring(filename.lastIndexOf('.') + 1);
				if (ext.equalsIgnoreCase("svg")) {
					SvgCanvas canvas = (SvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, true, new CanvasFactory() {
						public mxICanvas createCanvas(int width, int height) {
							SvgCanvas canvas = new SvgCanvas(mxDomUtils.createSvgDocument(width, height));
							canvas.setEmbedded(true);
							return canvas;
						}
					});
					mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
				} else if (selectedFilter == vmlFileFilter) {
					mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createVmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
				} else if (ext.equalsIgnoreCase("html")) {
					mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createHtmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
				} else if (ext.equalsIgnoreCase("bpmn") || ext.equalsIgnoreCase("xml")) {
					BPMNCodec codec = new BPMNCodec(graph);
					bpmnModel.setExporter("Yaoqiang BPMN Editor");
					bpmnModel.setExporterVersion(BaseEditor.VERSION);
					mxUtils.writeFile(mxXmlUtils.getXml(codec.encode().getDocumentElement()), filename);
					if ("2".equals(stringValue)) {
						BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, graphComponent.isAntiAlias(), null, false,
								graphComponent.getCanvas());
						if (image != null) {
							String imagefilename = filename.substring(0, filename.lastIndexOf(".")) + ".png";
							ImageIO.write(image, "png", new File(imagefilename));
							editor.getFragmentsPalette().addFragmentTemplate(new File(imagefilename));
							editor.getFragmentsPalette().setPreferredCols(1, 195 * image.getHeight() / image.getWidth());
						}
					} else {
						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
						BPMNEditorUtils.addRecentFiletoList(editor, filename);
					}
				} else if (ext.equalsIgnoreCase("bar")) {
					BPMNEditorUtils.createBarFile(graphComponent, filename);
				} else if (ext.equalsIgnoreCase("odt")) {
					BPMNEditorUtils.createODTFile(graphComponent, filename);
				} else {
					Color bg = null;
					if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png")) || !"3".equals(stringValue)
							&& JOptionPane.showConfirmDialog(graphComponent, mxResources.get("transparentBackground")) != JOptionPane.YES_OPTION) {
						bg = graphComponent.getViewport().getBackground();
					}
					double scale = 1;
					if ("1".equals(Constants.SETTINGS.getProperty("exportImageResolution", "0"))) {
						scale = graph.getView().getScale();
					}
					BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, scale, bg, graphComponent.isAntiAlias(), null, true,
							graphComponent.getCanvas());
					if (image != null) {
						ImageIO.write(image, ext, new File(filename));
						if ("3".equals(stringValue) && !dialogShown) {
							JOptionPane.showMessageDialog(graphComponent, "The png file is saved! \n (" + filename + ")");
						}

					} else {
						JOptionPane.showMessageDialog(graphComponent, mxResources.get("noImageData"));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(graphComponent, ex.toString(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
			}
		} else if (OPEN.equals(type) || RELOAD.equals(type) || OPEN_URL.equals(type)) {
			editor.progress(true);
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, mxResources.get("saveChanges"), mxResources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				graph.getView().setCurrentRoot(null);
				if (RELOAD.equals(type) && BPMNEditor.getCurrentFile() != null) {
					stringValue = BPMNEditor.getCurrentFile().getAbsolutePath();
				}
				if (stringValue != null && stringValue.length() != 0) {
					try {
						File file = new File(stringValue);
						Document document = null;
						if (file.exists() && file.isFile()) {
							document = EditorUtils.parseXml(new FileInputStream(stringValue));
						} else {
							BPMNEditorUtils.openBPMN(editor, stringValue);
						}

						if (document != null) {
							String name = document.getDocumentElement().getNodeName();
							if (name.endsWith("definitions")) {
								BPMNEditorUtils.openBPMN(editor, file);
								BPMNEditorUtils.addRecentFiletoList(editor, stringValue);
							} else if (name.equals("VisioDocument")) {
								BPMNEditorUtils.openVdx(editor, file, document);
							} else if (name.equals("graphml")) {
								BPMNEditorUtils.openGraphML(editor, file, document);
							}
						}
					} catch (FileNotFoundException e1) {
						editor.progress(false);
						EditorUtils.removeRecentFiletoList(editor, stringValue);
						JOptionPane.showMessageDialog(editor.getGraphComponent(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					} catch (Exception e2) {
						editor.progress(false);
						e2.printStackTrace();
						EditorUtils.removeRecentFiletoList(editor, stringValue);
					}
				} else if (OPEN_URL.equals(type)) {
					Object url = null;
					url = JOptionPane.showInputDialog(graphComponent, mxResources.get("openURLText"), mxResources.get("openURL"), JOptionPane.PLAIN_MESSAGE,
							null, null, "http://");
					if (url != null) {
						BPMNEditorUtils.openBPMN(editor, url);
					}
				} else if (objectValue != null) {
					BPMNEditorUtils.openBPMN(editor, objectValue);
				} else {
					String wd = (EditorConstants.LAST_OPEN_DIR != null) ? EditorConstants.LAST_OPEN_DIR : System.getProperty("user.dir");

					JFileChooser fc = new JFileChooser(wd);

					// Adds file filter for supported file format
					FileFilter defaultFilter = new DefaultFileFilter(".bpmn", mxResources.get("allSupportedFormats") + " (.bpmn, .bpmn20.xml, .xml)") {
						public boolean accept(File file) {
							String lcase = file.getName().toLowerCase();
							return super.accept(file) || lcase.endsWith(".xml");
						}
					};
					fc.addChoosableFileFilter(defaultFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + mxResources.get("file") + " (.bpmn)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".bpmn20.xml", "OMG BPMN 2.0 " + mxResources.get("file") + " for Activiti (.bpmn20.xml)"));

					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, mxResources.get("openFile"));

					if (rc == JFileChooser.APPROVE_OPTION) {
						EditorConstants.LAST_OPEN_DIR = fc.getSelectedFile().getParent();
						Utils.saveToConfigureFile("lastOpenDir", EditorConstants.LAST_OPEN_DIR);
						String filepath = fc.getSelectedFile().getAbsolutePath();
						try {
							Document document = EditorUtils.parseXml(new FileInputStream(filepath));
							if (document != null) {
								String name = document.getDocumentElement().getNodeName();
								if (name.endsWith("definitions")) {
									BPMNEditorUtils.openBPMN(editor, fc.getSelectedFile());
									BPMNEditorUtils.addRecentFiletoList(editor, filepath);
								} else if (name.equals("VisioDocument")) {
									BPMNEditorUtils.openVdx(editor, fc.getSelectedFile(), document);
								} else if (name.equals("graphml")) {
									BPMNEditorUtils.openGraphML(editor, fc.getSelectedFile(), document);
								} else {
									JOptionPane
									.showMessageDialog(
											editor.getGraphComponent(),
											"The selected file is not recognized by Yaoqiang BPMN Editor. \nBecause the file format is unknown by the Editor,\nyou should be sure that the file comes from a trustworthy source.",
											"Unsupported file format", JOptionPane.ERROR_MESSAGE);
								}
							}
						} catch (Exception ex) {
							editor.progress(false);
							ex.printStackTrace();
							JOptionPane.showMessageDialog(editor.getGraphComponent(), ex.getStackTrace(),
									"Please Capture This Error Screen Shots and Submit this BUG.", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				editor.closeGraphComponents();
			}
			editor.progress(false);
		} else if (type == EXIT) {
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, mxResources.get("saveChanges"), mxResources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				editor.exit();
			}
		} else if (OPEN_ORGANIZATION.equals(type)) {
			try {
				String filepath = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator + stringValue + ".org";
				Document document = EditorUtils.parseXml(new FileInputStream(filepath));
				mxCodec codec = new mxCodec(document);
				codec.decode(document.getDocumentElement(), editor.getOrgGraphComponent().getGraph().getModel());
				editor.getOrgGraphComponent().setVerticalPageCount(editor.getOrgGraphComponent().getGraph().getModel().getPageCount());
				editor.getOrgGraphComponent().setHorizontalPageCount(editor.getOrgGraphComponent().getGraph().getModel().getHorizontalPageCount());
				editor.getOrgGraphComponent().getViewport().setOpaque(false);
				editor.getOrgGraphComponent().setBackground(editor.getOrgGraphComponent().getGraph().getModel().getBackgroundColor());
				editor.getOrgGraphComponent().getGraph().refresh();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			editor.setOrgModified(false);
			editor.getUndoManager().clear();
			editor.getOrgGraphComponent().zoomAndCenter();
		} else if (SAVE_ORGANIZATION.equals(type)) {
			BPMNEditorUtils.saveOrganization(editor.getCurrentOrganization().optString("id"), editor.getOrgGraphComponent().getGraph().getModel());
			editor.setOrgModified(false);
		} else if (ADD_ORGANIZATION.equals(type)) {
			BPMNGraph orgGraph = editor.getOrgGraphComponent().getGraph();
			GraphModel orgModel = orgGraph.getModel();
			mxCell pCell = (mxCell) orgGraph.getSelectionCell();
			if (editor.getCurrentGraphComponent() != editor.getOrgGraphComponent() || pCell == null) {
				return;
			}
			mxGeometry geo = (mxGeometry) pCell.getGeometry().clone();
			String orgtype = null;
			if ("role".equals(stringValue)) {
				orgtype = "organizationalRole";
			} else if ("unit".equals(stringValue)) {
				orgtype = "organizationalUnit";
			} else if ("same".equals(stringValue)) {
				orgtype = pCell.getStyle();
			}
			mxCell orgCell = new mxCell(pCell.getValue(), geo, orgtype);
			orgCell.setVertex(true);
			orgModel.add(orgModel.getCell("_1"), orgCell, 0);
			mxCell edge = new mxCell(null, new mxGeometry(), "organizationFlow");
			edge.setEdge(true);
			edge.getGeometry().setRelative(true);

			if ("same".equals(stringValue)) {
				pCell = (mxCell) BPMNEditorUtils.getParentOrganization(orgGraph, pCell);
			}
			orgGraph.addEdge(edge, null, pCell, orgCell, null);
			orgGraph.setSelectionCell(orgCell);
			GraphActions.getAutoLayoutAction().actionPerformed(e);
		}else if (EXPORT_ORGANIZATION.equals(type)) {
			BPMNGraphComponent orgGraphComponent = editor.getOrgGraphComponent();
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setAcceptAllFileFilterUsed(false);
			DefaultFileFilter ldifFilter = new DefaultFileFilter(".ldif", "LDIF " + mxResources.get("file") + " (.ldif)");
			fc.addChoosableFileFilter(ldifFilter);
			Object[] imageFormats = ImageIO.getReaderFormatNames();
			HashSet<String> formats = new HashSet<String>();
			for (int i = 0; i < imageFormats.length; i++) {
				String ext = imageFormats[i].toString().toLowerCase();
				formats.add(ext);
			}
			imageFormats = formats.toArray();
			for (int i = 0; i < imageFormats.length; i++) {
				String ext = imageFormats[i].toString();
				fc.addChoosableFileFilter(new DefaultFileFilter("." + ext, ext.toUpperCase() + " " + mxResources.get("file") + " (." + ext + ")"));
			}
			fc.setFileFilter(ldifFilter);

			int rc = fc.showDialog(null, mxResources.get("export"));
			if (rc != JFileChooser.APPROVE_OPTION) {
				return;
			}
			String filename = fc.getSelectedFile().getAbsolutePath();
			FileFilter selectedFilter = fc.getFileFilter();
			if (selectedFilter instanceof DefaultFileFilter) {
				String ext = ((DefaultFileFilter) selectedFilter).getExtension();
				if (!filename.toLowerCase().endsWith(ext)) {
					filename += ext;
				}
			}
			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(orgGraphComponent, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
				return;
			}

			String ext = filename.substring(filename.lastIndexOf('.') + 1);
			if (ext.equalsIgnoreCase("ldif")) {
				LdapUtils.exportLDIF(filename, editor, "diagram");
			} else {
				Color bg = null;
				if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png"))
						|| JOptionPane.showConfirmDialog(orgGraphComponent, mxResources.get("transparentBackground")) != JOptionPane.YES_OPTION) {
					bg = orgGraphComponent.getBackground();
				}

				BufferedImage image = mxCellRenderer.createBufferedImage(orgGraphComponent.getGraph(), null, 1, bg, orgGraphComponent.isAntiAlias(), null,
						true, orgGraphComponent.getCanvas());
				if (image != null) {
					try {
						ImageIO.write(image, ext, new File(filename));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(orgGraphComponent, mxResources.get("noImageData"));
				}
			}
		} else if (OPEN_DIAGRAM.equals(type)) {
			mxCell diagramCell = (mxCell) graph.getModel().getCell(stringValue);
			editor.insertGraphComponent(stringValue, (String) diagramCell.getValue());
		} else if (DIAGRAM_NAME.equals(type)) {
			mxCell root = (mxCell) model.getChildAt(model.getRoot(), 0);
			String initial = (String) root.getValue();
			String value = (String) JOptionPane.showInputDialog(graphComponent, mxResources.get(DIAGRAM_NAME), mxResources.get(DIAGRAM_NAME),
					JOptionPane.PLAIN_MESSAGE, null, null, initial);
			if (value != null) {
				root.setValue(value);
				editor.setDiagramName(value);
				editor.setModified(true);
				if (BPMNEditor.getRevision() > 0) {
					editor.updateTitle();
				}
				int index = editor.getDiagramName().indexOf(" -> SubProcess: ");
				if (index > 0) {
					value += editor.getDiagramName().substring(index);
					editor.setDiagramName(value);
				}

				BPMNEditorUtils.refreshDiagramList(editor, root);
			}
		} else if (ELEMENT.equals(type)) {
			dialog.initDialog().editBPMNElement(el);
		} else if (CONDITION_EXPRESSION.equals(type)) {
			if (el instanceof SequenceFlow) {
				dialog.initDialog().editBPMNElement(((SequenceFlow) el).getConditionExpression());
			}
		} else if (DATA_STATE.equals(type)) {
			if (el instanceof DataObjectReference) {
				dialog.initDialog().editBPMNElement(((DataObjectReference) el).get(type));
			}
		} else if (DOCUMENTATION.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			}
			if (el instanceof BaseElement) {
				dialog.initDialog().editBPMNElement(((BaseElement) el).getDocumentations(), type);
			}
		} else if (EVENT_DEFINITION.equals(type)) {
			if (el instanceof Event) {
				if (((Event) el).getEventDefinitionList().isEmpty()) {
					dialog.initDialog().editBPMNElement(((Event) el).getRefEventDefinition());
				} else {
					dialog.initDialog().editBPMNElement(((Event) el).getEventDefinition());
				}
			}
		} else if (EVENT_DEFINITIONS.equals(type)) {
			if (el instanceof Event) {
				dialog.initDialog().editBPMNElement(((Event) el).getEventDefinitions());
			}
		} else if (DEF_EVENT_DEFINITIONS.equals(type)) {
			dialog.initDialog().editBPMNElement(graph.getBpmnModel(), EVENT_DEFINITIONS);
		} else if (LOOP_CHARACTERISTICS.equals(type)) {
			if (el instanceof Activity) {
				dialog.initDialog().editBPMNElement(((Activity) el).getLoopCharacteristics());
			}
		} else if (PROCESS_PROPERTIES.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(el);
			}
		} else if (DATA_PROPERTIES.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof Activity || el instanceof Event || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("properties"), DATA_PROPERTIES);
			}
		} else if (DATA_OBJECTS.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof SubProcess || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("flowElements"), DATA_OBJECTS);
			}
		} else if (DATA_INOUT.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			}
			if (el instanceof Activity || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(el, "dataInputOutputs");
			} else if (el instanceof CatchEvent) {
				dialog.initDialog().editBPMNElement(el, "dataoutputs");
			} else if (el instanceof ThrowEvent) {
				dialog.initDialog().editBPMNElement(el, "datainputs");
			}
		} else if (RESOURCE_ASSIGNMENT.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof Activity || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("resources"));
			}
		} else if (DEPLOY.equals(type)) {
			if (BPMNEditor.getCurrentFile() == null) {
				JOptionPane.showMessageDialog(graphComponent, mxResources.get("saveFileFirst"));
				return;
			}
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, mxResources.get("saveChanges"), mxResources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				editor.getDialog().initDialog(false, "deploy").editObject(editor, "deployProcessDefinition");
			}
		} else if (ENGINE_CONNECTIONS.equals(type)) {
			editor.getDialog().initDialog(false).editObject(editor, "engineConnections");
		} else if (LDAP_CONNECTIONS.equals(type)) {
			editor.getDialog().initDialog(false).editObject(editor, "ldapConnections");
		} else if (IMPORT_LDIF.equals(type)) {
			String wd = (EditorConstants.LAST_OPEN_DIR != null) ? EditorConstants.LAST_OPEN_DIR : System.getProperty("user.dir");
			JFileChooser fc = new JFileChooser(wd);

			FileFilter defaultFilter = new DefaultFileFilter(".ldif", "LDIF " + mxResources.get("file") + " (.ldif)");
			fc.addChoosableFileFilter(defaultFilter);
			fc.setFileFilter(defaultFilter);
			int rc = fc.showDialog(null, mxResources.get("importLdif"));

			if (rc == JFileChooser.APPROVE_OPTION) {
				EditorConstants.LAST_OPEN_DIR = fc.getSelectedFile().getParent();
				Utils.saveToConfigureFile("lastOpenDir", EditorConstants.LAST_OPEN_DIR);
				String filename = fc.getSelectedFile().getAbsolutePath();
				JSONObject con = editor.getCurrentLdapConnection();
				LdapUtils.importLDIF(con, filename);

				Map<String, Attributes> entries = LdapUtils.searchLdap(con);
				BPMNEditor.setLdapEntries(entries);
				Map<String, LdapTreeNode> nodes = LdapUtils.buildLdapTreeNodes(con.optString("baseDN"), entries);
				editor.resetLdapTree(new LdapTreeNode[] { nodes.get(con.optString("baseDN")), nodes.get(editor.getLdapSelectedEntry()) });
			}
		} else if (EXPORT_LDIF.equals(type)) {
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new DefaultFileFilter(".ldif", "LDIF " + mxResources.get("file") + " (.ldif)"));

			int rc = fc.showDialog(null, mxResources.get("exportLdif"));
			if (rc != JFileChooser.APPROVE_OPTION) {
				return;
			}
			String filename = fc.getSelectedFile().getAbsolutePath();
			FileFilter selectedFilter = fc.getFileFilter();
			if (selectedFilter instanceof DefaultFileFilter) {
				String ext = ((DefaultFileFilter) selectedFilter).getExtension();
				if (!filename.toLowerCase().endsWith(ext)) {
					filename += ext;
				}
			}
			if (new File(filename).exists() && JOptionPane.showConfirmDialog(null, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
				return;
			}

			LdapUtils.exportLDIF(filename, editor, "ldap");
		} else if (CACHE_LDAP.equals(type)) {
			String filename = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator
					+ editor.getLdapPanel().getRootNode() + ".org";
			if (new File(filename).exists() && JOptionPane.showConfirmDialog(null, mxResources.get("overwriteExistingCache")) != JOptionPane.YES_OPTION) {
				return;
			}
			LdapUtils.cacheLdap(editor, !new File(filename).exists());
			JOptionPane.showMessageDialog(null, "The LDAP data is cached!");
			editor.setOrgModified(false);
		} else if (ORGANIZATIONS.equals(type)) {
			editor.getDialog().initDialog(false).editObject(editor, "organizations");
		} else if (USERS.equals(type)) {
			editor.getDialog().initDialog(false).editObject(editor, "users");
		} else if (ADDON.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			}
			dialog.getPanelContainer().setCurrentAddon(editor.getAddonsManager().getAddon(stringValue));
			dialog.initDialog().editBPMNElement(el, "addon:" + stringValue);
		} else if (RUN_SIMULATION.equals(type)) {
			if (BPMNEditor.isSimulationPausing()) {
				editor.restartSimulation();
				return;
			}
			if (BPMNEditor.isSimulationRunning()) {
				JOptionPane.showMessageDialog(null, "Simulation is already running!", mxResources.get("optionTitle"), JOptionPane.WARNING_MESSAGE);
				return;
			}
			editor.startSimulation();
		} else if (PAUSE_SIMULATION.equals(type)) {
			editor.pauseSimulation();
		} else if (STOP_SIMULATION.equals(type)) {
			editor.stopSimulation();
		} else if (SIMULATION_SPEED.equals(type)) {
			int interval = 0;
			String value = (String) JOptionPane.showInputDialog(graphComponent, mxResources.get("interval") + " (ms)", mxResources.get("simulationSpeed"),
					JOptionPane.PLAIN_MESSAGE, null, null, BPMNEditor.simulationInterval);

			if (value != null) {
				interval = Integer.parseInt(value);
			}
			if (interval > 0) {
				BPMNEditor.simulationInterval = interval;
				Constants.SETTINGS.put("interval", value);
				Utils.saveConfigureFile();
			}
		} else if (CONFIG_SIMULATION.equals(type)) {
			List<XMLElement> properties = BPMNModelUtils.getAllProperties(bpmnModel);
			properties.addAll(BPMNModelUtils.getAllConditionalSequenceFlows(bpmnModel));
			properties.addAll(BPMNModelUtils.getAllBoundaryEvents(bpmnModel));
			properties.addAll(BPMNModelUtils.getAllEventGateways(bpmnModel));
			MainPanel simPanel = (MainPanel) editor.getSimulationPane();
			XMLTablePanel table = simPanel.getPropertiesTable();
			table.fillTableContent(properties);
			((JPanel) editor.getMainPane().getRightComponent()).add(simPanel, BorderLayout.EAST);
			BPMNEditor.configSimulation = true;
			editor.getMainPane().revalidate();
		} else if (CLOSE_SIMULATION.equals(type)) {
			((JPanel) editor.getMainPane().getRightComponent()).remove(editor.getSimulationPane());
			BPMNEditor.configSimulation = false;
			editor.getMainPane().revalidate();
		} else if (SAVE_TO_REPO.equals(type)) {
			dialog.initDialog(false, "save").editBPMNElement(bpmnModel, type);
		} else if (VERSION_HISTORY.equals(type)) {
			if (BPMNEditor.getRevision() > 0) {
				dialog.initDialog(false).editObject(bpmnModel, type);
			}
		} else if (type == CHANGE_FLOW_ELEMENT_TYPE) {
			BaseElement value = (BaseElement) model.getValue(cell);
			BaseElement newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
			model.beginUpdate();
			if ("exclusiveGatewayWithIndicator".equals(stringValue) || "exclusiveGateway".equals(stringValue)) {
				model.setStyle(cell, stringValue);
				stringValue = "exclusiveGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
			} else if ("eventGatewayInstantiate".equals(stringValue)) {
				stringValue = "eventBasedGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("instantiate", "true");
			} else if ("parallelEventGateway".equals(stringValue)) {
				stringValue = "eventBasedGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("eventGatewayType", "Parallel");
				newValue.set("instantiate", "true");
			} else if ("intermediateThrowEvent".equals(stringValue)) {
				model.setStyle(cell, "intermediateEvent");
			} else if ("endEvent".equals(stringValue)) {
				model.setStyle(cell, stringValue);
			} else if ("eventSubProcess".equals(stringValue)) {
				stringValue = "subProcess";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("triggeredByEvent", "true");
				newValue.add(value.get("flowElements"));
			} else if (value instanceof CallActivity) {
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement("callActivity");
				GlobalTask task = null;
				for (XMLElement gt : bpmnModel.getGlobalTasks(stringValue)) {
					task = (GlobalTask) gt;
					break;
				}
				if (task == null) {
					task = bpmnModel.createGlobalTask(stringValue);
				}
				((CallActivity) newValue).setCalledElement(task.getId());
			}
			// TODO:A
			if (value instanceof Activity && newValue instanceof Activity) {
				newValue.add(value.get("properties"));
				newValue.add(value.get("loopCharacteristics"));
			} else if (value instanceof Event && newValue instanceof Event) {
				newValue.add(value.get("properties"));
				newValue.add(((Event) value).getEventDefinitions());
				newValue.add(((Event) value).getEventDefinitionRefs());
			}
			if (value instanceof FlowNode && newValue instanceof FlowNode) {
				newValue.add(((FlowNode) value).getIncomings());
				newValue.add(((FlowNode) value).getOutgoings());
			}
			if (value instanceof SubProcess && newValue instanceof SubProcess) {
				newValue.add(value.get("flowElements"));
			}
			mxGeometry geo = (mxGeometry) cell.getGeometry().clone();
			if (newValue instanceof Gateway && !(value instanceof Gateway)) {
				int size = Integer.parseInt(Constants.SETTINGS.getProperty("style_gateway_size", "42"));
				geo.setRect(geo.getCenterX() - size / 2, geo.getCenterY() - size / 2, size, size);
				model.setGeometry(cell, geo);
			} else if (newValue instanceof Task && !(value instanceof Task)) {
				model.setStyle(cell, stringValue);
				int width = Integer.parseInt(Constants.SETTINGS.getProperty("style_task_width", "85"));
				int height = Integer.parseInt(Constants.SETTINGS.getProperty("style_task_height", "55"));
				geo.setRect(geo.getCenterX() - width / 2, geo.getCenterY() - height / 2, width, height);
				model.setGeometry(cell, geo);
			}
			newValue.setId(value.getId());
			newValue.set("name", value.get("name").toValue());
			model.setValue(cell, newValue);
			model.endUpdate();
		} else if (type == CHANGE_ACTIVITY_LOOP_TYPE) {
			FlowNode value = null;
			if (model.getValue(cell) instanceof Activity) {
				value = (Activity) ((Activity) model.getValue(cell)).clone();
				LoopCharacteristics loopCharacteristics = ((Activity) value).setLoopCharacteristics(stringValue);
				if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics) {
					((MultiInstanceLoopCharacteristics) loopCharacteristics).setIsSequential(booleanValue);
				}
			} else if (model.getValue(cell) instanceof ChoreographyActivity) {
				value = (ChoreographyActivity) ((ChoreographyActivity) model.getValue(cell)).clone();
				((ChoreographyActivity) value).setLoopType(stringValue);
			}
			if (model.getValue(cell) instanceof FlowElementsContainer) {
				value.add(((FlowNode) model.getValue(cell)).get("flowElements"));
			}
			model.setValue(cell, value);
		} else if (type == CHANGE_ACTIVITY_COMPENSATION_TYPE) {
			Activity value = (Activity) ((Activity) model.getValue(cell)).clone();
			value.setIsForCompensation(booleanValue);
			model.setValue(cell, value);
		} else if (type == CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE) {
			ReceiveTask value = (ReceiveTask) ((ReceiveTask) model.getValue(cell)).clone();
			value.setInstantiate(booleanValue);
			model.setValue(cell, value);
		} else if (type == CHANGE_DATA_TYPE) {
			BaseElement value = (BaseElement) model.getValue(cell);
			if (!value.toName().equals(stringValue)) {
				BaseElement newValue = null;
				XMLCollection parentElement = (XMLCollection) value.getParent();
				if ("dataInput".equals(stringValue)) {
					newValue = new DataInput(((DataOutput) value).getName(), ((DataOutput) value).isCollection());
				} else {
					newValue = new DataOutput(((DataInput) value).getName(), ((DataInput) value).isCollection());
				}
				newValue.setId(value.getId());
				parentElement.add(newValue);
				model.setValue(cell, newValue);
			}
		} else if (type == CHANGE_DATA_COLLECTION_TYPE) {
			XMLElement value = (XMLElement) ((XMLElement) model.getValue(cell)).clone();
			model.setValue(cell, value);
			if (value instanceof DataObjectReference) {
				((DataObjectReference) value).getRefDataObject().setIsCollection(booleanValue);
			} else {
				((BaseElement) value).set("isCollection", String.valueOf(booleanValue));
			}
		} else if (type == DEFAUT_SF) {
			BaseElement bpmnElement = (BaseElement) model.getValue(cell);
			bpmnElement.set("default", stringValue);
			graph.refresh();
		} else if (type == CHANGE_EVENT_TYPE) {
			Event value = (Event) ((Event) model.getValue(cell)).clone();
			model.beginUpdate();
			String style = null;
			if (stringValue.length() == 0) {
				if (value instanceof StartEvent) {
					style = "startEvent";
				} else if (value instanceof EndEvent) {
					style = "endEvent";
				} else {
					style = "intermediateEvent";
				}
				value.getEventDefinitions().clear();
				value.getEventDefinitionRefs().clear();
			} else if (stringValue.equals("multiple")) {
				if (value instanceof StartEvent) {
					style = "startMultipleEvent";
				} else if (value instanceof IntermediateCatchEvent || value instanceof BoundaryEvent) {
					style = "intermediateMultipleEvent";
				} else if (value instanceof IntermediateThrowEvent) {
					style = "intermediateMultipleThrowEvent";
				} else if (value instanceof EndEvent) {
					style = "endMultipleEvent";
				}
				if (value instanceof CatchEvent) {
					((CatchEvent) value).setParallelMultiple(false);
				}
			} else if (stringValue.equals("parallelMultiple")) {
				if (value instanceof StartEvent) {
					style = "startParallelMultipleEvent";
				} else if (value instanceof IntermediateCatchEvent || value instanceof BoundaryEvent) {
					style = "intermediateParallelMultipleEvent";
				}
				((CatchEvent) value).setParallelMultiple(true);
			} else {
				if (value instanceof StartEvent) {
					style = "startEvent";
				} else if (value instanceof EndEvent) {
					style = "endEvent";
				} else {
					style = "intermediateEvent";
				}
				value.getEventDefinitions().clear();
				value.getEventDefinitionRefs().clear();
				EventDefinition eventDefinition = value.generateEventDefinition(stringValue);
				value.addEventDefinition(eventDefinition);
				if (eventDefinition instanceof LinkEventDefinition) {
					((LinkEventDefinition) eventDefinition).setName(value.getName());
				}
			}
			XMLExtensionElement styleElement = ((BaseElement) value).getExtensionElements().getChildElement("yaoqiang:style");
			if (styleElement != null) {
				for (XMLElement attr : styleElement.toElements()) {
					style += ";" + ((XMLAttribute) attr).toName() + "=" + ((XMLAttribute) attr).toValue();
				}
			}
			model.setStyle(cell, style);
			model.setValue(cell, value);
			model.endUpdate();
		} else if (type == ELEMENT_STYLES) {
			dialog.initDialog("reset").editObject((Object) null, "elementStyles");
		} else if (type == PREFERENCES) {
			dialog.initDialog().editObject((Object) null, "preferences");
		} else {
			dialog.initDialog().editBPMNElement(bpmnModel, type);
		}
		graphComponent.requestFocusInWindow();
	}

	public ModelActions setStringValue(String value) {
		this.stringValue = value;
		return this;
	}

	public ModelActions setObjectValue(Object value) {
		this.objectValue = value;
		return this;
	}

	public ModelActions setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		return this;
	}

	public BPMNEditor getEditor(ActionEvent e) {
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();
			while (component != null && !(component instanceof BPMNEditor)) {
				component = component.getParent();
			}
			return (BPMNEditor) component;
		}
		return null;
	}

}
