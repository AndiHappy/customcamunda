package com.zlz.customcamunda.cmd.task;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

import java.util.Map;

import org.camunda.bpm.engine.impl.cmd.CompleteTaskCmd;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.util.ActivityManagerUtil;

public class CusTomCompleteTaskCmd extends CompleteTaskCmd {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(CusTomCompleteTaskCmd.class);

	public CusTomCompleteTaskCmd(String taskId, Map<String, Object> variables, boolean localScope) {
		super(taskId, variables);
	}

	public CusTomCompleteTaskCmd(String taskId, Map<String, Object> variables) {
		super(taskId, variables);
	}
	
	public Void execute(CommandContext commandContext) {
	    ensureNotNull("taskId", taskId);

	    TaskManager taskManager = commandContext.getTaskManager();
	    TaskEntity task = taskManager.findTaskById(taskId);
	    ensureNotNull("Cannot find task with id " + taskId, "task", task);

	    checkCompleteTask(task, commandContext);

	    if (variables != null) {
	      task.setExecutionVariables(variables);
	    }
	    
	    ExecutionEntity entity =  task.getExecution();
	    //首先请求entity.getActivityId()，不然在请求entity.getActivity的时候，会把activityId覆盖掉
		String currentActivityId = entity.getActivityId();
		 ActivityImpl activi = entity.getActivity();
		if (currentActivityId != null && activi == null) {
			// construct activiti by variables
			if (entity.getVariables() != null) {
				activi = ActivityManagerUtil.getInstance().getCacheActivity(currentActivityId);
				if (activi == null) {
					String desid = (String) entity.getVariable(ActivityManagerUtil.getDestinationActivityIdName(currentActivityId));
					String assignee = (String) entity.getVariable(ActivityManagerUtil.getCurrentActivityAssigneeName(currentActivityId));
					ProcessDefinitionImpl processDef = entity.getProcessDefinition();
					log.info("clone activity :{}",currentActivityId);
					activi = ActivityManagerUtil.cloneCurrentActivityAndPointToDestination(currentActivityId, desid, processDef, assignee);
				}
			}

			if (activi != null) {
				entity.setActivity(activi);
			}

		}
		task.setExecution(entity);
	    //如果是前加签的任务的话，task的ExecutionEntity,需要重新的设置
	    completeTask(task);
	    return null;
	  }



}
