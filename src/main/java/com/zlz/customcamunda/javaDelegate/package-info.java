/**
 * @author zhailz
 *
 * @version 2018年4月18日 下午3:38:11
 */
package com.zlz.customcamunda.javaDelegate;

/*
 * 

Java Delegates can be attached to a BPMN Service Task.

To implement a class that can be called during process execution, 
this class needs to implement the org.camunda.bpm.engine.delegate.JavaDelegate interface and 
provide the required logic in the execute method. 
When process execution arrives at this particular step, it will execute this logic defined in that method and 
leave the activity in the default BPMN 2.0 way.

Note!
Each time a delegation class referencing activity is executed, 
a separate instance of this class will be created. 
This means that each time an activity is executed 
there will be used another instance of the class to call execute(DelegateExecution).

The classes that are referenced in the process definition (i.e., by using camunda:class ) 
are NOT instantiated during deployment.
Only when a process execution arrives at the point in the process where the class is used for the first time,
an instance of that class will be created. If the class cannot be found, a ProcessEngineException will be thrown. 
The reason for this is that the environment (and more specifically the classpath)
when you are deploying is often different than the actual runtime environment.

*/