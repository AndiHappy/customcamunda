package com.zlz.customcamunda.server;

import java.io.InputStream;

import org.camunda.bpm.engine.impl.RepositoryServiceImpl;

public class CustomRepositoryService extends RepositoryServiceImpl {

	public boolean validateBpmnFile(InputStream input) {
		return commandExecutor.execute(new ValidateBPMNFileCmd(input));
	}

}
