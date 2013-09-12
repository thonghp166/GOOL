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

import gool.generator.GoolGeneratorController;

/**
 * This class captures the "free" of the intermediate language, i.e freeing an
 * unused object.
 */
public final class ClassFree extends Statement {

	/**
	 * The pointer to the instance.
	 */
	private ClassNew classnew;

	/**
	 * @param classcall
	 *            is a pointer to the objects instantiation place.
	 */
	public ClassFree(ClassNew classcall) {
		this.setClassNew(classcall);
	}

	/**
	 * Sets the node that instantiates the object.
	 * 
	 * @param classnew
	 *            the class that instantiates the object.
	 */
	public final void setClassNew(ClassNew classnew) {
		this.classnew = classnew;
	}

	/**
	 * Gets the class instantiation node.
	 * 
	 * @return the node that instantiates the object.
	 */
	public final ClassNew getClassNew() {
		return classnew;
	}

	@Override
	public String callGetCode() {
		return GoolGeneratorController.generator().getCode(this);
	}
}
