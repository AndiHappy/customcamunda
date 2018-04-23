package com.zlz.customcamunda.cmd.task;

import java.util.Map;

import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;

public class MoveTowarsCmd implements Command<TaskEntity> {

	public MoveTowarsCmd(String activityId, Map<String, Object> variables) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TaskEntity execute(CommandContext commandContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
