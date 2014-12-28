/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.pvm.kernel.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absTokenType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TokenImpl.class,TokenHistory.class})
public abstract class AbsToken extends AbsWorkflowEntity implements Token {
    private String processInstanceId = null;
    private String elementInstanceId = null;
    
    private Boolean businessPermitted = Boolean.TRUE;
    private Integer value = 0;
    private Integer stepNumber = 0;
    private TokenState state = TokenState.INITIALIZED;
    
    private String processId = null;
    private int version = 0;
    private String processType = null;
    private String elementId = null;
    
    private OperationContextName operationContextName = OperationContextName.NORMAL;
    private String callbackTokenId = null;
    private String parentTokenId = null;
    private String fromToken = null;
    private String nextCompensationToken = null;
    private String compensationCode = null;
    private Boolean isContainer = Boolean.FALSE;
    
    private String attachedToToken = null;


    public AbsToken(){
    	
    }
    

    /* (non-Javadoc)
     * @see org.fireflow.kenel.IToken#getValue()
     */
    public Integer getValue() {

        return value;
    }

   
    public Boolean isBusinessPermitted() {
        return businessPermitted;
    }

    public void setBusinessPermitted(Boolean alive) {
        this.businessPermitted = alive;
    }



    public void setValue(Integer v) {
        value = v;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String wfElementId) {
        this.elementId = wfElementId;
    }



    public void setProcessInstanceId(String id) {
        this.processInstanceId = id;
    }

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    public Integer getStepNumber(){
        return this.stepNumber;
    }

    public void setStepNumber(Integer i){
        this.stepNumber = i;
    }

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getCallbackTokenId()
	 */
	public String getCallbackTokenId() {
		return this.callbackTokenId;
	}

	public void setCallbackTokenId(String id){
		this.callbackTokenId = id;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getParentTokenId()
	 */
	public String getParentTokenId() {
		
		return this.parentTokenId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getProcessId()
	 */
	public String getProcessId() {
		
		return this.processId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getProcessType()
	 */
	public String getProcessType() {
		return this.processType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getState()
	 */
	public TokenState getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getVersion()
	 */
	public Integer getVersion() {
		return version;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getWorkflowElementInstanceId()
	 */
	public String getElementInstanceId() {
		
		return elementInstanceId;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setParentTokenId(java.lang.String)
	 */
	public void setParentTokenId(String pid) {
		this.parentTokenId = pid;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setProcessId(java.lang.String)
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setProcessType(java.lang.String)
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setState(org.fireflow.pvm.kernel.TokenState)
	 */
	public void setState(TokenState state) {
		this.state = state;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version) {
		this.version = version;
		
	}

	
	
	/**
	 * @return the fromToken
	 */
	public String getFromToken() {
		return fromToken;
	}


	/**
	 * @param fromToken the fromToken to set
	 */
	public void setFromToken(String fromToken) {
		this.fromToken = fromToken;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setWorkflowElementInstanceId(java.lang.String)
	 */
	public void setElementInstanceId(String elementInstanceId) {
		this.elementInstanceId = elementInstanceId;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#getOperationContextName()
	 */
	public OperationContextName getOperationContextName() {

		return this.operationContextName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.Token#setOperationContextName(org.fireflow.pvm.kernel.OperationContextName)
	 */
	public void setOperationContextName(OperationContextName opCtxName) {

		this.operationContextName = opCtxName;
	}


	/**
	 * @return the nextCompensationToken
	 */
	public String getNextCompensationToken() {
		return nextCompensationToken;
	}


	/**
	 * @param nextCompensationToken the nextCompensationToken to set
	 */
	public void setNextCompensationToken(String nextCompensationToken) {
		this.nextCompensationToken = nextCompensationToken;
	}


	/**
	 * @return the compensationCode
	 */
	public String getCompensationCode() {
		return compensationCode;
	}


	/**
	 * @param compensationCode the compensationCode to set
	 */
	public void setCompensationCode(String compensationCode) {
		this.compensationCode = compensationCode;
	}


	
	/**
	 * @return the attachedToToken
	 */
	public String getAttachedToToken() {
		return attachedToToken;
	}


	/**
	 * @param attachedToToken the attachedToToken to set
	 */
	public void setAttachedToToken(String attachedToToken) {
		this.attachedToToken = attachedToToken;
	}


	public Boolean isContainer(){
		return isContainer;
	}
	public void setContainer(Boolean b){
		isContainer = b;
	}
	
}
