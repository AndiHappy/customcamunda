package com.zlz.customcamunda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:camunda.cfg.xml")
public class CamundaBaseTest {

	private Logger log = LoggerFactory.getLogger("CamundaBaseTest");

	@Resource
	protected FlowEngine engine;

	protected String orgId = "001";
	protected String resourceName = "./bpmn/ABCD.bpmn";
	protected String workFlowDefinitionText = null;
	protected String workflowDefinitionId = null;
	protected ProcessDefinition prodef = null;
	protected boolean update = false;

	@Before
	public void testFindProcessDef() {
		try {
			// 准备流程定义，加载到测试用例中
			engine.buildProcessEngine();
			log.info("初始化引擎");
			if (!update) {
				ProcessDefinition prodef = engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionResourceName(resourceName).singleResult();
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
			Deployment deployment = engine.getRepositoryService().createDeployment().addInputStream(resourceName, inputStream).deploy();
			String deployid = deployment.getId();
			log.info("deployid:{}", deployid);
			this.prodef = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployid).singleResult();
			log.info("初始化引擎:{}", this.prodef.getId());
		}

	}

	@Test
	public void getService() {
		// 准备流程定义，加载到测试用例中，必须有
		engine.buildProcessEngine();
		ProcessEngine processEngine = engine.getProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		log.info(repositoryService.toString());
		RuntimeService runtimeService = processEngine.getRuntimeService();
		log.info(runtimeService.toString());
		TaskService taskService = processEngine.getTaskService();
		log.info(taskService.toString());
		IdentityService IdentityService = processEngine.getIdentityService();
		log.info(IdentityService.toString());
		FormService formService = processEngine.getFormService();
		log.info(formService.toString());
		HistoryService historyService = processEngine.getHistoryService();
		log.info(historyService.toString());
		ManagementService managementService = processEngine.getManagementService();
		log.info(managementService.toString());
		FilterService filterService = processEngine.getFilterService();
		log.info(filterService.toString());
		ExternalTaskService externalTaskService = processEngine.getExternalTaskService();
		log.info(externalTaskService.toString());
		CaseService caseService = processEngine.getCaseService();
		log.info(caseService.toString());
		DecisionService decisionService = processEngine.getDecisionService();
		log.info(decisionService.toString());
	}
}
