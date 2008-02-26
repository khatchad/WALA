/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.analysis.reflection;

import com.ibm.wala.analysis.typeInference.PointType;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.ConstantKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * A {@link ContextSelector} to intercept calls to certain methods on java.lang.Class when the receiver is a type
 * constant
 * 
 * Currently supported methods:
 * <ul>
 * <li> getConstructor
 * <li> getConstructors
 * <li> getDeclaredMethod
 * <li> getMethods
 * </ul>
 * 
 * @author pistoia
 * @author sjfink
 */
class JavaLangClassContextSelector implements ContextSelector {

  public JavaLangClassContextSelector() {
  }

  public boolean allSitesDispatchIdentically(CGNode node, CallSiteReference site) {
    return false;
  }

  public boolean contextIsIrrelevant(CGNode node, CallSiteReference site) {
    return false;
  }

  /**
   * If the {@link CallSiteReference} invokes a method we understand and c is a type constant, return a
   * {@link JavaTypeContext} representing the type named by s, if we can resolve it in the {@link IClassHierarchy}.
   */
  public Context getCalleeTarget(CGNode caller, CallSiteReference site, IMethod callee, InstanceKey receiver) {
    if (mayUnderstand(caller, site, callee, receiver)) {
      return new JavaTypeContext(new PointType(getTypeConstant(receiver)));
    }
    return null;
  }

  private IClass getTypeConstant(InstanceKey instance) {
    if (instance instanceof ConstantKey) {
      ConstantKey c = (ConstantKey) instance;
      if (c.getValue() instanceof IClass) {
        return (IClass) c.getValue();
      }
    }
    return null;
  }

  /**
   * This object may understand a dispatch to Class.getContructor when the receiver is a type constant.
   */
  public boolean mayUnderstand(CGNode caller, CallSiteReference site, IMethod targetMethod, InstanceKey instance) {
    if (targetMethod.getReference().equals(JavaLangClassContextInterpreter.GET_CONSTRUCTOR) && getTypeConstant(instance) != null) {
      return true;
    }
    if (targetMethod.getReference().equals(JavaLangClassContextInterpreter.GET_CONSTRUCTORS) && getTypeConstant(instance) != null) {
      return true;
    }
    if (targetMethod.getReference().equals(JavaLangClassContextInterpreter.GET_DECLARED_METHOD)
        && getTypeConstant(instance) != null) {
      return true;
    }
    if (targetMethod.getReference().equals(JavaLangClassContextInterpreter.GET_METHODS)
        && getTypeConstant(instance) != null) {
      return true;
    }
    return false;
  }
}