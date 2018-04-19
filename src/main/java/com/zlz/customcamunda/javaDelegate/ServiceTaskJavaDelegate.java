package com.zlz.customcamunda.javaDelegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @auth zlz
<serviceTask id="javaService"
name="My Java Service Task"
camunda:class="org.camunda.bpm.MyJavaDelegate" />

<serviceTask id="expressionService"
             name="My Expression Service Task"
             camunda:expression="${myBean.doWork()}" />
             
<serviceTask id="beanService"
             name="My Bean Service Task"
             camunda:delegateExpression="${myDelegateBean}" 
             camunda:resultVariable="myVar" />
对应的是Service task 的实现的逻辑

*/

public class ServiceTaskJavaDelegate implements JavaDelegate {

	private static Logger log = LoggerFactory.getLogger(ServiceTaskJavaDelegate.class);

    public void execute(DelegateExecution execution) throws Exception {
     
    	log.info("执行:{}",execution.getBpmnModelInstance());
    	
    	log.info("执行体:{}",execution.getCurrentActivityName());
    	
    	log.info("执行:{}",execution);
    	
    	execution.setVariable("myDelegateBean", new com.zlz.customcamunda.behavior.CustomeActivityBehavior());
    }
    
    public String cal(String value){
    	System.out.println(value);
    	return "hello";
    }

}