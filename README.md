# customcamunda
camunda流程引擎扩展，支持中国方式的流程运转，流程定义

1. Camunda Modeler 这个是根据bpmn2.0规范，建立bpmn文件的APP工具，目前我使用的是mac版本的，这个工具是开源的

2. 为了检验如何增加扩展功能，自定义CustomRepositoryService服务，扩展自RepositoryService

	增加校验bpmn文件的Command：ValidateBPMNFileCmd
	
3. 发现camunda已经能够代码构建bpmnModel，所以直接使用：Bpmn.readModelFrom*** 来校验BPMN文件的正确性

4. 确认Service task的配置，代理类的用法，具体的说明在WIKI里面


