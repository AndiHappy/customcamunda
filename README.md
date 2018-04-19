### 流程引擎Camunda 解析说明

camunda流程引擎，2003年Fork自Activiti，逐渐的发展为有自己的特色的一款高效，易用，嵌入式的引擎。

1.  camunda提供一个免费的BPMN2.0的构建工具：Camunda Modeler：[具体的下载地址](https://camunda.com/download/modeler/)

2. 为了检验如何增加扩展功能，自定义CustomRepositoryService服务：[具体的配置见](https://github.com/AndiHappy/customcamunda/wiki/%E6%89%A9%E5%B1%95%E6%88%96%E4%BF%AE%E6%94%B9%E5%BC%95%E6%93%8E%E5%8A%9F%E8%83%BD)
	
3.camunda已经能够支持直接构建bpmnModel，所以直接使用：Bpmn.readModelFrom*** 来校验BPMN文件的正确性

~~~   
       /**
	 * 直接的校验bpmn文件
	 */
	public boolean validateBpmn(InputStream input) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromStream(input);
		// validate the model
		Bpmn.validateModel(modelInstance);
		return modelInstance != null;
	}
~~~   

4. 确认Service task的配置，代理类的用法:[具体的实现逻辑](https://github.com/AndiHappy/customcamunda/wiki/%E5%89%8D%E5%8A%A0%E7%AD%BE%EF%BC%8C%E5%90%8E%E5%8A%A0%E7%AD%BE%E7%9A%84%E5%AE%9E%E7%8E%B0)

5. 扩展流程引擎支持的加签操作：[具体的扩展实现](https://github.com/AndiHappy/customcamunda/wiki/%E5%89%8D%E5%8A%A0%E7%AD%BE%EF%BC%8C%E5%90%8E%E5%8A%A0%E7%AD%BE%E7%9A%84%E5%AE%9E%E7%8E%B0)


