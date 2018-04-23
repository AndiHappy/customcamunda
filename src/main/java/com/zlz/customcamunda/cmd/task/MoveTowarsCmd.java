package com.zlz.customcamunda.cmd.task;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.cfg.CommandChecker;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveTowarsCmd extends BaseCmd implements Command<TaskEntity> {

	private String activityId;
	private String taskId;
	private Map<String, Object> variables;
	
	public static final String DELETE_REASON_DELETED = "move_towards_action";

	private Logger log = LoggerFactory.getLogger("MoveTowarsCmd");

	public MoveTowarsCmd(String taskId, String activityId, Map<String, Object> variables) {
		this.taskId = taskId;
		this.activityId = activityId;
		this.variables = variables;
	}

	@Override
	public TaskEntity execute(CommandContext commandContext) {
		

		TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);
		// 校验的逻辑
		ensureNotNull("Cannot find task with id " + taskId, "task", task);
		checkCompleteTask(task, commandContext);

		log.info("find current task taskId:{},taskKey:{}", task.getId(), task.getName());
		if (variables != null) {
			task.setExecutionVariables(variables);
		}
		// 找到back的上一级的task
		String definitionKey = task.getTaskDefinitionKey();
		ProcessDefinitionImpl processDefinition = task.getExecution().getProcessDefinition();
		ActivityImpl activiti = task.getExecution().getProcessDefinition().findActivity(definitionKey);
		if (activiti != null) {
			ActivityImpl des = processDefinition.findActivity(this.activityId);
			if (des != null) {
				log.info("find back activiti:{}", des.toString());
				ExecutionEntity execution = task.getExecution();
				execution = completeTaskOnlyNotLeave(task, DELETE_REASON_DELETED, this.getVariables());
				execution.setActivity(des);
				execution.performOperation(PvmAtomicOperation.ACTIVITY_START);
				List<TaskEntity> tasks = task.getExecution().getTasks();
				if(tasks != null && !tasks.isEmpty()){
					return tasks.get(0);
				}
			}else{
				ensureNotNull("Cannot find task with id " + this.activityId, "task", des);
			}
		}else{
			ensureNotNull("Cannot find task with id " + definitionKey, "task", task);
		}
		return null;
	}

	protected void checkCompleteTask(TaskEntity task, CommandContext commandContext) {
		for (CommandChecker checker : commandContext.getProcessEngineConfiguration().getCommandCheckers()) {
			checker.checkTaskWork(task);
		}
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
