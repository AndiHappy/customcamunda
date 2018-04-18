package com.zlz.customcamunda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

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
import org.camunda.bpm.engine.ProcessEngines;
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

/**
 * 查询时间:[363, 40, 43, 34, 34, 29, 24, 27, 23, 24, 22, 22, 19, 24, 23, 31, 21,
 * 23, 19, 19, 16, 21, 22, 23, 19, 25, 20, 20, 17, 18, 19, 22, 23, 22, 20, 19,
 * 17, 15, 14, 17] Camunda 引擎最好有一个预热的过程，不然第一次的操作可能会比较的慢
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:camunda.cfg.xml")
public class CamundaBaseTest {

	private Logger log = LoggerFactory.getLogger("CamundaBaseTest");

	@Resource
	protected FlowEngine engine;

	protected String orgId = "001";
	protected String prodefName = "001";
	protected String prodefKey = "test1";
	protected String resourceName = "./bpmn/test1.bpmn";
	protected String workFlowDefinitionText = null;
	protected String workflowDefinitionId = null;
	protected ProcessDefinition prodef = null;
	protected boolean update = true;

	@Before
	public void testFindProcessDef() {
		try {
			// 准备流程定义，加载到测试用例中
			engine.buildProcessEngine();
			int num = 1;
			double sum = 0;
			log.info("初始化引擎");
			ArrayList<Integer> value = new ArrayList<Integer>();

			// 校验流程文件
			InputStream inputStream = new FileInputStream(new File(resourceName));
			boolean validate = engine.getCustomRepositoryService().validateBpmnFile(inputStream);
			log.info("校验文件:{},结果:{}", resourceName, validate);
			if (validate) {
				int deplyid = 425;
				for (int i = 0; i < num; i++) {
					if (!update) {
						long time = System.currentTimeMillis();
						ProcessDefinition prodef = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deplyid + "").singleResult();
						if (prodef != null) {
							this.prodef = prodef;
						} else {
							Deployment deployment = engine.getRepositoryService().createDeployment().addInputStream(resourceName, inputStream).deploy();
							String deployid = deployment.getId();
							this.prodef = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployid).singleResult();
							log.info("初始化引擎:{}", this.prodef.getId());
						}
						long time1 = System.currentTimeMillis();
						value.add((int) (time1 - time));
						sum = sum + (time1 - time);
						deplyid = deplyid + 3;
					} else {

						Deployment deployment = engine.getRepositoryService().createDeployment().addInputStream(resourceName, inputStream).deploy();
						String deployid = deployment.getId();
						log.info("deployid:{}", deployid);
						this.prodef = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployid).singleResult();
						log.info("初始化引擎:{}", this.prodef.getId());
					}
				}
			}
			log.info("查询时间:{}", value.toString());
			log.info("平均时间是:{} ms", sum / num);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
