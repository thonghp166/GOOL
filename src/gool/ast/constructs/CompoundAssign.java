/*
 * Copyright 2010 Pablo Arrighi, Alex Concha, Miguel Lezama for version 1.
 * Copyright 2013 Pablo Arrighi, Miguel Lezama, Kevin Mazet for version 2.    
 *
 * This file is part of GOOL.
 *
 * GOOL is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, version 3.
 *
 * GOOL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License version 3 for more details.
 *
 * You should have received a copy of the GNU General Public License along with GOOL,
 * in the file COPYING.txt.  If not, see <http://www.gnu.org/licenses/>.
 */

package gool.ast.constructs;

import gool.ast.type.IType;
import gool.generator.GoolGeneratorController;

public class CompoundAssign extends Assign {

	private Operator operator;
	private String textualoperator;
	private IType type;

	public CompoundAssign(Node var, Expression value, Operator operator,
			String textualoperator, IType type) {
		super(var, value);
		this.operator = operator;
		this.textualoperator = textualoperator;
		this.type = type;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getTextualoperator() {
		return textualoperator;
	}

	public IType getType() {
		return type;
	}

	@Override
	public String callGetCode() {
		return GoolGeneratorController.generator().getCode(this);
	}

}
