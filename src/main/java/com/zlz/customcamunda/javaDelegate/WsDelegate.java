package com.zlz.customcamunda.javaDelegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsDelegate implements JavaDelegate {

	private static Logger log = LoggerFactory.getLogger(WsDelegate.class);

	private Expression wsdl;
	private Expression operation;
	private Expression parameters;
	private Expression returnValue;

	public void execute(DelegateExecution execution) throws Exception {
		String wsdlString = (String) wsdl.getValue(execution);
		log.info("wsdlString:{}", wsdlString);

		// JaxWsDynamicClientFactory dcf =
		// JaxWsDynamicClientFactory.newInstance();
		// Client client = dcf.createClient(wsdlString);
		//
		// ArrayList paramStrings = new ArrayList();
		// if (parameters!=null) {
		// StringTokenizer st = new StringTokenizer(
		// (String)parameters.getValue(execution), ",");
		// while (st.hasMoreTokens()) {
		// paramStrings.add(st.nextToken().trim());
		// }
		// }
		Object response = operation.getValue(execution);
		if (returnValue != null) {
			String returnVariableName = (String) returnValue.getValue(execution);
			execution.setVariable(returnVariableName, response);
		}
	}
}