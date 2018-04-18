package com.zlz.customcamunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaInMemTest {
	private static Logger log = LoggerFactory.getLogger("camundaTest");

  public static void main(String[] args) {    
    StandaloneInMemProcessEngineConfiguration conf = new StandaloneInMemProcessEngineConfiguration();    
    ProcessEngine engine = conf.buildProcessEngine();
    
	Deployment deployment = engine.getRepositoryService().createDeployment() //
      .addModelInstance("flow.bpmn", Bpmn.createExecutableProcess("flow") //
        .startEvent()
        .userTask().name("Step1")
        .userTask().name("Step2")
        .userTask().name("Step3")
        .userTask().name("Step4")
        .userTask().name("Step5")
        .userTask().name("Step6")
        .userTask().name("Step7")
        .endEvent()
      .done()
    ).deploy();
    
    ProcessDefinition definition =  engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    
    
    double startTime =0;
	double completefirstTask =0;
	double completeSecondTask = 0;
	double sum = 0;
	double num = 3000;
	for (int i = 0; i < num; i++) {
		long time = System.currentTimeMillis();
		org.camunda.bpm.engine.runtime.ProcessInstance instance = engine.getRuntimeService().startProcessInstanceById(definition.getId());
		long time2 = System.currentTimeMillis();
		startTime = startTime+ (time2-time);
		log.info("启动流程实例花费:{}  ms",time2-time);
		org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity instanceEntity = ((org.camunda.bpm.engine.impl.persistence.entity.ProcessInstanceWithVariablesImpl)instance).getExecutionEntity();
		engine.getTaskService().complete(instanceEntity.getTasks().get(0).getId());
		long time3 = System.currentTimeMillis();
		completefirstTask = completefirstTask+ (time3-time2);
		log.info("完成第一个任务花费:{} ms",time3-time2);
		String nextId = engine.getTaskService().createTaskQuery().processInstanceId(instance.getId()).singleResult().getId();
		engine.getTaskService().complete(nextId);
		long time4 = System.currentTimeMillis();
		completeSecondTask = completeSecondTask+ (time4-time3);
		log.info("完成第二个任务花费:{} ms",time4-time3);
		sum = sum+ (time4-time);
		log.info("总的花费是:{} ms",time4-time);
	}
	
	log.info("------------------------------------运行"+ num+"次的耗时-----------------------------------------------------");
	log.info("---启动流程实例总花费:{}  ms,平均耗时:{} ",startTime,startTime/num);
	log.info("---完成第一个任务总花费:{} ms,平均耗时:{} ",completefirstTask,completefirstTask/num);
	log.info("---完成第二个任务总花费:{} ms,平均耗时:{} ",completeSecondTask,completeSecondTask/num);
	log.info("---整个过程总花费:{} ms,平均耗时:{} ",sum,sum/num);
	
}
    
  public static class SysoutDelegate implements JavaDelegate {
    public void execute(DelegateExecution execution) throws Exception {
      System.out.println("Hello from activity " + execution.getCurrentActivityId());
    }
  }  
}