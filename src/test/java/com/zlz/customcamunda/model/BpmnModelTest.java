package com.zlz.customcamunda.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.javaDelegate.WsDelegate;

public class BpmnModelTest {
	
	public static Logger log = LoggerFactory.getLogger("modle");

	
	public static void main(String[] args) throws IOException{
		
//		long time  = System.currentTimeMillis();
//		// read a model from a file
//		File file = new File("./bpmn/ABCD.bpmn");
//		/**
//		 * 可以作为校验的策略
//		 * */
//		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
//		log.info(modelInstance.toString());
//		log.info("load waste:{}",(System.currentTimeMillis() - time));
//		// read a model from a stream
//		
//		// find element instance by ID
//		StartEvent start = (StartEvent) modelInstance.getModelElementById("start");
//		// find all elements of the type task
//		ModelElementType taskType = modelInstance.getModel().getType(Task.class);
//		Collection<ModelElementInstance> taskInstances = modelInstance.getModelElementsByType(taskType);
//		
		
//		
//		buildSimpleWorkFlowHasNoBPMNDiagram();
//		
//		
//		buildGateWayWorkFlowHasNoDiagram();
		
		buildProcess();
	}

	private static void buildProcess() {
		// Directly define the subprocess
		BpmnModelInstance modelInstance = Bpmn.createProcess().name("processinstance")
		  .startEvent()
		  .subProcess()
		    .camundaAsyncBefore(true)
		    .embeddedSubProcess()
		      .startEvent()
		      .userTask()
		      .endEvent()
		    .subProcessDone()
		  .serviceTask()
		    .camundaClass(WsDelegate.class)
		    .camundaDelegateExpression("${successBean}")
		  .endEvent()
		  .done();

		// Detach the subprocess building
		modelInstance = Bpmn.createProcess()
		  .startEvent()
		  .subProcess("subProcess")
		  .serviceTask()
		  .endEvent()
		  .done();

		SubProcess subProcess = (SubProcess) modelInstance.getModelElementById("subProcess");
		subProcess.builder()
		  .camundaAsyncBefore(true)
		  .embeddedSubProcess()
		    .startEvent()
		    .userTask()
		    .endEvent();	
		
		try {
			String bpmnfile = Bpmn.convertToString(modelInstance);
			File file = new File("./bpmn/processinstance.bpmn");
			if(file.exists()){
				file.delete();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(bpmnfile);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 创建一个固定的流程，但是没有图像，因为压根没有生成：bpmndi:BPMNDiagram 节点
	 * */
	public static void buildGateWayWorkFlowHasNoDiagram() throws IOException {
		// create an empty model
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace("http://camunda.org/examples");
		modelInstance.setDefinitions(definitions);
		// create elements
		Process process = createElement(modelInstance,definitions, "process-with-one-task", Process.class);
		process.setExecutable(true);
		StartEvent startEvent = createElement(modelInstance,process, "start", StartEvent.class);
		ParallelGateway fork = createElement(modelInstance,process, "fork1", ParallelGateway.class);
		ServiceTask task11 = createElement(modelInstance,process, "task1", ServiceTask.class);
		task11.setName("Service Task");
		UserTask task2 = createElement(modelInstance,process, "task2", UserTask.class);
		task2.setName("User Task");
		ParallelGateway join = createElement(modelInstance,process, "join", ParallelGateway.class);
		EndEvent endEvent = createElement(modelInstance,process, "end", EndEvent.class);

		// create flows
		createSequenceFlow(modelInstance,process, startEvent, fork);
		createSequenceFlow(modelInstance,process, fork, task11);
		createSequenceFlow(modelInstance,process, fork, task2);
		createSequenceFlow(modelInstance,process, task11, join);
		createSequenceFlow(modelInstance,process, task2, join);
		createSequenceFlow(modelInstance,process, join, endEvent);

		// validate and write model to file
		Bpmn.validateModel(modelInstance);
		File file = File.createTempFile("./bpmn/bpmn-model-api-GateWay", ".bpmn");
		System.out.println(file.getAbsolutePath());
		Bpmn.writeModelToFile(file, modelInstance);
	}

	/**
	 * 创建一个固定的流程，但是没有图像，因为压根没有生成：bpmndi:BPMNDiagram 节点
	 * */
	public static void buildSimpleWorkFlowHasNoBPMNDiagram() throws IOException {
		// create an empty model
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace("http://camunda.org/examples");
		modelInstance.setDefinitions(definitions);
		// create the process
		Process process = createElement(modelInstance,definitions, "process-with-one-task", Process.class);
		process.setExecutable(true);
		// create start event, user task and end event
		StartEvent startEvent = createElement(modelInstance,process, "start", StartEvent.class);
		UserTask task1 = createElement(modelInstance,process, "task1", UserTask.class);
		task1.setName("User Task");
		EndEvent endEvent = createElement(modelInstance,process, "end", EndEvent.class);

		// create the connections between the elements
		createSequenceFlow(modelInstance,process, startEvent, task1);
		createSequenceFlow(modelInstance,process, task1, endEvent);

		// validate and write model to file
		Bpmn.validateModel(modelInstance);
		File file = File.createTempFile("./bpmn/bpmn-model-api-simple", ".bpmn");
		System.out.println(file.getAbsolutePath());
		Bpmn.writeModelToFile(file, modelInstance);
	}
	

	public static SequenceFlow createSequenceFlow(BpmnModelInstance modelInstance,Process process, FlowNode from, FlowNode to) {
		  String identifier = from.getId() + "-" + to.getId();
		  SequenceFlow sequenceFlow = createElement(modelInstance,process,identifier, SequenceFlow.class);
		  process.addChildElement(sequenceFlow);
		  sequenceFlow.setSource(from);
		  from.getOutgoing().add(sequenceFlow);
		  sequenceFlow.setTarget(to);
		  to.getIncoming().add(sequenceFlow);
		  return sequenceFlow;
		}
	
	protected static  <T extends ModelElementInstance> T createElement(BpmnModelInstance modelInstance,BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
		  T element = modelInstance.newInstance(elementClass);
		  element.setAttributeValue("id", id, true);
		  parentElement.addChildElement(element);
		  return element;
		}

}
