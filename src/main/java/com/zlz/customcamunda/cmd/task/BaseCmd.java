package com.zlz.customcamunda.cmd.task;

import java.util.Map;

import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.EnginePersistenceLogger;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SuspensionState;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseCmd {

	private Logger log = LoggerFactory.getLogger("BackTaskCmd");
	private boolean skipCustomListeners = true;
	protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

	// 完成这个任务
	protected ExecutionEntity completeTaskOnlyNotLeave(TaskEntity task, String deleteReasonDeleted, Map<String, Object> variables2) {
		log.info("complete : {} ,reason : {}", task.getId(), deleteReasonDeleted);
		/**
		 * if the task is associated with a case execution then call complete on the
		 * associated case execution. The case execution handles the completion of the task.
		 * */ 
		if (task.getCaseExecutionId() != null) {
			task.getCaseExecution().manualComplete();
			return task.getExecution();
		}

		/**
		 * in the other case:
		 *  ensure the the Task is not suspended
		 * */ 
		if (task.getSuspensionState() == SuspensionState.SUSPENDED.getStateCode()) {
			throw LOG.suspendedEntityException("task", task.getId());
		}

		// trigger TaskListener.complete event
		task.fireEvent(TaskListener.EVENTNAME_COMPLETE);

		// delete the task,this is for historyActivityInstance
		Context.getCommandContext().getTaskManager().deleteTask(task, deleteReasonDeleted, false, skipCustomListeners);

		/**
		 * if the task is associated with a  execution (and not a case execution)
		 *  then call signal an the associated  execution.
		 * */
		if (task.getExecutionId() != null) {
			ExecutionEntity execution = task.getExecution();
			execution.removeTask(task);
			return execution;
		}

		return null;

	}
	
	/***
	 * 根据活动标识（activityId）克隆出来一个活动定义
	 * */
	protected ActivityImpl onlyCloneActivity(String activityId,ProcessDefinitionImpl processDefinition) {
		ActivityImpl tmp = processDefinition.createActivity(activityId);
		TaskDefinition definition = new TaskDefinition(null);
		definition.setKey(activityId);
		Expression nameExpression = new FixedValue(activityId);
		definition.setNameExpression(nameExpression);
		ExpressionManager mana =Context.getProcessEngineConfiguration().getExpressionManager();
		tmp.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definition,mana)));
		return tmp;
	}
	
	protected String getSuspendedTaskException() {
		return "Cannot execute operation: task is suspended";
	}

}
