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

package gool.generator.common.exception;

import logger.Log;

public class VelocityException extends RuntimeException {

	/**
	 * Serialization id
	 */
	private static final long serialVersionUID = -4800335762729552706L;

	public VelocityException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public String getCauseStack(){
		String mess = super.getCause().toString();
		mess += "\n";
		for(StackTraceElement se : super.getCause().getStackTrace()){
			mess += se.toString() + "\n";
		}
		return mess;
	}
}
