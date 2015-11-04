package org.yaoqiang.bpmn.editor.simulation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElementsContainer;
import org.yaoqiang.bpmn.model.elements.core.common.FlowNode;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.data.Property;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.gateways.EventBasedGateway;
import org.yaoqiang.bpmn.model.elements.gateways.ExclusiveGateway;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.Constants;

//import sun.org.mozilla.javascript.internal.Context;
//import sun.org.mozilla.javascript.internal.Scriptable;

import com.mxgraph.model.mxCell;

/**
 * SimulationAdmin
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SimulationAdmin {

	protected BPMNGraphComponent graphComponent;

	protected static BPMNGraph graph;

	protected GraphModel model;

	protected static Map<mxCell, String> previousStyles = new HashMap<mxCell, String>();

	protected Definitions definitions;

	static List<Set<FlowNode>> flowNodeList = new ArrayList<Set<FlowNode>>();

	protected Set<FlowNode> waitNodes = new HashSet<FlowNode>();

	protected boolean clean = true;

	protected boolean debug = false;

	public SimulationAdmin(BPMNGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
		SimulationAdmin.graph = graphComponent.getGraph();
		this.model = graphComponent.getGraph().getModel();
		this.definitions = graphComponent.getGraph().getBpmnModel();
	}

	public void start() {
		clearOldProcessInstances();
		Map<Long, BPMNProcess> processes = loadDefinitions();
		Map<Long, BPMNProcess> processInstances = createProcessInstances(processes);
		startProcesses(processInstances);
		Set<FlowNode> flowNodes = acceptAssignments(processInstances);
		while (!flowNodes.isEmpty()) {
			flowNodeList.add(flowNodes);
			flowNodes = acceptAssignments(processInstances);
		}
		completeProcesses(processInstances);
		unloadDefinitions();
	}

	Map<Long, BPMNProcess> loadDefinitions() {
		int version = 0;
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT REVISION FROM YAO_DEFINITIONS WHERE HEAD = 1 AND DEF_ID = ?");
			ps.setString(1, definitions.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				version = rs.getInt(1);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<Long, BPMNProcess> processes = new HashMap<Long, BPMNProcess>();
		String createTime = Constants.DF.format(new Date());
		for (BPMNProcess p : definitions.getProcesses()) {
			try {
				PreparedStatement ps = BPMNEditor.dbConn
						.prepareStatement("INSERT INTO YAO_PROCESS(DEF_ID,DEF_NAME,PROCESS_ID,PROCESS_NAME,PROCESS_TYPE,IS_EXECUTABLE,IS_CLOSED,STATE,VERSION,CREATE_TIME) VALUES(?,?,?,?,?,?,?,?,?,?);");
				ps.setString(1, definitions.getId());
				ps.setString(2, definitions.getName());
				ps.setString(3, p.getId());
				ps.setString(4, p.getName());
				ps.setString(5, p.getProcessType());
				ps.setBoolean(6, p.isExecutable());
				ps.setBoolean(7, p.isClosed());
				ps.setInt(8, 0);
				ps.setInt(9, version);
				ps.setString(10, createTime);
				ps.execute();
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					processes.put(rs.getLong(1), p);
					break;
				}
				printlog("Process: " + p.getName() + ", Id: " + p.getId() + ", Version " + version + " Loaded.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return processes;
	}

	void clearOldProcessInstances() {
		try {
			PreparedStatement ps = BPMNEditor.dbConn
					.prepareStatement("DELETE FROM YAO_ASSIGNMENT;DELETE FROM YAO_FLOW_NODE_DATA;DELETE FROM YAO_FLOW_NODE;DELETE FROM YAO_PROCESS_INSTANCE_DATA;DELETE FROM YAO_PROCESS_INSTANCE;DELETE FROM YAO_PROCESS;");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void unloadDefinitions() {
		if (clean) {
			try {
				PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("DELETE FROM YAO_PROCESS WHERE DEF_ID = ?");
				ps.setString(1, definitions.getId());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			printlog("Definitions: " + definitions.getName() + " unLoaded.");
		}
	}

	Map<Long, BPMNProcess> createProcessInstances(Map<Long, BPMNProcess> processes) {
		Map<Long, BPMNProcess> processInstances = new HashMap<Long, BPMNProcess>();
		for (Entry<Long, BPMNProcess> p : processes.entrySet()) {
			long instance_id = 0;
			BPMNProcess process = p.getValue();
			try {
				Long process_id = p.getKey();
				String createTime = Constants.DF.format(new Date());
				PreparedStatement ps = BPMNEditor.dbConn
						.prepareStatement("INSERT INTO YAO_PROCESS_INSTANCE(PROCESS_ID,PROCESS_NAME,STATE,STATE_TIME,CREATE_TIME) VALUES(?,?,?,?,?);");
				ps.setLong(1, process_id);
				ps.setString(2, process.getName());
				ps.setString(3, "open.not_running.not_started");
				ps.setString(4, createTime);
				ps.setString(5, createTime);
				ps.execute();

				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					instance_id = rs.getLong(1);
					break;
				}
				processInstances.put(instance_id, process);
				createProcessInstanceData(instance_id, process);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			printlog("Process Instance: " + process.getName() + " (" + instance_id + ")" + " created.");
		}

		return processInstances;
	}

	void createProcessInstanceData(Long instance_id, BPMNProcess process) {
		for (XMLElement d : process.getProperties().getXMLElements()) {
			try {
				Property data = (Property) d;
				PreparedStatement ps = BPMNEditor.dbConn
						.prepareStatement("INSERT INTO YAO_PROCESS_INSTANCE_DATA(PROCESS_INSTANCE_ID,DATA_ID,DATA_NAME,DATA_TYPE) VALUES(?,?,?,?);");
				ps.setLong(1, instance_id);
				ps.setString(2, data.getId());
				ps.setString(3, data.getName());
				ps.setString(4, data.getItemSubjectRef());
				ps.execute();
				printlog("Process Instance Data: " + data.getName() + " (" + data.getId() + ")" + " created.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	void startProcesses(Map<Long, BPMNProcess> processInstances) {
		for (Entry<Long, BPMNProcess> pi : processInstances.entrySet()) {
			Long instance_id = pi.getKey();
			BPMNProcess process = pi.getValue();
			printlog("Process Instance: " + process.getName() + " (" + instance_id + ")" + " is starting...");
			String stateTime = Constants.DF.format(new Date());
			try {
				PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_PROCESS_INSTANCE SET STATE = ?, STATE_TIME = ? WHERE ID = ?");
				ps.setString(1, "open.running");
				ps.setString(2, stateTime);
				ps.setLong(3, instance_id);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			executeProcess(instance_id, process);
		}
	}

	void executeProcess(Long instance_id, FlowElementsContainer process) {
		for (FlowNode node : process.getStartingFlowNodes()) {
			createFlowNode(instance_id, node);
		}
	}

	void completeProcesses(Map<Long, BPMNProcess> processInstances) {
		for (Entry<Long, BPMNProcess> pi : processInstances.entrySet()) {
			Long instance_id = pi.getKey();
			BPMNProcess process = pi.getValue();
			try {
				String stateTime = Constants.DF.format(new Date());
				PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_PROCESS_INSTANCE SET STATE = ?, STATE_TIME = ? WHERE ID = ?");
				ps.setString(1, "closed.completed");
				ps.setString(2, stateTime);
				ps.setLong(3, instance_id);
				ps.executeUpdate();
				printlog("Process Instance " + process.getName() + " (" + instance_id + ")" + " finished.");
				if (clean) {
					ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_FLOW_NODE_DATA WHERE PROCESS_INSTANCE_ID = ?");
					ps.setLong(1, instance_id);
					ps.executeUpdate();

					ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_FLOW_NODE WHERE PROCESS_INSTANCE_ID = ?");
					ps.setLong(1, instance_id);
					ps.executeUpdate();

					ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_PROCESS_INSTANCE_DATA WHERE PROCESS_INSTANCE_ID = ?");
					ps.setLong(1, instance_id);
					ps.executeUpdate();

					ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_PROCESS_INSTANCE WHERE ID = ?");
					ps.setLong(1, instance_id);
					ps.executeUpdate();
					printlog("Process Instance " + process.getName() + " (" + instance_id + ")" + " deleted.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	void createFlowNode(Long instance_id, FlowNode flowNode) {
		long flow_node_id = 0;
		try {
			PreparedStatement ps = BPMNEditor.dbConn
					.prepareStatement("SELECT ID FROM YAO_FLOW_NODE WHERE STATE != ? AND FLOW_NODE_ID = ? AND PROCESS_INSTANCE_ID = ?");
			ps.setString(1, "closed.completed");
			ps.setString(2, flowNode.getId());
			ps.setLong(3, instance_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				flow_node_id = rs.getLong(1);
			}

			if (flow_node_id == 0) {
				String createTime = Constants.DF.format(new Date());
				ps = BaseEditor.dbConn
						.prepareStatement("INSERT INTO YAO_FLOW_NODE(PROCESS_INSTANCE_ID,FLOW_NODE_ID,FLOW_NODE_NAME,STATE,STATE_TIME,CREATE_TIME) VALUES(?,?,?,?,?,?);");
				ps.setLong(1, instance_id);
				ps.setString(2, flowNode.getId());
				ps.setString(3, flowNode.getName());
				ps.setString(4, "open.not_running.not_started");
				ps.setString(5, createTime);
				ps.setString(6, createTime);
				ps.execute();

				rs = ps.getGeneratedKeys();
				while (rs.next()) {
					flow_node_id = rs.getLong(1);
					break;
				}

				createFlowNodeData(instance_id, flow_node_id, flowNode);
				printlog("FlowNode: " + flowNode + " (" + flow_node_id + ")" + " created.");
			}

			createAssignment(instance_id, flow_node_id, flowNode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void createFlowNodeData(Long instance_id, Long flow_node_id, FlowNode flowNode) {
		List<XMLElement> properties = new ArrayList<XMLElement>();
		if (flowNode instanceof Event) {
			properties = ((Event) flowNode).getProperties().getXMLElements();
		} else if (flowNode instanceof Activity) {
			properties = ((Activity) flowNode).getProperties().getXMLElements();
		}
		for (XMLElement d : properties) {
			try {
				Property data = (Property) d;
				PreparedStatement ps = BaseEditor.dbConn
						.prepareStatement("INSERT INTO YAO_FLOW_NODE_DATA(PROCESS_INSTANCE_ID,FLOW_NODE_ID,DATA_ID,DATA_NAME,DATA_TYPE) VALUES(?,?,?,?,?);");
				ps.setLong(1, instance_id);
				ps.setLong(2, flow_node_id);
				ps.setString(3, data.getId());
				ps.setString(4, data.getName());
				ps.setString(5, data.getItemSubjectRef());
				ps.execute();
				printlog("FlowNode Data: " + data.getName() + " (" + data.getId() + ")" + " created.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	long createAssignment(Long instance_id, Long flow_node_id, FlowNode flowNode) {
		long assignment_id = 0;
		try {
			PreparedStatement ps = BaseEditor.dbConn
					.prepareStatement("INSERT INTO YAO_ASSIGNMENT(PROCESS_INSTANCE_ID,FLOW_NODE_ID,ASSIGNMENT_ID,ASSIGNMENT_NAME,IS_ACCEPTED) VALUES(?,?,?,?,?);");
			ps.setLong(1, instance_id);
			ps.setLong(2, flow_node_id);
			ps.setString(3, "simulator");
			ps.setString(4, "Simulator");
			ps.setBoolean(5, false);
			ps.execute();

			ResultSet rs = ps.getGeneratedKeys();
			while (rs.next()) {
				assignment_id = rs.getLong(1);
				break;
			}

			printlog("Assignment for FlowNode: " + flow_node_id + " created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return assignment_id;
	}

	Map<Long, Long> getAssignments(Long instance_id) {
		Map<Long, Long> assignments = new HashMap<Long, Long>();
		try {
			PreparedStatement ps = BPMNEditor.dbConn
					.prepareStatement("SELECT ID,FLOW_NODE_ID FROM YAO_ASSIGNMENT WHERE IS_ACCEPTED = 'FALSE' AND PROCESS_INSTANCE_ID = ?");
			ps.setLong(1, instance_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				assignments.put(rs.getLong(1), rs.getLong(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		printlog("Get Assignments for Process Intance " + instance_id + ", count: " + assignments.size());
		return assignments;
	}

	Set<FlowNode> acceptAssignments(Map<Long, BPMNProcess> processInstances) {
		Set<FlowNode> flowNodes = new HashSet<FlowNode>(waitNodes);
		for (Entry<Long, BPMNProcess> pi : processInstances.entrySet()) {
			Long instance_id = pi.getKey();
			BPMNProcess process = pi.getValue();
			Map<Long, Long> assignments = getAssignments(instance_id);
			for (Entry<Long, Long> assignment : assignments.entrySet()) {
				Long assignment_id = assignment.getKey();
				Long flow_node_id = assignment.getValue();
				acceptAssignment(assignment_id);
				FlowNode flowNode = getFlowNode(process, flow_node_id);
				flowNodes.add(flowNode);
				XMLElement parent = flowNode.getParent().getParent();
				if (parent instanceof SubProcess) {
					flowNodes.add((SubProcess) parent);
				}
				executeFlowNode(instance_id, flow_node_id, flowNodes, flowNode);
			}
		}
		return flowNodes;
	}

	void acceptAssignment(Long assignment_id) {
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_ASSIGNMENT SET IS_ACCEPTED = 'TRUE' WHERE ID = ?");
			ps.setLong(1, assignment_id);
			ps.executeUpdate();
			printlog("Assignment: " + assignment_id + " accepted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void deleteAssignment(Long flow_node_id) {
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_ASSIGNMENT WHERE IS_ACCEPTED = 'TRUE' AND FLOW_NODE_ID = ?");
			ps.setLong(1, flow_node_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void executeFlowNode(Long instance_id, Long flow_node_id, Set<FlowNode> flowNodes, FlowNode flowNode) {
		printlog("FlowNode: " + flowNode + " (" + flow_node_id + ")" + " is running...");
		String stateTime = Constants.DF.format(new Date());
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_FLOW_NODE SET STATE = ?, STATE_TIME = ? WHERE ID = ?");
			ps.setString(1, "open.running");
			ps.setString(2, stateTime);
			ps.setLong(3, flow_node_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (flowNode instanceof SubProcess) {
			executeProcess(instance_id, (SubProcess) flowNode);
		} else {
			boolean completed = true;
			for (SequenceFlow sf : flowNode.getIncomingSequenceFlows()) {
				FlowNode node = sf.getSourceFlowNode();
				if (flowNodes.contains(node)) {
					completed = false;
					break;
				}
				String state = getFlowNodeState(instance_id, node);
				completed = completed && "closed.completed".equals(state);
			}
			if (completed || flowNode instanceof ExclusiveGateway) {
				completeFlowNode(instance_id, flow_node_id, flowNode);
				waitNodes.remove(flowNode);
			} else {
				deleteAssignment(flow_node_id);
				waitNodes.add(flowNode);
			}
		}
	}

	void completeFlowNode(Long instance_id, Long flow_node_id, FlowNode flowNode) {
		String stateTime = Constants.DF.format(new Date());
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("UPDATE YAO_FLOW_NODE SET STATE = ?, STATE_TIME = ? WHERE ID = ?");
			ps.setString(1, "closed.completed");
			ps.setString(2, stateTime);
			ps.setLong(3, flow_node_id);
			ps.executeUpdate();

			ps = BPMNEditor.dbConn.prepareStatement("DELETE YAO_ASSIGNMENT WHERE IS_ACCEPTED = 'TRUE' AND FLOW_NODE_ID = ?");
			ps.setLong(1, flow_node_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		printlog("FlowNode: " + flowNode + " (" + flow_node_id + ")" + " finished.");

		boolean cancelActivity = false;
		XMLElement parent = flowNode.getParent();
		if (parent instanceof FlowElements) {
			Map<String, Set<BoundaryEvent>> boundaryEventMap = ((FlowElements) parent).getBoundaryEventMap();
			Set<BoundaryEvent> boundaryEvents = boundaryEventMap.get(flowNode.getId());
			if (boundaryEvents != null) {
				for (BoundaryEvent event : boundaryEvents) {
					boolean isTriggered = false;
					XMLElement valueEl = event.get("yaoqiang:value");
					if (valueEl != null) {
						isTriggered = Boolean.valueOf(valueEl.toValue());
					}
					if (isTriggered) {
						if (event.cancelActivity()) {
							cancelActivity = true;
						}
						createFlowNode(instance_id, event);
					}
				}
			}
		}
		if (cancelActivity) {
			return;
		}

		if (flowNode.getOutgoingSequenceFlows().isEmpty()) {
			XMLElement container = flowNode.getParent().getParent();
			if (container instanceof SubProcess) {
				SubProcess subProcess = (SubProcess) container;
				FlowNode node = null;
				for (Long id : getAssignments(instance_id).values()) {
					node = getFlowNode(subProcess, id);
					if (node != null) {
						break;
					}
				}
				if (node == null) {
					completeFlowNode(instance_id, getFlowNodeId(subProcess), subProcess);
				}
			}
		} else {
			boolean hasToken = true;
			SequenceFlow defaultSF = null;
			if (flowNode instanceof ExclusiveGateway) {
				defaultSF = ((ExclusiveGateway) flowNode).getDefaultSequenceFlow();
			} else if (flowNode instanceof Activity) {
				defaultSF = ((Activity) flowNode).getDefaultSequenceFlow();
			}
			for (SequenceFlow sf : flowNode.getOutgoingSequenceFlows()) {
				if (defaultSF == sf) {
					continue;
				}
				boolean isTrue = false;
				if (flowNode instanceof EventBasedGateway) {
					XMLElement valueEl = flowNode.get("yaoqiang:value");
					if (valueEl == null) {
						isTrue = true;
					} else {
						isTrue = sf.getId().equals(valueEl.toValue());
					}
				} else {
					String expr = sf.getConditionExpression().toValue();
					if (expr.trim().length() != 0) {
						XMLElement valueEl = sf.get("yaoqiang:value");
						if (valueEl == null) {
							isTrue = true;
						} else {
							if ("Dynamic".equals(valueEl.toValue())) {
								isTrue = evaluateCondition(sf);
							} else {
								isTrue = Boolean.valueOf(valueEl.toValue());
							}
						}
					} else {
						isTrue = true;
					}
				}

				if (isTrue) {
					FlowNode node = sf.getTargetFlowNode();
					createFlowNode(instance_id, node);
					if (flowNode instanceof ExclusiveGateway || flowNode instanceof EventBasedGateway) {
						hasToken = false;
						break;
					}
				}
			}

			if (defaultSF != null && hasToken) {
				FlowNode node = defaultSF.getTargetFlowNode();
				createFlowNode(instance_id, node);
			}
		}
	}

	String getFlowNodeState(Long instance_id, FlowNode flowNode) {
		String state = "";
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT STATE FROM YAO_FLOW_NODE WHERE PROCESS_INSTANCE_ID = ? AND FLOW_NODE_ID = ?");
			ps.setLong(1, instance_id);
			ps.setString(2, flowNode.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				state = rs.getString(1);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return state;
	}

	long getFlowNodeId(FlowNode flowNode) {
		long flow_node_id = 0;
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT ID FROM YAO_FLOW_NODE WHERE FLOW_NODE_ID = ?");
			ps.setString(1, flowNode.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				flow_node_id = rs.getLong(1);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return flow_node_id;
	}

	FlowNode getFlowNode(FlowElementsContainer container, Long flow_node_id) {
		FlowNode flowNode = null;
		try {
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT FLOW_NODE_ID FROM YAO_FLOW_NODE WHERE ID = ?");
			ps.setLong(1, flow_node_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				flowNode = (FlowNode) BPMNModelUtils.getFlowElement(container, rs.getString(1));
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flowNode;
	}

	public void highLightNodes(Set<FlowNode> nodes) {
		List<Object> cells = new ArrayList<Object>();
		for (FlowNode flowNode : nodes) {
			mxCell cell = (mxCell) model.getCell(flowNode.getId());
			String style = cell.getStyle();
			cells.add(cell);
			previousStyles.put(cell, style);
			if (model.isExpandedSubProcess(cell)) {
				cell.setStyle(style + ";strokeColor=#00FF00");
			} else {
				cell.setStyle(style + ";strokeColor=#00FF00;fillColor=#FFFF00;gradientColor=#CFCF00");
			}
		}
	}

	public void unHighLightNodes(Set<FlowNode> nodes) {
		for (FlowNode flowNode : nodes) {
			mxCell cell = (mxCell) model.getCell(flowNode.getId());
			String style = previousStyles.remove(cell);
			if (style != null) {
				cell.setStyle(style);
			}
		}
	}

	public static void reset() {
		flowNodeList.clear();
		for (Entry<mxCell, String> cellStyle : previousStyles.entrySet()) {
			cellStyle.getKey().setStyle(cellStyle.getValue());
		}
		previousStyles.clear();
		graph.refresh();
	}

	public List<Set<FlowNode>> getNodeList() {
		return flowNodeList;
	}

	public boolean evaluateCondition(SequenceFlow sequenceFlow) {
//		String condition = sequenceFlow.getConditionExpression().toValue();
//		if (condition == null || condition.trim().length() == 0) {
//			return true;
//		}
//		boolean result = true;
//		Context cx = Context.enter();
//		try {
//			Scriptable scope = cx.initStandardObjects();
//			for (XMLElement prop : sequenceFlow.getAccessibleProperties()) {
//				Object value = "";
//				XMLElement valueEl = ((Property) prop).get("yaoqiang:value");
//				if (valueEl != null) {
//					value = valueEl.toValue();
//				}
//				scope.put(((Property) prop).getName(), scope, value);
//			}
//			result = (Boolean) cx.evaluateString(scope, condition, "", 1, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			Context.exit();
//		}
//		return result;
		return true;
	}

	void printlog(String log) {
		if (debug) {
			System.out.println(log);
		}
	}

}
