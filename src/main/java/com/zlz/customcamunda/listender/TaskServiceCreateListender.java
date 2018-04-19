package com.zlz.customcamunda.listender;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhailz
 * 
 * 支持的task的事件的类型：
 * 
  String EVENTNAME_CREATE = "create";
  String EVENTNAME_ASSIGNMENT = "assignment";
  String EVENTNAME_COMPLETE = "complete";
  String EVENTNAME_DELETE = "delete";
 *
 *
 *userTask 上面的配置
 <userTask id="usertask0" name="部门主管" camunda:assignee="taskExecutor">
      <extensionElements>
        <camunda:taskListener event="create" class="com.zlz.customcamunda.listender.TaskServiceCreateListender">
          <camunda:field name="informPerson">
            <camunda:string>6000004545,ROLE-32424124124141,ROLE-3242412412490</camunda:string>
          </camunda:field>
        </camunda:taskListener>
      </extensionElements>
    </userTask>
 *
 *
 * @version 2018年4月19日 下午3:58:09
 */


public class TaskServiceCreateListender implements TaskListener {

	private static Logger log = LoggerFactory.getLogger(TaskServiceCreateListender.class);

	private FixedValue informPerson;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("informPerson:{}",informPerson.getValue(delegateTask));
		log.info("delegateTask create : {}",delegateTask.getId());
	}

	public FixedValue getInformPerson() {
		return informPerson;
	}

	public void setInformPerson(FixedValue informPerson) {
		this.informPerson = informPerson;
	}


}
