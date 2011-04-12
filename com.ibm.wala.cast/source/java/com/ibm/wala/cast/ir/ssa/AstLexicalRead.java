/******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.wala.cast.ir.ssa;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

/**
 * A set of lexical reads. This instruction represents reads of a set of variables that are defined by a pair of
 * variable name and defining code body (i.e. a method or function). This instruction has one local value number
 * definition for each lexical read, and the call graph builder ensures that these value numbers are kept consistent as
 * lexical uses and definitions are discovered during call graph construction.
 * 
 * @author Julian Dolby (dolby@us.ibm.com)
 */
public class AstLexicalRead extends AstLexicalAccess {

  public AstLexicalRead(int iindex, Access[] accesses) {
    super(iindex, accesses);
  }

  public AstLexicalRead(int iindex, Access access) {
    this(iindex, new Access[] { access });
  }

  public AstLexicalRead(int iindex, int lhs, String definer, String globalName) {
    this(iindex, new Access(globalName, definer, lhs));
  }

  public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
    if (defs == null) {
      return new AstLexicalRead(iindex, getAccesses());
    } else {
      Access[] accesses = new Access[getAccessCount()];
      for (int i = 0; i < accesses.length; i++) {
        Access oldAccess = getAccess(i);
        accesses[i] = new Access(oldAccess.variableName, oldAccess.variableDefiner, defs[i]);
      }

      return ((AstInstructionFactory)insts).LexicalRead(iindex, accesses);
    }
  }

  public int getNumberOfDefs() {
    return getAccessCount();
  }

  public int getDef(int i) {
    return getAccess(i).valueNumber;
  }

  public int getNumberOfUses() {
    return 0;
  }

  public int getUse(int i) {
    throw new UnsupportedOperationException();
  }

  public String toString(SymbolTable symbolTable) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < getAccessCount(); i++) {
      Access A = getAccess(i);
      if (i != 0)
        sb.append(", ");
      sb.append(getValueString(symbolTable, A.valueNumber));
      sb.append(" = lexical:");
      sb.append(A.variableName);
      sb.append("@");
      sb.append(A.variableDefiner);
    }

    return sb.toString();
  }

  public void visit(IVisitor v) {
    assert v instanceof AstInstructionVisitor;
    ((AstInstructionVisitor) v).visitAstLexicalRead(this);
  }
}
