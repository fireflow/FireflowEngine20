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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="token")
@XmlType(name="tokenType")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenImpl extends AbsToken implements Token {
	public TokenImpl(){
		
	}
    public TokenImpl(Token previousToken){
    	if(previousToken!=null){			
			this.setBusinessPermitted(previousToken.isBusinessPermitted());
			this.setValue(previousToken.getValue());			
			this.setStepNumber(previousToken.getStepNumber()+1);
			
			this.setProcessInstanceId(previousToken.getProcessInstanceId());
			this.setCallbackTokenId(previousToken.getCallbackTokenId());
			this.setOperationContextName(previousToken.getOperationContextName());
			this.setParentTokenId(previousToken.getParentTokenId());

			this.setProcessId(previousToken.getProcessId());
			this.setVersion(previousToken.getVersion());
			this.setProcessType(previousToken.getProcessType());

			this.setState(TokenState.INITIALIZED);
			
			this.setFromToken(previousToken.getId());
    	}
    }
}
