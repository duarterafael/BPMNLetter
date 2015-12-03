package org.yaoqiang.bpmn.editor.swing;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.addon.Addon;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLComplexElement;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.LoopTypes;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.data.DataObjectReference;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.events.IntermediateCatchEvent;
import org.yaoqiang.bpmn.model.elements.events.IntermediateThrowEvent;
import org.yaoqiang.bpmn.model.elements.events.StartEvent;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.util.Constants;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.graph.view.Graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

/**
 * PopupMenu
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class PopupMenu extends JPopupMenu {

	private static final long serialVersionUID = -3132749140550242191L;

	protected BaseEditor editor;

	protected Graph graph;

	protected GraphModel model;

	protected mxCell cell;

	protected String wrapstyle = "";

	protected boolean wrapped = true;

	protected void populateEventMenu() {
		Event event = (Event) cell.getValue();
		
		if (model.isStartEvent(cell)) {
			JMenu eventmenu = (JMenu) add(new JMenu(mxResources.get("eventType")));
			Object parent = graph.getModel().getParent(cell);
			if (model.isEventSubProcess(parent)) {
				if (((StartEvent) event).isInterrupting()) {
					eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_message.png"));
					eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_timer.png"));
					eventmenu.add(editor.bind(mxResources.get("errorEvent"), ModelActions.getChangeEventTypeAction("errorEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_error.png"));
					eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_escalation.png"));
					eventmenu.add(editor.bind(mxResources.get("compensationEvent"), ModelActions.getChangeEventTypeAction("compensateEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_compensate.png"));
					eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
					eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_signal.png"));
					eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
							"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
					eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
							"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));
				} else {
					eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_message.png"));
					eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_timer.png"));
					eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_escalation.png"));
					eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
					eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_signal.png"));
					eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
							"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
					eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
							"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));
				}
			} else if (model.isSubProcess(parent)) {
				eventmenu.add(editor.bind(mxResources.get("noneEvent"), ModelActions.getChangeEventTypeAction("")));
			} else if (!model.isSubProcess(parent)) {
				eventmenu.add(editor.bind(mxResources.get("noneEvent"), ModelActions.getChangeEventTypeAction("")));
				eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
						"/org/yaoqiang/graph/shape/markers/event_message.png"));
				eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
						"/org/yaoqiang/graph/shape/markers/event_timer.png"));
				eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
						"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
				eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
						"/org/yaoqiang/graph/shape/markers/event_signal.png"));
				eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
						"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
				eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
						"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));
			}
		} else if (model.isIntermediateEvent(cell)) {
			if (model.isBoundaryEvent(cell)) {
				if (graph.getConnections(cell).length == 0 || !model.isCompensationEvent(cell)) {
					JMenu eventmenu = (JMenu) add(new JMenu(mxResources.get("eventType")));
					if (((BoundaryEvent) event).cancelActivity()) {
						eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_message.png"));
						eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_timer.png"));
						eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_escalation.png"));
						eventmenu.add(editor.bind(mxResources.get("errorEvent"), ModelActions.getChangeEventTypeAction("errorEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_error.png"));
						if (model.isTransactionSubProcess(model.getParent(cell))) {
							eventmenu.add(editor.bind(mxResources.get("cancelEvent"), ModelActions.getChangeEventTypeAction("cancelEventDefinition"),
									"/org/yaoqiang/graph/shape/markers/event_cancel.png"));
						}
						if (graph.getConnections(cell).length == 0) {
							eventmenu.add(editor.bind(mxResources.get("compensationEvent"), ModelActions.getChangeEventTypeAction("compensateEventDefinition"),
									"/org/yaoqiang/graph/shape/markers/event_compensate.png"));
						}
						eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
						eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_signal.png"));
						eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
								"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
						eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
								"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));
					} else {
						eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_message.png"));
						eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_timer.png"));
						eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_escalation.png"));
						eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
						eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_signal.png"));
						eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
								"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
						eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
								"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));
					}
				}
			} else {
				JMenu eventmenu = (JMenu) add(new JMenu(mxResources.get("eventType")));
				Object[] incomingEdges = graph.getIncomingEdges(cell);
				if (incomingEdges.length > 0 && model.isEventGateway(model.getTerminal(incomingEdges[0], true))) {
					// P336 For Event Gateway, the following Intermediate Event
					// triggers are not valid: Error,
					// Cancel, Compensation, and Link.
				} else {
					if (event instanceof IntermediateCatchEvent) {
						eventmenu.add(editor.bind(mxResources.get("linkEvent"), ModelActions.getChangeEventTypeAction("linkEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_link.png"));
					} else {
						eventmenu.add(editor.bind(mxResources.get("noneEvent"), ModelActions.getChangeEventTypeAction("")));
						eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_escalation_throwing.png"));
						eventmenu.add(editor.bind(mxResources.get("compensationEvent"), ModelActions.getChangeEventTypeAction("compensateEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_compensate_throwing.png"));
						eventmenu.add(editor.bind(mxResources.get("linkEvent"), ModelActions.getChangeEventTypeAction("linkEventDefinition"),
								"/org/yaoqiang/graph/shape/markers/event_link_throwing.png"));
					}
				}
				if (event instanceof IntermediateCatchEvent) {
					eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_message.png"));
					eventmenu.add(editor.bind(mxResources.get("timerEvent"), ModelActions.getChangeEventTypeAction("timerEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_timer.png"));
					eventmenu.add(editor.bind(mxResources.get("conditionalEvent"), ModelActions.getChangeEventTypeAction("conditionalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_conditional.png"));
					eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_signal.png"));
					eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
							"/org/yaoqiang/graph/shape/markers/event_multiple.png"));
					eventmenu.add(editor.bind(mxResources.get("parallelMultipleEvent"), ModelActions.getChangeEventTypeAction("parallelMultiple"),
							"/org/yaoqiang/graph/shape/markers/event_parallel_multiple.png"));

				} else if (event instanceof IntermediateThrowEvent) {
					eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_message_throwing.png"));
					eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
							"/org/yaoqiang/graph/shape/markers/event_signal_throwing.png"));
					eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
							"/org/yaoqiang/graph/shape/markers/event_multiple_throwing.png"));

				}
			}
		} else if (model.isEndEvent(cell)) {
			JMenu eventmenu = (JMenu) add(new JMenu(mxResources.get("eventType")));
			eventmenu.add(editor.bind(mxResources.get("noneEvent"), ModelActions.getChangeEventTypeAction("")));
			eventmenu.add(editor.bind(mxResources.get("messageEvent"), ModelActions.getChangeEventTypeAction("messageEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_message_throwing.png"));
			eventmenu.add(editor.bind(mxResources.get("escalationEvent"), ModelActions.getChangeEventTypeAction("escalationEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_escalation_throwing.png"));
			eventmenu.add(editor.bind(mxResources.get("errorEvent"), ModelActions.getChangeEventTypeAction("errorEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_error_throwing.png"));
			if (model.isTransactionSubProcess(model.getParent(cell))) {
				eventmenu.add(editor.bind(mxResources.get("cancelEvent"), ModelActions.getChangeEventTypeAction("cancelEventDefinition"),
						"/org/yaoqiang/graph/shape/markers/event_cancel_throwing.png"));
			}
			eventmenu.add(editor.bind(mxResources.get("compensationEvent"), ModelActions.getChangeEventTypeAction("compensateEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_compensate_throwing.png"));
			eventmenu.add(editor.bind(mxResources.get("signalEvent"), ModelActions.getChangeEventTypeAction("signalEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_signal_throwing.png"));
			eventmenu.add(editor.bind(mxResources.get("multipleEvent"), ModelActions.getChangeEventTypeAction("multiple"),
					"/org/yaoqiang/graph/shape/markers/event_multiple_throwing.png"));
			eventmenu.add(editor.bind(mxResources.get("terminateEvent"), ModelActions.getChangeEventTypeAction("terminateEventDefinition"),
					"/org/yaoqiang/graph/shape/markers/event_terminate_throwing.png"));
		}
		if (!model.isNoneEvent(cell)) {
			if (model.isMultipleEvent(cell)) {
				add(editor.bind(mxResources.get("eventDefinitions"), ModelActions.getAction(ModelActions.EVENT_DEFINITIONS)));
			} else {
				add(editor.bind(mxResources.get("eventDefinition"), ModelActions.getAction(ModelActions.EVENT_DEFINITION)));
			}
		}
		add(editor.bind(mxResources.get("dataProperties"), ModelActions.getAction(ModelActions.DATA_PROPERTIES)));
		if (model.isThrowEvent(cell)) {
			add(editor.bind(mxResources.get("datainputs"), ModelActions.getAction(ModelActions.DATA_INOUT)));
		} else {
			add(editor.bind(mxResources.get("dataoutputs"), ModelActions.getAction(ModelActions.DATA_INOUT)));
		}
	}

	protected void populateGatewayMenu() {
		JMenu gatewaymenu = (JMenu) add(new JMenu(mxResources.get("gatewayType")));
		gatewaymenu.add(editor.bind(mxResources.get("exclusiveGateway"), ModelActions.getChangeFlowElementTypeAction("exclusiveGateway")));
		gatewaymenu.add(editor.bind(mxResources.get("exclusiveGatewayWithIndicator"),
				ModelActions.getChangeFlowElementTypeAction("exclusiveGatewayWithIndicator"), "/org/yaoqiang/graph/shape/markers/gateway_exclusive.png"));
		gatewaymenu.add(editor.bind(mxResources.get("inclusiveGateway"), ModelActions.getChangeFlowElementTypeAction("inclusiveGateway"),
				"/org/yaoqiang/graph/shape/markers/gateway_inclusive.png"));
		gatewaymenu.add(editor.bind(mxResources.get("complexGateway"), ModelActions.getChangeFlowElementTypeAction("complexGateway"),
				"/org/yaoqiang/graph/shape/markers/gateway_complex.png"));
		if (!model.hasDefaultSequenceFlow(cell)) {
			gatewaymenu.add(editor.bind(mxResources.get("parallelGateway"), ModelActions.getChangeFlowElementTypeAction("parallelGateway"),
					"/org/yaoqiang/graph/shape/markers/gateway_parallel.png"));
			gatewaymenu.add(editor.bind(mxResources.get("eventGateway"), ModelActions.getChangeFlowElementTypeAction("eventBasedGateway"),
					"/org/yaoqiang/graph/shape/markers/gateway_event.png"));
			gatewaymenu.add(editor.bind(mxResources.get("eventGatewayInstantiate"), ModelActions.getChangeFlowElementTypeAction("eventGatewayInstantiate"),
					"/org/yaoqiang/graph/shape/markers/gateway_event_instantiate.png"));
			gatewaymenu.add(editor.bind(mxResources.get("parallelEventGateway"), ModelActions.getChangeFlowElementTypeAction("parallelEventGateway"),
					"/org/yaoqiang/graph/shape/markers/gateway_event_parallel.png"));
		}
		addSeparator();
	}

	protected void populateDefaultSequenceFlowMenu() {
		if (model.canHasDefaultSequenceFlow(cell) && graph.getOutgoingEdges(cell).length > 1) {
			List<Object> sfList = new ArrayList<Object>();
			for (Object edge : graph.getOutgoingEdges(cell)) {
				if (model.isSequenceFlow(edge) && !model.isConditionalSequenceFlow(edge)) {
					sfList.add(edge);
				}
			}
			if (sfList.size() > 0) {
				JMenu defaultSequenceFlowmenu = (JMenu) add(new JMenu(mxResources.get("setDefaultSequenceFlow")));
				if (sfList.size() > 0) {
					for (Object e : sfList) {
						mxCell edge = (mxCell) e;
						defaultSequenceFlowmenu.add(editor.bind(edge.getValue().toString(), ModelActions.getDefaultSequenceFlowAction(edge.getId())));
					}
				}
				if (model.hasDefaultSequenceFlow(cell)) {
					defaultSequenceFlowmenu.addSeparator();
					defaultSequenceFlowmenu.add(editor.bind(mxResources.get("none"), ModelActions.getDefaultSequenceFlowAction("")));
				}
			}
		}
	}

	protected void populateTaskAndSubProcessMenu() {
		if (model.isSubProcess(cell)) {
			JMenu subprocessmenu = (JMenu) add(new JMenu(mxResources.get("subProcessType")));
			subprocessmenu.add(editor.bind(mxResources.get("subProcess"), ModelActions.getChangeFlowElementTypeAction("subProcess")));
			subprocessmenu.add(editor.bind(mxResources.get("eventSubProcess"), ModelActions.getChangeFlowElementTypeAction("eventSubProcess")));
			subprocessmenu.add(editor.bind(mxResources.get("tranSubProcess"), ModelActions.getChangeFlowElementTypeAction("transaction")));
			subprocessmenu.add(editor.bind(mxResources.get("adHocSubProcess"), ModelActions.getChangeFlowElementTypeAction("adHocSubProcess")));
		} else if (model.isCallActivity(cell) && !model.isCallProcess(cell)) {
			JMenu taskmenu = (JMenu) add(new JMenu(mxResources.get("globalTaskType")));
			taskmenu.add(editor.bind(mxResources.get("callActivity"), ModelActions.getChangeFlowElementTypeAction("globalTask")));
			taskmenu.add(editor.bind(mxResources.get("callUser"), ModelActions.getChangeFlowElementTypeAction("globalUserTask"),
					"/org/yaoqiang/graph/shape/markers/task_user.png"));
			taskmenu.add(editor.bind(mxResources.get("callManual"), ModelActions.getChangeFlowElementTypeAction("globalManualTask"),
					"/org/yaoqiang/graph/shape/markers/task_manual.png"));
			taskmenu.add(editor.bind(mxResources.get("callScript"), ModelActions.getChangeFlowElementTypeAction("globalScriptTask"),
					"/org/yaoqiang/graph/shape/markers/task_script.png"));
			taskmenu.add(editor.bind(mxResources.get("callBusinessRule"), ModelActions.getChangeFlowElementTypeAction("globalBusinessRuleTask"),
					"/org/yaoqiang/graph/shape/markers/task_businessrule.png"));
		} else if (model.isTask(cell)) {
			JMenu taskmenu = (JMenu) add(new JMenu(mxResources.get("taskType")));
			taskmenu.add(editor.bind(mxResources.get("task"), ModelActions.getChangeFlowElementTypeAction("task")));
			taskmenu.add(editor.bind(mxResources.get("sendTask"), ModelActions.getChangeFlowElementTypeAction("sendTask"),
					"/org/yaoqiang/graph/shape/markers/task_send.png"));
			taskmenu.add(editor.bind(mxResources.get("receiveTask"), ModelActions.getChangeFlowElementTypeAction("receiveTask"),
					"/org/yaoqiang/graph/shape/markers/task_receive.png"));
			taskmenu.add(editor.bind(mxResources.get("serviceTask"), ModelActions.getChangeFlowElementTypeAction("serviceTask"),
					"/org/yaoqiang/graph/shape/markers/task_service.png"));
			taskmenu.add(editor.bind(mxResources.get("userTask"), ModelActions.getChangeFlowElementTypeAction("userTask"),
					"/org/yaoqiang/graph/shape/markers/task_user.png"));
			taskmenu.add(editor.bind(mxResources.get("manualTask"), ModelActions.getChangeFlowElementTypeAction("manualTask"),
					"/org/yaoqiang/graph/shape/markers/task_manual.png"));
			taskmenu.add(editor.bind(mxResources.get("scriptTask"), ModelActions.getChangeFlowElementTypeAction("scriptTask"),
					"/org/yaoqiang/graph/shape/markers/task_script.png"));
			taskmenu.add(editor.bind(mxResources.get("businessRuleTask"), ModelActions.getChangeFlowElementTypeAction("businessRuleTask"),
					"/org/yaoqiang/graph/shape/markers/task_businessrule.png"));
		}

		JMenu loopmenu = (JMenu) add(new JMenu(mxResources.get("loopType")));
		loopmenu.add(editor.bind(mxResources.get("loopNone"), ModelActions.getChangeActivityLoopTypeAction("", false)));
		loopmenu.add(editor.bind(mxResources.get("loopStandard"),
				ModelActions.getChangeActivityLoopTypeAction(LoopTypes.TYPE_STANDARD_LOOPCHARACTERISTICS, false),
				"/org/yaoqiang/graph/shape/markers/loop_standard.png"));
		loopmenu.add(editor.bind(mxResources.get("loopMultiInstance"),
				ModelActions.getChangeActivityLoopTypeAction(LoopTypes.TYPE_MULTI_INSTANCE_LOOPCHARACTERISTICS, false),
				"/org/yaoqiang/graph/shape/markers/loop_multiple.png"));
		loopmenu.add(editor.bind(mxResources.get("loopMultiInstanceSequential"),
				ModelActions.getChangeActivityLoopTypeAction(LoopTypes.TYPE_MULTI_INSTANCE_LOOPCHARACTERISTICS, true),
				"/org/yaoqiang/graph/shape/markers/loop_multiple_sequential.png"));

		JMenu compensationmenu = (JMenu) add(new JMenu(mxResources.get("compensation")));
		compensationmenu.add(editor.bind(mxResources.get("true"), ModelActions.getChangeActivityCompensationTypeAction(true)));
		compensationmenu.add(editor.bind(mxResources.get("false"), ModelActions.getChangeActivityCompensationTypeAction(false)));

		if (model.isReceiveTask(cell)) {
			JMenu instantiatemenu = (JMenu) add(new JMenu(mxResources.get("instantiate")));
			instantiatemenu.add(editor.bind(mxResources.get("true"), ModelActions.getChangeReceiveTaskInstantiateTypeAction(true)));
			instantiatemenu.add(editor.bind(mxResources.get("false"), ModelActions.getChangeReceiveTaskInstantiateTypeAction(false)));
		}
		if (model.isLoopActivity(cell)) {
			add(editor.bind(mxResources.get("loopCharacteristics"), ModelActions.getAction(ModelActions.LOOP_CHARACTERISTICS)));
		}
		if (model.isTask(cell) && !model.isManualTask(cell) && !model.isAbstractTask(cell) || model.isCallActivity(cell)) {
			add(editor.bind(mxResources.get("taskProperties"), ModelActions.getAction()));
		}
		if (model.isAdhocSubProcess(cell)) {
			add(editor.bind(mxResources.get("adHocProperties"), ModelActions.getAction()));
		}
		if (model.isTransactionSubProcess(cell)) {
			add(editor.bind(mxResources.get("tranProperties"), ModelActions.getAction()));
		}
		add(editor.bind(mxResources.get("dataProperties"), ModelActions.getAction(ModelActions.DATA_PROPERTIES)));
		if (model.isSubProcess(cell)) {
			add(editor.bind(mxResources.get("dataobjects"), ModelActions.getAction(ModelActions.DATA_OBJECTS)));
		} else {
			add(editor.bind(mxResources.get("dataInputOutputs"), ModelActions.getAction(ModelActions.DATA_INOUT)));
		}
		
	}

	protected void populateChoreographyMenu() {
		JMenu initmenu = (JMenu) add(new JMenu(mxResources.get("initiatingParticipant")));
		Object[] partCells = mxGraphModel.getChildVertices(graph.getModel(), cell);
		for (int i = 0; i < partCells.length; i++) {
			if (model.isChoreographyParticipant(partCells[i])) {
				String partName = ((mxCell) partCells[i]).getValue().toString();
				if (partName.length() == 0) {
					if (graph.isTopChoreographyParticipant(partCells[i])) {
						partName = mxResources.get("topParticipant");
					} else {
						partName = mxResources.get("bottomParticipant");
					}
				}
				initmenu.add(editor.bind(partName, GraphActions.getInitPartAction(((mxCell) partCells[i]).getValue())));
			}
		}

		JMenu multipartmenu = (JMenu) add(new JMenu(mxResources.get("multiParticipant")));
		for (int i = 0; i < partCells.length; i++) {
			if (model.isChoreographyParticipant(partCells[i])) {
				String partName = ((mxCell) partCells[i]).getValue().toString();
				JMenu submenu = (JMenu) multipartmenu.add(new JMenu(partName));
				submenu.add(editor.bind(mxResources.get("true"), GraphActions.getMultiPartAction(((mxCell) partCells[i]).getValue(), "2")));
				submenu.add(editor.bind(mxResources.get("false"), GraphActions.getMultiPartAction(((mxCell) partCells[i]).getValue(), "1")));
			}
		}

		JMenu loopmenu = (JMenu) add(new JMenu(mxResources.get("loopType")));
		loopmenu.add(editor.bind(mxResources.get("loopNone"), ModelActions.getChangeActivityLoopTypeAction("None", false)));
		loopmenu.add(editor.bind(mxResources.get("loopStandard"), ModelActions.getChangeActivityLoopTypeAction("Standard", false),
				"/org/yaoqiang/graph/shape/markers/loop_standard.png"));
		loopmenu.add(editor.bind(mxResources.get("loopMultiInstance"), ModelActions.getChangeActivityLoopTypeAction("MultiInstanceParallel", false),
				"/org/yaoqiang/graph/shape/markers/loop_multiple.png"));
		loopmenu.add(editor.bind(mxResources.get("loopMultiInstanceSequential"), ModelActions.getChangeActivityLoopTypeAction("MultiInstanceSequential", true),
				"/org/yaoqiang/graph/shape/markers/loop_multiple_sequential.png"));
		add(editor.bind(mxResources.get("swapParticipants"), GraphActions.getSwapPartAction()));
	}

	protected void populateSubChoreographyMenu() {
		Object[] partCells = mxGraphModel.getChildVertices(graph.getModel(), cell);
		JMenu addmenu = (JMenu) add(new JMenu(mxResources.get("addParticipant")));
		addmenu.add(editor.bind(mxResources.get("toTop"), GraphActions.getAddPartAction(cell, true)));
		addmenu.add(editor.bind(mxResources.get("toBottom"), GraphActions.getAddPartAction(cell, false)));

		JMenu deletemenu = (JMenu) add(new JMenu(mxResources.get("deleteParticipant")));
		for (int i = 0; i < partCells.length; i++) {
			if (graph.isAdditionalChoreographyParticipant(partCells[i])) {
				String partName = ((mxCell) partCells[i]).getValue().toString();
				deletemenu.add(editor.bind(partName, GraphActions.getDelPartAction(partCells[i])));
			}
		}
	}

	protected void populateParticipantMenu() {
		add(editor.bind(mxResources.get("partProperties"), ModelActions.getAction()));
		JMenu multipartmenu = (JMenu) add(new JMenu(mxResources.get("multiParticipant")));
		multipartmenu.add(editor.bind(mxResources.get("true"), GraphActions.getMultiPartAction(cell.getValue(), "2")));
		multipartmenu.add(editor.bind(mxResources.get("false"), GraphActions.getMultiPartAction(cell.getValue(), "1")));
		addSeparator();
	}

	protected void populateDataObjectMenu() {
		XMLElement el = null;
		if (cell != null) {
			el = (XMLElement) cell.getValue();
		}
		if (el != null) {
			XMLComplexElement data = (XMLComplexElement) el;
			if (el instanceof DataObjectReference) {
				data = ((DataObjectReference) el).getRefDataObject();
			}
			if (data != null && data.get("itemSubjectRef").isEmpty()) {
				JMenu collectionmenu = (JMenu) add(new JMenu(mxResources.get("collection")));
				collectionmenu.add(editor.bind(mxResources.get("true"), ModelActions.getChangeDataCollectionType(true)));
				collectionmenu.add(editor.bind(mxResources.get("false"), ModelActions.getChangeDataCollectionType(false)));
			}
		}

		if (model.isDataInput(cell) || model.isDataOutput(cell)) {
			if (!graph.hasDataAssociationConnections(cell)) {
				JMenu dataobjectmenu = (JMenu) add(new JMenu(mxResources.get("dataObjectType")));
				dataobjectmenu.add(editor.bind(mxResources.get("dataInput"), ModelActions.getChangeDataType("dataInput")));
				dataobjectmenu.add(editor.bind(mxResources.get("dataOutput"), ModelActions.getChangeDataType("dataOutput")));
			}
			add(editor.bind(mxResources.get(model.isDataInput(cell) ? "dataInputProperties" : "dataOutputProperties"), ModelActions.getAction()));
		} else {
			add(editor.bind(mxResources.get("editDataState"), ModelActions.getAction(ModelActions.DATA_STATE), "/org/yaoqiang/bpmn/editor/images/edit.png"));
		}

		addSeparator();
	}

	protected void populateAlignMenu() {
		addSeparator();

		JMenu alignmenu = (JMenu) add(new JMenu(mxResources.get("align")));
		alignmenu.add(editor.bind(mxResources.get("alignleft"), GraphActions.getAction(GraphActions.ALIGN_LEFT),
				"/org/yaoqiang/bpmn/editor/images/alignleft.gif"));
		alignmenu.add(editor.bind(mxResources.get("aligncenter"), GraphActions.getAction(GraphActions.ALIGN_CENTER),
				"/org/yaoqiang/bpmn/editor/images/aligncenter.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignright"), GraphActions.getAction(GraphActions.ALIGN_RIGHT),
				"/org/yaoqiang/bpmn/editor/images/alignright.gif"));

		alignmenu.addSeparator();

		alignmenu
				.add(editor.bind(mxResources.get("aligntop"), GraphActions.getAction(GraphActions.ALIGN_TOP), "/org/yaoqiang/bpmn/editor/images/aligntop.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignmiddle"), GraphActions.getAction(GraphActions.ALIGN_MIDDLE),
				"/org/yaoqiang/bpmn/editor/images/alignmiddle.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignbottom"), GraphActions.getAction(GraphActions.ALIGN_BOTTOM),
				"/org/yaoqiang/bpmn/editor/images/alignbottom.gif"));

		alignmenu.addSeparator();

		alignmenu.add(editor.bind(mxResources.get("sameheight"), GraphActions.getAction(GraphActions.SAME_HEIGHT),
				"/org/yaoqiang/bpmn/editor/images/sameheight.gif"));
		alignmenu.add(editor.bind(mxResources.get("samewidth"), GraphActions.getAction(GraphActions.SAME_WIDTH),
				"/org/yaoqiang/bpmn/editor/images/samewidth.gif"));
		alignmenu.add(editor.bind(mxResources.get("samesize"), GraphActions.getAction(GraphActions.SAME), "/org/yaoqiang/bpmn/editor/images/samesize.gif"));

		alignmenu.addSeparator();

		alignmenu.add(editor.bind(mxResources.get("distributeH"), GraphActions.getAction(GraphActions.DISTRIBUTE_HORIZONTALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_horizontally.png"));
		alignmenu.add(editor.bind(mxResources.get("distributeV"), GraphActions.getAction(GraphActions.DISTRIBUTE_VERTICALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_vertically.png"));
	}

	protected void populateViewMenu() {
		if (graph.getCurrentRoot() != graph.getModel().getCell(editor.getCurrentGraphComponent().getName())) {
			add(editor.bind(mxResources.get("home"), GraphActions.getAction(GraphActions.HOME), "/org/yaoqiang/bpmn/editor/images/home.png"));
			add(editor
					.bind(mxResources.get("exitSubprocess"), GraphActions.getAction(GraphActions.EXIT_GROUP), "/org/yaoqiang/bpmn/editor/images/exit_box.png"));
		}
		if (model.isSubProcess(cell) && graph.getSelectionCount() == 1) {
			add(editor.bind(mxResources.get("enterSubprocess"), GraphActions.getAction(GraphActions.ENTER_GROUP),
					"/org/yaoqiang/bpmn/editor/images/enter_box.png"));
			add(editor.bind(mxResources.get("disassembleSubprocess"), GraphActions.getAction(GraphActions.UNGROUP),
					"/org/yaoqiang/bpmn/editor/images/ungroup.png"));
			addSeparator();
		} else {
			if (graph.canBeAssembled()) {
				add(editor
						.bind(mxResources.get("assembleSubprocess"), GraphActions.getAction(GraphActions.GROUP), "/org/yaoqiang/bpmn/editor/images/group.png"));
				addSeparator();
			} else if (graph.getCurrentRoot() != graph.getModel().getCell(editor.getCurrentGraphComponent().getName())) {
				addSeparator();
			}
		}

		if (model.isFlowNode(cell) && graph.getSelectionCount() == 1) {
			JMenu submenu = (JMenu) add(new JMenu(mxResources.get("selection")));
			submenu.add(editor.bind(mxResources.get("selectRightOfProcess"), GraphActions.getSelectProcessAction("right")));
			submenu.add(editor.bind(mxResources.get("selectBottomOfProcess"), GraphActions.getSelectProcessAction("bottom")));
			submenu.add(editor.bind(mxResources.get("selectLeftOfProcess"), GraphActions.getSelectProcessAction("left")));
			submenu.add(editor.bind(mxResources.get("selectTopOfProcess"), GraphActions.getSelectProcessAction("top")));
			submenu.add(editor.bind(mxResources.get("selectEntireProcess"), GraphActions.getSelectProcessAction("entrire")));
		}

		if (cell == null) {
			JMenu addmenu = (JMenu) add(new JMenu(mxResources.get("addPage")));
			addmenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getAddPageAction(true)));
			addmenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getAddPageAction(false)));
			JMenu removemenu = (JMenu) add(new JMenu(mxResources.get("removePage")));
			removemenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getRemovePageAction(true)));
			removemenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getRemovePageAction(false)));
			addSeparator();
			add(editor.bind(mxResources.get("selectAll"), GraphActions.getAction(GraphActions.SELECT_ALL), "/org/yaoqiang/bpmn/editor/images/select_all.png"));
		}
	}

	protected void populateEditMenu() {
		add(editor.bind(mxResources.get("editLabel"), GraphActions.getAction(GraphActions.EDIT), "/org/yaoqiang/bpmn/editor/images/edit.png"));
		if (graph.isSwimlane(cell)) {
			add(editor.bind(mxResources.get("paste"), GraphActions.getAction(GraphActions.PASTE), "/org/yaoqiang/bpmn/editor/images/paste.png"));
		} else if (graph.getModel().isEdge(cell)) {
			mxICell line = (mxICell) cell;
			if (model.isSequenceFlow(line) && !model.isDefaultSequenceFlow(line) && !model.isEvent(line.getTerminal(true))
					&& !model.isEventGateway(line.getTerminal(true)) && !model.isParallelGateway(line.getTerminal(true))) {
				add(editor.bind(mxResources.get("editExpression"), ModelActions.getAction(ModelActions.CONDITION_EXPRESSION),
						"/org/yaoqiang/bpmn/editor/images/edit.png"));
			} else if (model.isDataAssociation(line)) {
				add(editor.bind(mxResources.get("dataAssociationProperties"), ModelActions.getAction(), "/org/yaoqiang/bpmn/editor/images/edit.png"));
			}
			add(editor.bind(mxResources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));
		} else {
			populateCommonEditMenu();
		}
	}

	protected void populateCommonEditMenu() {
		populateCallActivityEditMenu();
		add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/org/yaoqiang/bpmn/editor/images/cut.png"));
		add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/org/yaoqiang/bpmn/editor/images/copy.png"));
		add(editor.bind(mxResources.get("paste"), GraphActions.getAction(GraphActions.PASTE), "/org/yaoqiang/bpmn/editor/images/paste.png"));
		add(editor.bind(mxResources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));
	}

	protected void populateCallActivityEditMenu() {
		if (model.isCallProcess(cell)) {
			XMLElement el = null;
			if (cell != null) {
				el = (XMLElement) model.getValue(cell);
			}
			String calledElement = ((CallActivity) el).getCalledElement();
			int index = calledElement.indexOf(":");
			String ns = getGraph().getBpmnModel().getNamespaces().get(getGraph().getBpmnModel().getTargetNamespace());
			if (calledElement.equals("callProcess") || (index == -1 || calledElement.substring(0, index).equals(ns))
					&& getGraph().getBpmnModel().getRootElement(calledElement) != null) {
				add(editor.bind(mxResources.get("editCalledProcess"), EditorActions.getAction(EditorActions.EDIT_CALLED_PROCESS),
						"/org/yaoqiang/bpmn/editor/images/edit.png"));
			}
		}
	}

	protected void populateStyleMenu() {
		if (graph.getModel().isEdge(cell)) {
			mxICell line = (mxICell) cell;
			if (model.isAnnotation(line.getTerminal(true)) || model.isAnnotation(line.getTerminal(false))) {
			} else if (model.isConversationNode(line.getTerminal(true)) || model.isConversationNode(line.getTerminal(false))) {
				addSeparator();
				JMenu edgemenu = (JMenu) add(new JMenu(mxResources.get("edgeStyle")));
				edgemenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getStyleAction("horizontal"),
						"/org/yaoqiang/bpmn/editor/images/connect.gif"));
				edgemenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getStyleAction("vertical"), "/org/yaoqiang/bpmn/editor/images/vertical.gif"));
			} else if (model.isMessageFlow(line) || model.isConditionalSequenceFlow(line) && !model.isGateway(line.getTerminal(true))
					|| model.isDefaultSequenceFlow(line)) {

			} else {
				addSeparator();
				JMenu edgemenu = (JMenu) add(new JMenu(mxResources.get("edgeStyle")));
				edgemenu.add(editor.bind(mxResources.get("straight"), GraphActions.getStyleAction("straight"), "/org/yaoqiang/bpmn/editor/images/straight.gif"));
				edgemenu.add(editor.bind(mxResources.get("curve"), GraphActions.getStyleAction("curve"), "/org/yaoqiang/bpmn/editor/images/curve.gif"));
				edgemenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getStyleAction("horizontal"),
						"/org/yaoqiang/bpmn/editor/images/connect.gif"));
				edgemenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getStyleAction("vertical"), "/org/yaoqiang/bpmn/editor/images/vertical.gif"));
				edgemenu.addSeparator();
				edgemenu.add(editor.bind(mxResources.get("resetEdgeStyle"), GraphActions.getStyleAction("resetEdgeStyle")));
			}
			addSeparator();
		} else {
			if (graph.getSelectionCount() > 1) {
				populateAlignMenu();
			}

			addSeparator();
			JMenu backgroundmenu = (JMenu) add(new JMenu(mxResources.get("background")));
			backgroundmenu.add(editor.bind(mxResources.get("FillColor"),
					EditorActions.getColorAction(mxResources.get("fillColor"), mxConstants.STYLE_FILLCOLOR), "/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));
			backgroundmenu.add(editor.bind(mxResources.get("Gradient"),
					EditorActions.getColorAction(mxResources.get("gradient"), mxConstants.STYLE_GRADIENTCOLOR)));
			backgroundmenu.add(editor.bind(mxResources.get("Opacity"), GraphActions.getPromptValueAction(mxConstants.STYLE_OPACITY, "Opacity (0-100)")));
			backgroundmenu.addSeparator();
			backgroundmenu.add(editor.bind(mxResources.get("resetBackground"), GraphActions.getResetBackgroundAction()));
		}
		add(editor.bind(mxResources.get("Stroke"), EditorActions.getColorAction(mxResources.get("Stroke"), mxConstants.STYLE_STROKECOLOR),
				"/org/yaoqiang/bpmn/editor/images/linecolor.gif"));

		if (model.isBoundaryEvent(cell)) {
			JMenu labelmenu = (JMenu) add(new JMenu(mxResources.get("labelposition")));
			labelmenu.add(editor.bind(mxResources.get("top"), GraphActions.getLabelPosAction(mxConstants.ALIGN_TOP, ""),
					"/org/yaoqiang/bpmn/editor/images/up.png"));
			labelmenu.add(editor.bind(mxResources.get("topleft"), GraphActions.getLabelPosAction(mxConstants.ALIGN_TOP, mxConstants.ALIGN_LEFT)));
			labelmenu.add(editor.bind(mxResources.get("left"), GraphActions.getLabelPosAction(mxConstants.ALIGN_LEFT, ""),
					"/org/yaoqiang/bpmn/editor/images/left.png"));
			labelmenu.add(editor.bind(mxResources.get("bottomleft"), GraphActions.getLabelPosAction(mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_LEFT)));
			labelmenu.add(editor.bind(mxResources.get("bottom"), GraphActions.getLabelPosAction(mxConstants.ALIGN_BOTTOM, ""),
					"/org/yaoqiang/bpmn/editor/images/down.png"));
			labelmenu.add(editor.bind(mxResources.get("bottomright"), GraphActions.getLabelPosAction(mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_RIGHT)));
			labelmenu.add(editor.bind(mxResources.get("right"), GraphActions.getLabelPosAction(mxConstants.ALIGN_RIGHT, ""),
					"/org/yaoqiang/bpmn/editor/images/right.png"));
			labelmenu.add(editor.bind(mxResources.get("topright"), GraphActions.getLabelPosAction(mxConstants.ALIGN_TOP, mxConstants.ALIGN_RIGHT)));
		}

		JMenu labelsubmenu = (JMenu) add(new JMenu(mxResources.get("labelstyle")));
		labelsubmenu.add(editor.bind(mxResources.get("FontColor"), EditorActions.getColorAction(mxResources.get("FontColor"), mxConstants.STYLE_FONTCOLOR),
				"/org/yaoqiang/bpmn/editor/images/fontcolor.gif"));

		if (model.isEvent(cell) || model.isGateway(cell) || graph.getModel().isEdge(cell)) {
			labelsubmenu.add(editor.bind(mxResources.get("labelFill"),
					EditorActions.getColorAction(mxResources.get("labelFill"), mxConstants.STYLE_LABEL_BACKGROUNDCOLOR)));
			labelsubmenu.add(editor.bind(mxResources.get("labelBorder"),
					EditorActions.getColorAction(mxResources.get("labelBorder"), mxConstants.STYLE_LABEL_BORDERCOLOR)));

			if (graph.getModel().isEdge(cell)) {
				add(editor.bind(mxResources.get("rotateLabel"), GraphActions.getToggleAction(mxConstants.STYLE_HORIZONTAL, true)));
			}
		}

		if (mxUtils.isTrue(graph.getCellStyle(cell), mxConstants.STYLE_NOLABEL, false)) {
			add(editor.bind(mxResources.get("showLabel"), GraphActions.getToggleAction(mxConstants.STYLE_NOLABEL, true)));
		} else {
			add(editor.bind(mxResources.get("hideLabel"), GraphActions.getToggleAction(mxConstants.STYLE_NOLABEL, false)));
		}

		if (!model.isMessage(cell)) {
			if (wrapped) {
				add(editor.bind(mxResources.get("noLabelWrap"), EditorActions.getKeyValueAction(mxConstants.STYLE_WHITE_SPACE, null)));
			} else {
				add(editor.bind(mxResources.get("labelWrap"), EditorActions.getKeyValueAction(mxConstants.STYLE_WHITE_SPACE, "wrap")));
			}
		}

		if (model.isPool(cell)) {
			if (graph.isAutoPool(cell)) {
				add(editor.bind(mxResources.get("manualAdjust"), GraphActions.getToggleAction(Constants.STYLE_AUTO, false)));
			} else if (!GraphUtils.hasSwimlane(graph, !graph.isVerticalSwimlane(cell))) {
				add(editor.bind(mxResources.get("autoAdjust"), GraphActions.getToggleAction(Constants.STYLE_AUTO, true)));
			}
		}

		addSeparator();
	}

	protected void populateSwimlaneMenu() {
		if (graph.isLaneMovable(cell)) {
			addSeparator();
			if (graph.isVerticalSwimlane(cell)) {
				add(editor.bind(mxResources.get("moveleft"), GraphActions.getMoveLaneAction(0), "/org/yaoqiang/bpmn/editor/images/left.png"));
				add(editor.bind(mxResources.get("moveright"), GraphActions.getMoveLaneAction(1), "/org/yaoqiang/bpmn/editor/images/right.png"));
			} else {
				add(editor.bind(mxResources.get("moveup"), GraphActions.getMoveLaneAction(0), "/org/yaoqiang/bpmn/editor/images/up.png"));
				add(editor.bind(mxResources.get("movedown"), GraphActions.getMoveLaneAction(1), "/org/yaoqiang/bpmn/editor/images/down.png"));
			}
		}
		addSeparator();
	}

	protected void populateAddonMenu() {
		boolean hasmenu = false;
		for (Addon addon : getEditor().getAddonsManager().getAddons()) {
			if (addon != null && addon.getPopupMenuItem() != null) {
				add(addon.getPopupMenuItem());
				hasmenu = hasmenu || true;
			}
		}
		if (hasmenu) {
			addSeparator();
		}
	}

	protected void populateConvertMenu() {
		if (graph.isCellConvertable(cell)) {
			JMenu convertmenu = (JMenu) add(new JMenu(mxResources.get("convertTo")));
			if (model.isIntermediateCatchEvent(cell) || model.isEndEvent(cell)) {
				convertmenu.add(editor.bind(mxResources.get("intermediateThrowEvent"), ModelActions.getChangeFlowElementTypeAction("intermediateThrowEvent")));
			} else if (model.isIntermediateThrowEvent(cell)) {
				convertmenu.add(editor.bind(mxResources.get("intermediateCatchEvent"), ModelActions.getChangeFlowElementTypeAction("intermediateCatchEvent")));
				convertmenu.add(editor.bind(mxResources.get("endEvent"), ModelActions.getChangeFlowElementTypeAction("endEvent")));
			} else if (model.isTask(cell)) {
				convertmenu.add(editor.bind(mxResources.get("gateway"), ModelActions.getChangeFlowElementTypeAction("exclusiveGateway")));
			} else if (model.isGateway(cell)) {
				convertmenu.add(editor.bind(mxResources.get("task"), ModelActions.getChangeFlowElementTypeAction("task")));
			}
			addSeparator();
		}
	}

	protected void populateDocumentationMenu() {
		add(editor.bind(mxResources.get("Letter fragment"), ModelActions.getAction(ModelActions.DOCUMENTATION)));
	}

	protected void populateWhitespaceMenu(MouseEvent e) {
		BPMNProcess process = BPMNModelUtils.getDefaultProcess(getGraph().getBpmnModel());
		Object cell = editor.getGraphComponent().getCellAt(e.getX(), e.getY());
		if (cell == null
				&& process != null
				&& graph.getCurrentRoot() == null
				|| cell != null
				&& (model.isLane(cell) || model.isPool(cell)
						&& graph.getBpmnModel().getProcess(((Participant) ((mxCell) cell).getValue()).getProcessRef()) != null)) {
			addSeparator();
			populateDocumentationMenu();
			add(editor.bind(mxResources.get("processProperties"), ModelActions.getAction(ModelActions.PROCESS_PROPERTIES)));
			add(editor.bind(mxResources.get("dataProperties"), ModelActions.getAction(ModelActions.DATA_PROPERTIES)));
			add(editor.bind(mxResources.get("dataobjects"), ModelActions.getAction(ModelActions.DATA_OBJECTS)));
			add(editor.bind(mxResources.get("resourceAssignment"), ModelActions.getAction(ModelActions.RESOURCE_ASSIGNMENT)));
			boolean added = false;
			for (Addon addon : getEditor().getAddonsManager().getAddons()) {
				if (addon != null && addon.getPopupMenuItem() != null) {
					if (!added) {
						addSeparator();
						added = true;
					}
					add(addon.getPopupMenuItem());
				}
			}
		}

		addSeparator();
		add(editor.bind(mxResources.get("paste"), GraphActions.getAction(GraphActions.PASTE), "/org/yaoqiang/bpmn/editor/images/paste.png"));
		addSeparator();
		if (cell == null) {
			add(editor.bind(mxResources.get("backgroundColor"), GraphActions.getAction(GraphActions.BACKGROUND),
					"/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));
			addSeparator();
		}
	}

	public PopupMenu(BaseEditor editor, GraphComponent graphComponent, MouseEvent e) {
		this.editor = editor;
		this.graph = graphComponent.getGraph();
		this.model = graph.getModel();
		this.cell = (mxCell) graph.getSelectionCell();

		add(editor.bind(mxResources.get("undo"), EditorActions.getAction(EditorActions.UNDO), "/org/yaoqiang/bpmn/editor/images/undo.png"));

		if (cell != null) {
			Object c = cell;
			if (graph.isChoreography(cell) || graph.isSubChoreography(cell)) {
				c = GraphUtils.getChoreographyActivity(model, cell);
			}
			String wrap = mxUtils.getString(graph.getCellStyle(c), mxConstants.STYLE_WHITE_SPACE, "nowrap");
			this.wrapstyle = ";whiteSpace=" + wrap;
			if (wrap.equals("nowrap")) {
				wrapped = false;
			}

			populateSwimlaneMenu();

			populateDocumentationMenu();

			populateDefaultSequenceFlowMenu();

			if (model.isEvent(cell)) {
				populateEventMenu();
				addSeparator();
			} else if (model.isGateway(cell)) {
				populateGatewayMenu();
			} else if (model.isActivity(cell)) {
				populateTaskAndSubProcessMenu();
				addSeparator();
			} else if (graph.isChoreography(cell) || graph.isSubChoreography(cell)) {
				populateChoreographyMenu();
				if (graph.isSubChoreography(cell)) {
					populateSubChoreographyMenu();
				}
				addSeparator();
			} else if (model.isPool(cell)) {
				populateParticipantMenu();

			} else if (model.isDataObject(cell) || model.isDataInput(cell) || model.isDataOutput(cell)) {
				populateDataObjectMenu();
			} else {
				addSeparator();
			}

			if (model.isLane(cell) || model.isPool(cell) && graph.getBpmnModel().getProcess(((Participant) cell.getValue()).getProcessRef()) != null) {
				add(editor.bind(mxResources.get("processProperties"), ModelActions.getAction(ModelActions.PROCESS_PROPERTIES)));
				add(editor.bind(mxResources.get("dataProperties"), ModelActions.getAction(ModelActions.DATA_PROPERTIES)));
				add(editor.bind(mxResources.get("dataobjects"), ModelActions.getAction(ModelActions.DATA_OBJECTS)));
				add(editor.bind(mxResources.get("resourceAssignment"), ModelActions.getAction(ModelActions.RESOURCE_ASSIGNMENT)));
				addSeparator();
			}

			populateAddonMenu();

			populateConvertMenu();

			populateEditMenu();

			populateStyleMenu();

		} else {
			populateWhitespaceMenu(e);
		}

		populateViewMenu();
	}

	public BPMNGraph getGraph() {
		return (BPMNGraph) graph;
	}

	public BPMNEditor getEditor() {
		return (BPMNEditor) editor;
	}

}
