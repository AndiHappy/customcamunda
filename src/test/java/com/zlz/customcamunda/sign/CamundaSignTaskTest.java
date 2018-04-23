package com.zlz.customcamunda.sign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessInstanceWithVariablesImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zlz.customcamunda.FlowEngine;
import com.zlz.customcamunda.util.PropertiesUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:camunda.cfg.xml")
public class CamundaSignTaskTest {

	private Logger log = LoggerFactory.getLogger("CamundaBaseTest");

	@Resource
	protected FlowEngine engine;

	protected String orgId = "001";
	protected String prodefName = "001";
	protected String prodefKey = "test1";
	protected String resourceName = "./bpmn/simple.bpmn";
	protected String workFlowDefinitionText = null;
	protected String workflowDefinitionId = null;
	protected ProcessDefinition prodef = null;
	protected boolean update = true;
	private static PropertiesUtil util = new PropertiesUtil();

	@Test
	public void ini() {
		try {
			// 准备流程定义，加载到测试用例中
			engine.buildProcessEngine();
			log.info("初始化引擎");
			if (!update) {
				ProcessDefinition prodef = engine.getRepositoryService().createProcessDefinitionQuery()
						.processDefinitionResourceName(resourceName).singleResult();
				if (prodef != null) {
					this.prodef = prodef;
				} else {
					deployProcessDefinition();
				}
			} else {
				deployProcessDefinition();
			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private void deployProcessDefinition() throws FileNotFoundException {
		// 校验流程文件
		boolean validate = engine.validateBpmn(new File(resourceName));
		log.info("校验文件:{},结果:{}", resourceName, validate);
		if (validate) {
			InputStream inputStream = new FileInputStream(new File(resourceName));
			Deployment deployment = engine.getRepositoryService().createDeployment()
					.addInputStream(resourceName, inputStream).deploy();
			String deployid = deployment.getId();
			log.info("deployid:{}", deployid);
			this.prodef = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployid)
					.singleResult();
			log.info("初始化引擎:{}", this.prodef.getId());
		}

	}

	@Test
	public void testBeforeSignInOneMethod() throws FileNotFoundException {
		ini();
		ProcessInstance instance = engine.getRuntimeService().startProcessInstanceById(this.prodef.getId());
		log.info("instance: {} started!", instance.getId());
		util.setPropertiesValue("instanceId", instance.getId());
		Map<String, Object> variables = new HashMap<String, Object>();

		ProcessInstanceWithVariablesImpl instancetmp = (ProcessInstanceWithVariablesImpl) instance;
		List<TaskEntity> tasks = instancetmp.getExecutionEntity().getTasks();
		if (!tasks.isEmpty()) {
			TaskEntity task = tasks.get(0);
			log.info("todo task:{}", task);
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
			engine.getCustomTaskService().beforeSign(taskId, variables, "before-sign-user-100");
		}

		TaskEntity task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();

		engine.getCustomTaskService().beforeSign(util.getPropertyValue("taskId"), variables, "before-sign-user-300");
		task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
	}

	@Test
	public void testAfterSignInOneMethod() throws FileNotFoundException {
		ini();
		ProcessInstance instance = engine.getRuntimeService().startProcessInstanceById(this.prodef.getId());
		log.info("instance: {} started!", instance.getId());
		util.setPropertiesValue("instanceId", instance.getId());
		Map<String, Object> variables = new HashMap<String, Object>();

		ProcessInstanceWithVariablesImpl instancetmp = (ProcessInstanceWithVariablesImpl) instance;
		List<TaskEntity> tasks = instancetmp.getExecutionEntity().getTasks();
		if (!tasks.isEmpty()) {
			TaskEntity task = tasks.get(0);
			log.info("todo task:{}", task);
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
			engine.getCustomTaskService().afterSign(taskId, variables, "after-sign-user-100");
		}

		TaskEntity task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();

		engine.getCustomTaskService().afterSign(util.getPropertyValue("taskId"), variables, "before-sign-user-300");
		task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();
		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();
		
		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();
		
	}
	
	
	@Test
	public void testSignInOneMethod() throws FileNotFoundException {
		ini();
		ProcessInstance instance = engine.getRuntimeService().startProcessInstanceById(this.prodef.getId());
		log.info("instance: {} started!", instance.getId());
		util.setPropertiesValue("instanceId", instance.getId());
		Map<String, Object> variables = new HashMap<String, Object>();

		ProcessInstanceWithVariablesImpl instancetmp = (ProcessInstanceWithVariablesImpl) instance;
		List<TaskEntity> tasks = instancetmp.getExecutionEntity().getTasks();
		if (!tasks.isEmpty()) {
			TaskEntity task = tasks.get(0);
			log.info("todo task:{}", task);
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		
		engine.getCustomTaskService().beforeSign(util.getPropertyValue("taskId"), variables, "before-sign-user-100");

		

		TaskEntity task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();

		engine.getCustomTaskService().afterSign(util.getPropertyValue("taskId"), variables, "after-sign-user-300");
		task = (TaskEntity) engine.getTaskService().createTaskQuery().processInstanceId(instance.getId())
				.singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}

		showHis();

		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			util.setPropertiesValue("taskId", taskId);
		}
	}

	private void showHis() {
		List<HistoricTaskInstance> hiss = engine.getHistoryService().createHistoricTaskInstanceQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).list();
		log.info("------------------------------------------------------");
		for (HistoricTaskInstance h : hiss) {
			log.info("task:taskDefinitionKey:{},ActivityInstanceId:{},activityName:{},assignee:{},endTime:{}",
					h.getTaskDefinitionKey(), h.getActivityInstanceId(), h.getDeleteReason(), h.getAssignee(),
					h.getEndTime());
		}
		log.info("------------------------------------------------------");
	}

	@Test
	public void beforeSign() {
		engine.buildProcessEngine();
		Map<String, Object> variables = new HashMap<String, Object>();
		engine.getCustomTaskService().beforeSign(util.getPropertyValue("taskId"), variables, "300");
		TaskEntity task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			if (task != null) {
				taskId = task.getId();
				util.setPropertiesValue("taskId", taskId);
			}
		} else {
			task = (TaskEntity) engine.getTaskService().createTaskQuery()
					.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
			if (task != null) {
				String taskId = task.getId();
				util.setPropertiesValue("taskId", taskId);
			}
		}
		showHis();
	}

	@Test
	public void complete() {
		engine.buildProcessEngine();
		engine.getTaskService().complete(util.getPropertyValue("taskId"));
		TaskEntity task = (TaskEntity) engine.getTaskService().createTaskQuery()
				.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
		if (task != null) {
			String taskId = task.getId();
			engine.getTaskService().complete(taskId);
			task = (TaskEntity) engine.getTaskService().createTaskQuery()
					.processInstanceId(util.getPropertyValue("instanceId")).singleResult();
			if (task != null) {
				taskId = task.getId();
				util.setPropertiesValue("taskId", taskId);
			}
			showHis();
		}
	}

}
