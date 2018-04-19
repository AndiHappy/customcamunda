package com.zlz.customcamunda;

import java.io.File;
import java.io.InputStream;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.server.CustomRepositoryService;
import com.zlz.customcamunda.server.CustomTaskServiceImpl;

public class FlowEngine extends SpringProcessEngineConfiguration {
	
	private static Logger log = LoggerFactory.getLogger(FlowEngine.class);


	// 引擎启动的时候，承接activiti的Behavior的行为模式
	@Override
	public ProcessEngine buildProcessEngine() {
		return super.buildProcessEngine();
	}

	/**
	 * 扩展的静态服务
	 */
	public CustomRepositoryService getCustomRepositoryService() {
		return (CustomRepositoryService) repositoryService;
	}
	
	/**
	 * 扩展的任务服务
	 */
	public CustomTaskServiceImpl getCustomTaskService() {
		return (CustomTaskServiceImpl) taskService;
	}

	/**
	 * 直接的校验bpmn文件
	 */
	public boolean validateBpmn(File file) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
		// validate the model
		Bpmn.validateModel(modelInstance);

		return modelInstance != null;
	}

	/**
	 * 直接的校验bpmn文件
	 */
	public boolean validateBpmn(InputStream input) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromStream(input);
		// validate the model
		Bpmn.validateModel(modelInstance);
		return modelInstance != null;
	}

	// /**
	// * 获取代办的事件
	// * */
	// public List<Task> getTodoTaskId(String instanceId,String orgId,String
	// userId){
	// List<Task> tasks =
	// this.getTaskService().createTaskQuery().processInstanceId(instanceId).taskTenantId(orgId).taskAssignee(userId).active().list();
	// return tasks;
	// }
	//
	// /**
	// * 获取代办的事件
	// * */
	// public List<Task> getTodoTaskId(String instanceId){
	// List<Task> tasks =
	// this.getTaskService().createTaskQuery().processInstanceId(instanceId).active().list();
	// return tasks;
	// }
	//
	// public CustomTaskServiceImpl getCustomTaskService() {
	// return (CustomTaskServiceImpl) taskService;
	// }
	//
	// /**
	// * 历史任务
	// * */
	// public List<HistoricTaskInstance> gethisTasks(String preinstanceId) {
	// List<HistoricTaskInstance> histasks =
	// this.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(preinstanceId).list();
	// return histasks;
	// }
	//
	//
	// /**
	// * 获取代办的事件
	// * */
	// public List<Task> getTasks(String instanceId){
	// List<Task> tasks =
	// this.getTaskService().createTaskQuery().processInstanceId(instanceId).list();
	// return tasks;
	// }

}
