package com.zlz.customcamunda.server;

import java.io.InputStream;

import org.camunda.bpm.engine.impl.RepositoryServiceImpl;

import com.zlz.customcamunda.cmd.repository.ValidateBPMNFileCmd;

public class CustomRepositoryService extends RepositoryServiceImpl {

	public boolean validateBpmnFile(InputStream input) {
		return commandExecutor.execute(new ValidateBPMNFileCmd(input));
	}

}
