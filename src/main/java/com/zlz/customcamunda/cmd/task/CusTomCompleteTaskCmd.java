package com.zlz.customcamunda.cmd.task;

import java.util.Map;

import org.camunda.bpm.engine.impl.cmd.CompleteTaskCmd;

public class CusTomCompleteTaskCmd extends CompleteTaskCmd {

	private static final long serialVersionUID = 1L;

	public CusTomCompleteTaskCmd(String taskId, Map<String, Object> variables, boolean localScope) {
		super(taskId, variables);
	}

	public CusTomCompleteTaskCmd(String taskId, Map<String, Object> variables) {
		super(taskId, variables);
	}


}
