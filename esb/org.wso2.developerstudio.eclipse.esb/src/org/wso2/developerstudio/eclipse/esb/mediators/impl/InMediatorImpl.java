/*
 * Copyright 2009-2010 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.developerstudio.eclipse.esb.mediators.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.w3c.dom.Element;
import org.wso2.developerstudio.eclipse.esb.Mediator;
import org.wso2.developerstudio.eclipse.esb.impl.ParentMediatorImpl;
import org.wso2.developerstudio.eclipse.esb.mediators.InMediator;
import org.wso2.developerstudio.eclipse.esb.mediators.MediatorsPackage;
import org.wso2.developerstudio.eclipse.esb.util.ObjectValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>InMediator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class InMediatorImpl extends ParentMediatorImpl implements InMediator {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InMediatorImpl() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doLoad(Element self) throws Exception {
		super.doLoad(self);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Element doSave(Element parent) throws Exception {
		Element self = createChildElement(parent, "in");
		for (Mediator child : getChildren()) {
			child.save(self);
		}
		if(description!=null)
			description.save(self);
		
		addComments(self);
		return self;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	
	@Override
	protected EClass eStaticClass() {
		return MediatorsPackage.Literals.IN_MEDIATOR;
	}

	
    public Map<String, ObjectValidator> validate() {
	    // TODO Auto-generated method stub
	    return null;
    }

} //InMediatorImpl
