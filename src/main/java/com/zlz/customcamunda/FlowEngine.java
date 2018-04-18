package com.zlz.customcamunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;

import com.zlz.customcamunda.server.CustomRepositoryService;

public class FlowEngine extends SpringProcessEngineConfiguration {

	// 引擎启动的时候，承接activiti的Behavior的行为模式
	@Override
	public ProcessEngine buildProcessEngine() {
		return super.buildProcessEngine();
	}
	
	/**
	 * 
	 * */
	public CustomRepositoryService getCustomRepositoryService() {
	    return (CustomRepositoryService) repositoryService;
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
