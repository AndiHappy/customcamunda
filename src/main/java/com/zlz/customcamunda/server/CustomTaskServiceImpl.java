package com.zlz.customcamunda.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.TaskServiceImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import com.zlz.customcamunda.cmd.task.AfterSignCmd;
import com.zlz.customcamunda.cmd.task.BackTaskCmd;
import com.zlz.customcamunda.cmd.task.BeforeSignCmd;
import com.zlz.customcamunda.cmd.task.CusTomCompleteTaskCmd;

@Component
public class CustomTaskServiceImpl extends TaskServiceImpl {

	/**
	 * @param taskId
	 *            当前任务的taskId
	 * @param afterTaskIsdeleteCurrentTask
	 *            当前的任务是否删除
	 * @param variables
	 *            参数
	 * @param localScope
	 *            范围
	 * @return 后退的过程中，产生新的任务
	 * 
	 */
	public List<TaskEntity> back(String taskId, Map<String, Object> variables) {
		return commandExecutor.execute(new BackTaskCmd(taskId, variables));
	}

	public TaskEntity beforeSign(String taskId, Map<String, Object> variables, boolean localScope, String assignee) {
		return commandExecutor.execute(new BeforeSignCmd(taskId, variables, localScope, assignee));
	}
	
	public TaskEntity afterSign(String taskId, Map<String, Object> variables, boolean localScope, String assignee) {
		return commandExecutor.execute(new AfterSignCmd(taskId, variables, localScope, assignee));
	}
	

	@Override
	public void complete(String taskId) {
		Map<String, Object> variables = new HashMap<String, Object>();
		commandExecutor.execute(new CusTomCompleteTaskCmd(taskId, variables));
	}
	
	@Override
	public void complete(String taskId, Map<String, Object> variables) {
		commandExecutor.execute(new CusTomCompleteTaskCmd(taskId, variables));
	}
}
