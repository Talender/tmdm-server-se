// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation （1.1.2_01，编译版 R40）
// Generated source version: 1.1.2

package com.amalto.core.webservice;


public class WSStartProcessInstance {
    protected com.amalto.core.webservice.WSWorkflowProcessDefinitionUUID processUUID;
    protected com.amalto.core.webservice.WSHashMap variable;
    
    public WSStartProcessInstance() {
    }
    
    public WSStartProcessInstance(com.amalto.core.webservice.WSWorkflowProcessDefinitionUUID processUUID, com.amalto.core.webservice.WSHashMap variable) {
        this.processUUID = processUUID;
        this.variable = variable;
    }
    
    public com.amalto.core.webservice.WSWorkflowProcessDefinitionUUID getProcessUUID() {
        return processUUID;
    }
    
    public void setProcessUUID(com.amalto.core.webservice.WSWorkflowProcessDefinitionUUID processUUID) {
        this.processUUID = processUUID;
    }
    
    public com.amalto.core.webservice.WSHashMap getVariable() {
        return variable;
    }
    
    public void setVariable(com.amalto.core.webservice.WSHashMap variable) {
        this.variable = variable;
    }
}
