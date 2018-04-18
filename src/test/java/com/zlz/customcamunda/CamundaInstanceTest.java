package com.zlz.customcamunda;

import java.util.List;

import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessInstanceWithVariablesImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.util.PropertiesUtil;

/**
 * 启动流程实例
 * 查找待处理的任务
 * 完成任务
 * */
public class CamundaInstanceTest extends CamundaBaseTest{

	private Logger log = LoggerFactory.getLogger("CamundaInsTest");

	private static PropertiesUtil util = new PropertiesUtil();
	
	@Test
	public void startIns(){
		if(this.prodef != null){
			ProcessInstance instance = engine.getRuntimeService().startProcessInstanceById(this.prodef.getId());
			log.info("instance: {} started!",instance.getId());
			util.setPropertiesValue("instanceId", instance.getId());
			
			ProcessInstanceWithVariablesImpl instancetmp = (ProcessInstanceWithVariablesImpl) instance;
			List<TaskEntity> tasks = instancetmp.getExecutionEntity().getTasks();
			TaskEntity task = tasks.get(0);
			log.info("todo task:{}",task);
			
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
			engine.getTaskService().complete(taskId);
			
			task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId()).singleResult();
			taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
	}
	
	
	@Test
	public void completeTask(){
		boolean flag = true;
		while (flag) {
			String taskId = util.getPropertyValue("taskId");
			engine.getTaskService().complete(taskId);
			log.info("完成:{}",taskId);
			List<Task> tasks = engine.getTaskService().createTaskQuery().processInstanceId(util.getPropertyValue("instanceId")).list();
			if(tasks != null && !tasks.isEmpty()){
				TaskEntity task = (TaskEntity) tasks.get(0);
				log.info("待处理任务:{},{},{},{},{}",task.getId(),task.getName(),task.getAssignee(),task.getCandidates());
				taskId = task.getId();
				util.setPropertiesValue("taskId", taskId);
			}else{
				flag = false;
				List<HistoricTaskInstance> hisinss =engine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(util.getPropertyValue("instanceId")).orderByTaskId().list();
				show(hisinss);
			}
			
		}

		
	}


	private void show(List<HistoricTaskInstance> hisinss) {
		for (HistoricTaskInstance historicTaskInstance : hisinss) {
			log.info("HistoricTaskInstance: {}",historicTaskInstance.toString());
		}
		
	}
}
