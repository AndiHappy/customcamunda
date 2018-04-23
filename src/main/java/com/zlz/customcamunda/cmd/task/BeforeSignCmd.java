package com.zlz.customcamunda.cmd.task;

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
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlz.customcamunda.util.ActivityManagerUtil;

public class BeforeSignCmd extends BaseCmd implements Command<TaskEntity> {
	
	private Logger log = LoggerFactory.getLogger(BeforeSignCmd.class);
	protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

	public static final String DELETE_REASON_DELETED = "before_sign_action";
	
	private String taskId;
	private Map<String, Object> variables;
	private String assignee;

	public BeforeSignCmd(String taskId, Map<String, Object> variables, String assignee) {
		setAssignee(assignee);
		setTaskId(taskId);
		setVariables(variables);
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
		
		log.info("find current task taskId:{},taskKey:{}",task.getId(),task.getName());
		ExecutionEntity execution = completeTaskOnlyNotLeave(task,DELETE_REASON_DELETED,this.getVariables());
		ProcessDefinitionImpl processDefinition = task.getExecution().getProcessDefinition();
		//因为是前加签的操作，所以下一个任务：需要跳转回来，即是当前的Task对应的活动定义
		ActivityImpl desactiviti = findDestinationActivity(task, processDefinition);
		
		leave(execution, processDefinition, desactiviti);

		if(execution.getTasks() != null && !execution.getTasks().isEmpty()){
			return execution.getTasks().get(0);
		}else{
			return null;
		}
	}
	
	private void leave(ExecutionEntity execution, ProcessDefinitionImpl processDefinition, ActivityImpl desactiviti) {
		//construct new activiti as the insert task which refer to
		ActivityImpl newactivity= cloneBeforeSign(desactiviti,processDefinition);
		if(newactivity != null){
			log.info("clone activiti:{}",newactivity.toString());
			execution.setActivity(newactivity);
			//this taskentity refter to destination activity
			execution.setVariable(ActivityManagerUtil.getDestinationActivityIdName(newactivity.getId()), desactiviti.getId());
			//this taskentity refter to current activity
			execution.setVariable(ActivityManagerUtil.getCurrentActivityIdName(newactivity.getId()), newactivity.getId());
			//this para store activiti`s assigen
			execution.setVariable(ActivityManagerUtil.getCurrentActivityAssigneeName(newactivity.getId()), this.assignee);
			execution.performOperation(PvmAtomicOperation.ACTIVITY_START);
		}
	}

	// temporary construct sign activiti，
	private ActivityImpl cloneBeforeSign(ActivityImpl desactiviti, ProcessDefinitionImpl processDefinition) {
		String beforeId = desactiviti.getId();
		String activityId = "IBT@"+beforeId+"@"+System.currentTimeMillis();
		ActivityImpl tmp = processDefinition.createActivity(activityId);
		
		TaskDefinition definitions = new TaskDefinition(null);
		definitions.setKey(activityId);
		Expression nameExpression = new FixedValue(beforeId+"@IBT");
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
	
	/**
	 * 当前任务的对应的下一个活动定义
	 * */
	private ActivityImpl findDestinationActivity(TaskEntity task,ProcessDefinitionImpl processDefinition) {
		String definitionKey = task.getTaskDefinitionKey();
		//insert before task action ,this task refter to activity is  destination activity
		//查找当前的任务对应的任务的定义
		ActivityImpl desactiviti = processDefinition.findActivity(definitionKey);
		if(desactiviti == null){
			//如果当前的任务定义为null，说明这个任务本身有可能是加签产生的任务，或者临时生成的任务
			//直接的克隆一个活动定义出来
			desactiviti =  onlyCloneActivity(definitionKey,processDefinition);
		}
		return desactiviti;
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

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

}
