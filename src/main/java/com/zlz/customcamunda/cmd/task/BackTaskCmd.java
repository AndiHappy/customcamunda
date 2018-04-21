package com.zlz.customcamunda.cmd.task;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.bpmn.behavior.GatewayActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.cfg.CommandChecker;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.EnginePersistenceLogger;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SuspensionState;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.util.exception.BaseIllegalException;

/**
 * @author zhailz
 * 
 *
 * @version 2018年3月21日 下午4:20:35
 */
public class BackTaskCmd implements Command<List<TaskEntity>> {

	private Logger log = LoggerFactory.getLogger("BackTaskCmd");
	protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

	private String taskId;

	private Map<String, Object> variables;

	private boolean skipCustomListeners = true;

	public static final String DELETE_REASON_DELETED = "back_action";

	public BackTaskCmd(String taskId, Map<String, Object> variables) {
		this.taskId = taskId;
		this.variables = variables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activiti.engine.impl.interceptor.Command#execute(org.activiti.engine.
	 * impl.interceptor.CommandContext)
	 */
	@Override
	public List<TaskEntity> execute(CommandContext commandContext) {

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
			ActivityImpl back = getBackActiviti(activiti, processDefinition);
			if (back != null) {
				log.info("find back activiti:{}", back.toString());
				ExecutionEntity execution = task.getExecution();
				execution = complete(task, DELETE_REASON_DELETED, this.getVariables());
				execution.setActivity(back);
				execution.performOperation(PvmAtomicOperation.ACTIVITY_START);
				List<TaskEntity> tasks = task.getExecution().getTasks();
				return tasks;
			}
		}

		return null;
	}

	// 完成这个任务
	private ExecutionEntity complete(TaskEntity task, String deleteReasonDeleted, Map<String, Object> variables2) {
		log.info("complete : {} ,reason : {}", task.getId(), deleteReasonDeleted);
		// if the task is associated with a case
		// execution then call complete on the
		// associated case execution. The case
		// execution handles the completion of
		// the task.
		if (task.getCaseExecutionId() != null) {
			task.getCaseExecution().manualComplete();
		}

		// in the other case:
		// ensure the the Task is not suspended
		if (task.getSuspensionState() == SuspensionState.SUSPENDED.getStateCode()) {
			throw LOG.suspendedEntityException("task", task.getId());
		}

		// trigger TaskListener.complete event
		task.fireEvent(TaskListener.EVENTNAME_COMPLETE);

		// delete the task
		Context.getCommandContext().getTaskManager().deleteTask(task, TaskEntity.DELETE_REASON_COMPLETED, false, skipCustomListeners);

		// if the task is associated with a
		// execution (and not a case execution)
		// then call signal an the associated
		// execution.
		if (task.getExecutionId() != null) {
			ExecutionEntity execution = task.getExecution();
			execution.removeTask(task);
			return execution;
		}
		
		return null;

	}

	protected void checkCompleteTask(TaskEntity task, CommandContext commandContext) {
		for (CommandChecker checker : commandContext.getProcessEngineConfiguration().getCommandCheckers()) {
			checker.checkTaskWork(task);
		}
	}

	private ActivityImpl getBackActiviti(ActivityImpl currentActiviti, ProcessDefinitionImpl processDefinition) {
		List<PvmTransition> incomings = currentActiviti.getIncomingTransitions();
		if (incomings != null && incomings.size() > 1) {
			throw BaseIllegalException.tooManyBackActivities;
		}

		if (incomings != null && !incomings.isEmpty()) {
			ActivityImpl sources = (ActivityImpl) incomings.get(0).getSource();
			// 如果前面是usertask或者是开始节点，就能够回退
			if (sources.getActivityBehavior() instanceof UserTaskActivityBehavior || sources.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
				return sources;
			}

			// 如果是网关，回退的过程中碰到是网关的情况下，这中情况下也不能回退
			if (sources.getActivityBehavior() instanceof GatewayActivityBehavior) {
				throw BaseIllegalException.backActivitiIsWrongType;
			}
		} else {
			throw BaseIllegalException.noBackActivitiIs;
		}

		return null;
	}

	protected String getSuspendedTaskException() {
		return "Cannot execute operation: task is suspended";
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

}
