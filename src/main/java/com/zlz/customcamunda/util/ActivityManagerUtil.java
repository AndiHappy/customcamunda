package com.zlz.customcamunda.util;

import java.util.WeakHashMap;

import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

/**
 * @author zhailz
 *
 * @version 2018年3月28日 下午5:54:39
 */
public class ActivityManagerUtil {

	public  static final String  sourceActivityIdName = "sourceActivityId";
	
	public  static final String  destinationActivityIdName = "destinationActivityId";
	
	public  static final String  currentActivityIdName = "currentActivityId";
	
	public  static final String  currentActivityAssigenName = "currentActivityAssigen";

	
	public static final String spit = "@";

	private WeakHashMap<String, ActivityImpl> cacheActivity = new WeakHashMap<String, ActivityImpl>(100);
	
	private static class ActivityManagerUtilHoler{
		private static ActivityManagerUtil instance = new ActivityManagerUtil();
	}
	
	private ActivityManagerUtil(){}
	
	public static ActivityManagerUtil getInstance(){
		return ActivityManagerUtilHoler.instance;
	}
	
	public  void cache(ActivityImpl impl){
		cacheActivity.put(impl.getId(), impl);
	}

	public ActivityImpl getCacheActivity(String activityId) {
		return cacheActivity.get(activityId);
	}

	/**
	 * 构建的是当前的任务节点并且确定了指向：desid的活动定义
	 * */
	public static ActivityImpl cloneCurrentActivityAndPointToDestination(String currentid, String destinationActivityid, ProcessDefinitionImpl processDef,
			String assignee) {
		ActivityImpl des = processDef.findActivity(destinationActivityid);
		ExpressionManager expressionManager = Context.getProcessEngineConfiguration().getExpressionManager();
		if(des == null){
			des = processDef.createActivity(destinationActivityid);
			TaskDefinition definitions = new TaskDefinition(null);
			definitions.setKey(des.getId());
			Expression nameExpression = new FixedValue(des.getId());
			definitions.setNameExpression(nameExpression);
			des.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definitions, expressionManager)));
		}
		ActivityImpl tmp = processDef.createActivity(currentid);
		TaskDefinition definitions = new TaskDefinition(null);
		definitions.setKey(currentid);
		Expression nameExpression = new FixedValue(assignee);
		definitions.setNameExpression(nameExpression);
		tmp.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definitions, expressionManager)));
		TransitionImpl transition = tmp.createOutgoingTransition();
		transition.setDestination(des);
		ActivityManagerUtil.getInstance().cache(tmp);
		return tmp;
	}

	public static String getDestinationActivityIdName(String activityId) {
		return activityId+spit+destinationActivityIdName;
	}

	public static String getCurrentActivityIdName(String activityId) {
		return activityId+spit+currentActivityIdName;
	}

	public static String getCurrentActivityAssigneeName(String activityId) {
		return activityId+spit+currentActivityAssigenName;
	}

	public void clearCache() {
		if(cacheActivity != null){
			cacheActivity.clear();
		}
	}
}
