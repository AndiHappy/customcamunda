package com.zlz.customcamunda.cmd.task;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.EnginePersistenceLogger;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.util.ActivityManagerUtil;

public class AfterSignCmd extends BaseCmd implements Command<TaskEntity> {

	private Logger log = LoggerFactory.getLogger(BeforeSignCmd.class);
	protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

	private String taskId;
	private Map<String, Object> variables;
	private String assignee;

	public static final String DELETE_REASON_DELETED = "after_sign_action";

	public AfterSignCmd(String taskId, Map<String, Object> variables, String assignee) {
		this.assignee = assignee;
		this.variables = variables;
		this.taskId = taskId;
	}

	@Override
	public TaskEntity execute(CommandContext commandContext) {
		log.info("BeforeSignCmd:{},", this.taskId);
		TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);

		if (task == null) {
			throw new IllegalArgumentException("Cannot find task with id " + taskId);
		}

		if (task.isSuspended()) {
			throw new IllegalArgumentException(getSuspendedTaskException());
		}

		log.info("find current task taskId:{},taskKey:{}", task.getId(), task.getName());

		ExecutionEntity execution = completeTaskOnlyNotLeave(task, DELETE_REASON_DELETED, this.getVariables());

		// first:taskEntity refer to Activiti Definition
		// second:clone the activiti definition,set assigen
		ProcessDefinitionImpl processDefinition = task.getExecution().getProcessDefinition();
		// 当前任务执行的过程总，对应的下一个活动的定义，下一个任务，应该是task活动定义的下一个的活动定义，或者是excution中
		// 带有的下一个节点
		ActivityImpl desactiviti = findDestinationActivity(task, execution, processDefinition);
		leave(execution, processDefinition, desactiviti);
		if (execution.getTasks() != null && !execution.getTasks().isEmpty()) {
			return execution.getTasks().get(0);
		} else {
			return null;
		}
	}

	private void leave(ExecutionEntity execution, ProcessDefinitionImpl processDefinition, ActivityImpl desactiviti) {
//		 construct new activiti as the sign task which refer to
		ActivityImpl newactivity = cloneAfterSignActivity(desactiviti, processDefinition);
		if (newactivity != null) {
			execution.setActivity(newactivity);
			execution.setVariable(ActivityManagerUtil.getDestinationActivityIdName(newactivity.getId()),desactiviti.getId());
			execution.setVariable(ActivityManagerUtil.getCurrentActivityIdName(newactivity.getId()),newactivity.getId());
			execution.setVariable(ActivityManagerUtil.getCurrentActivityAssigneeName(newactivity.getId()),this.assignee);
			execution.performOperation(PvmAtomicOperation.ACTIVITY_START);
		} 

	}
	
	// temporary construct sign activiti，
		private ActivityImpl cloneAfterSignActivity(ActivityImpl desactiviti, ProcessDefinitionImpl processDefinition) {
			String beforeId = desactiviti.getId();
			String activityId = "InsertAfter@"+beforeId+"@"+System.currentTimeMillis();
			
			ActivityImpl tmp = processDefinition.createActivity(activityId);
			TaskDefinition definitions = new TaskDefinition(null);
			definitions.setKey(activityId);
			Expression nameExpression = new FixedValue("InsertAfter@"+beforeId);
			definitions.setNameExpression(nameExpression);
			Expression assigneeExpression = new FixedValue(this.assignee);
			definitions.setAssigneeExpression(assigneeExpression);
			ExpressionManager expressionManager = Context.getProcessEngineConfiguration().getExpressionManager();
			tmp.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definitions, expressionManager)));
			
			TransitionImpl transition = tmp.createOutgoingTransition();
			transition.setDestination(desactiviti);
			ActivityManagerUtil.getInstance().cache(tmp);
			return tmp;
		}


	private ActivityImpl findDestinationActivity(TaskEntity task, ExecutionEntity execution,
			ProcessDefinitionImpl processDefinition) {
		String currentActivityId = task.getTaskDefinitionKey();
		// insert before task action ,this task refter to activity is
		// destination activity
		// 查找当前的任务对应的任务的定义
		ActivityImpl currentActiviti = processDefinition.findActivity(currentActivityId);
		if (currentActiviti != null) {
			List<PvmTransition> outgoings = currentActiviti.getOutgoingTransitions();
			if (outgoings != null && !outgoings.isEmpty()) {
				return (ActivityImpl) outgoings.get(0).getDestination();
			}
		}

		// 如果当前的任务定义为null，说明这个任务本身有可能是加签产生的任务，或者临时生成的任务
		// 需要找到当前任务对应的下一个活动的定义
		String desid = (String) execution
				.getVariable(ActivityManagerUtil.getDestinationActivityIdName(currentActivityId));
		log.info("clone activity :{}", currentActivityId);
		ActivityImpl desitination = onlyCloneActivity(desid, processDefinition);
		return desitination;
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

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

}
