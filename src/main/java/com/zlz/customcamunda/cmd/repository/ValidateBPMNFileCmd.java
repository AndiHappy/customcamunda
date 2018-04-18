package com.zlz.customcamunda.cmd.repository;

import java.io.InputStream;

import org.camunda.bpm.engine.impl.bpmn.deployer.BpmnDeployer;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;

public class ValidateBPMNFileCmd implements Command<Boolean> {

	private InputStream input;

	public ValidateBPMNFileCmd(InputStream input) {
		this.setInput(input);
	}

	@Override
	public Boolean execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl processConfig = Context.getProcessEngineConfiguration();
		BpmnDeployer bpmnDeployer = new BpmnDeployer();
		bpmnDeployer.setExpressionManager(processConfig.getExpressionManager());
		bpmnDeployer.setIdGenerator(processConfig.getIdGenerator());

		BpmnParseFactory bpmnParseFactory = null;
		if (bpmnParseFactory == null) {
			bpmnParseFactory = new DefaultBpmnParseFactory();
		}

		BpmnParser bpmnParser = new BpmnParser(processConfig.getExpressionManager(), bpmnParseFactory);

		DeploymentEntity deployment = new DeploymentEntity();
		BpmnParse bpmnParse = bpmnParser.createParse().sourceInputStream(this.input).deployment(deployment );

		if (!deployment.isValidatingSchema()) {
			bpmnParse.setSchemaResource(null);
		}

		bpmnParse.execute();
		return bpmnParse.getProcessDefinitions() != null && !bpmnParse.getProcessDefinitions().isEmpty();
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

}
