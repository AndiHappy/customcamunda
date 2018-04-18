package com.zlz.customcamunda.behavior;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

/**
 * 不建议使用，主要是过于复杂，可以针对某一种类型的行为进行扩展，
 * 不建议全部的重写
 * */
public class CustomeActivityBehavior implements org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior{

	@Override
	public void execute(ActivityExecution execution) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
