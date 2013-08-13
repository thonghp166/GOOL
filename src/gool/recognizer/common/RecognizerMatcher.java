/*
 * Copyright 2010 Pablo Arrighi, Alex Concha, Miguel Lezama for version 1 of this file.
 * Copyright 2013 Pablo Arrighi, Miguel Lezama, Kevin Mazet for version 2 of this file.    
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





package gool.recognizer.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gool.ast.constructs.ClassDef;
import gool.ast.constructs.ClassNew;
import gool.ast.constructs.Expression;
import gool.ast.constructs.Field;
import gool.ast.constructs.Language;
import gool.ast.constructs.MemberSelect;
import gool.ast.constructs.Meth;
import gool.ast.constructs.MethCall;
import gool.ast.constructs.Modifier;
import gool.ast.constructs.VarAccess;
import gool.ast.constructs.VarDeclaration;
import gool.ast.type.IType;
import gool.ast.type.TypeUnknown;
import gool.ast.type.TypeVoid;
import gool.generator.GoolGeneratorController;
import gool.generator.common.CodeGenerator;
import gool.generator.common.Platform;
import gool.generator.java.JavaGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import gool.recognizer.common.MethodSignature;

public class RecognizerMatcher{

	private static Language InputLang;
	
	private static ArrayList<String> EnabledGoolLibs;
	private static ArrayList<String> BuiltGoolClassesNames;
	private static HashMap<String, MethodSignature> RecognizedGoolMethods;
	private static Map<IType, ClassDef> GoolClasses;


	/*
	 *  methods called by the input language recognizer to modify the nodes they constructs
	 */
	static public void init(Language inputLang, Platform outputLang, Map<IType, ClassDef> goolClasses){
		InputLang = inputLang;
		EnabledGoolLibs = new ArrayList<String>();
		BuiltGoolClassesNames = new ArrayList<String>();
		RecognizedGoolMethods = new HashMap<String, MethodSignature>();
		GoolClasses = goolClasses;
		ArrayList<String> defaultGoolLibs = getDefaultGoolLibs();
		if(!(defaultGoolLibs.size()==0))
			for(String lib: defaultGoolLibs)
				EnabledGoolLibs.add(lib);
	}
	
	public static String matchImport(String InputLangImport){
		String GoolLib = getMatchedGoolLib(InputLangImport);
		if(GoolLib!=null && !EnabledGoolLibs.contains(GoolLib))
			EnabledGoolLibs.add(GoolLib);
		//String GoolLib = getMatchedGoolLib(InputLangImport);
		return GoolLib;
	}

	public static String matchType(String InputLangClassName){
		return getMatchedGoolClass(InputLangClassName);
	}

	public static void matchDec(VarDeclaration variable){
		if(!(variable.getType() instanceof TypeUnknown))
			return;
		String InputLangClass = variable.getType().getName();
		String GoolClass = getMatchedGoolClass(InputLangClass);
		// if we don't find any GoolClass matched with the InputLangClass, we do nothing and pass on.
			if(GoolClass==null)
				return;
		// else, we change the type of the variable node by the GoolClass type
		// and we build a Gool library of this GoolClass if it hasn't been done yet
		variable.setType(new TypeUnknown(GoolClass));
		/*
		if(!BuiltGoolClassesNames.contains(GoolClass)){
			ClassDef GoolClassAST = buildGoolClass(GoolClass);
			GoolClasses.put(GoolClassAST.getType(), GoolClassAST);
			}
		*/
	}


	public static void matchClassNew(ClassNew ClassNew){
		if(ClassNew.getType() instanceof TypeUnknown){
			String InputLangClass = ClassNew.getType().getName();
			String GoolClass = getMatchedGoolClass(InputLangClass);
			ClassNew.setType(new TypeUnknown(GoolClass));
		}
	}

	public static void matchMethCall(MethCall MethCall){
		// substitution of MethCall type
		if(MethCall.getType() instanceof TypeUnknown){
			String InputLangClass = MethCall.getType().getName();
			String GoolClass = getMatchedGoolClass(InputLangClass);
			MethCall.setType(new TypeUnknown(GoolClass));
		}
		
		// substitution of the method's name
		if(MethCall.getTarget()!=null)
		{
			IType TargetClassType = ((VarAccess)((MemberSelect)MethCall.getTarget()).getTarget()).getDec().getType();
			if(TargetClassType instanceof TypeUnknown){
				String GoolClass = TargetClassType.getName();
				MethodSignature MethSign = new MethodSignature(MethCall);
				String GoolMethod = getMatchedGoolMethod(GoolClass, MethSign);
				((MemberSelect)MethCall.getTarget()).getDec().setName(GoolMethod);
				RecognizedGoolMethods.put(GoolClass+"."+GoolMethod, MethSign);
			}
		}
	}


	/*
	 *  methods used by the GoolMatcher to parse files and get the matching informations
	 */
	static private ArrayList<String> getDefaultGoolLibs(){
		ArrayList<String> res = new ArrayList<String>();
		try{
			InputStream ips= new FileInputStream(getPathOfInputImportMatchFile()); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line=br.readLine())!=null){
				line=removeSpaces(line);
				if(isInputMatchLine(line)){
					String GoolLib=getLeftPartOfInputMatchLine(line);
					String Import=getRightPartOfInputMatchLine(line);
					if(Import.equals("DEFAULT"))
						res.add(GoolLib);
				}	
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		System.out.println("res="+res.size());
		return res;
	}

	static private String getMatchedGoolLib(String InputLangImport){
		String res = null;
		try{
			InputStream ips= new FileInputStream(getPathOfInputImportMatchFile()); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line=br.readLine())!=null){
				line=removeSpaces(line);
				if(isInputMatchLine(line)){
					String GoolLib=getLeftPartOfInputMatchLine(line);
					String Import=getRightPartOfInputMatchLine(line);
					//TODO: this has to be improved
					if(Import.contains(InputLangImport)){
						res = GoolLib;
						break;
					}
				}	
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		return res;
	}

	static private String getMatchedGoolClass(String InputLangClass){
		String res = null;
		boolean matchFound=false;
		for(String GoolLib: EnabledGoolLibs){
			try{
				InputStream ips= new FileInputStream(getPathOfInputClassMatchFile(GoolLib)); 
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				String line;
				while ((line=br.readLine())!=null){
					line=removeSpaces(line);
					if(isInputMatchLine(line)){
						String GoolClass=getLeftPartOfInputMatchLine(line);
						String Classes=getRightPartOfInputMatchLine(line);
						//TODO: this has to be improved
						if(Classes.contains(InputLangClass)){
							res = GoolClass;
							matchFound=true;
							break;
						}
					}	
				}
				br.close(); 
				if(matchFound)
					break;
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
		}
		return res;
	}
	static private String getMatchedGoolMethod(String GoolClass, MethodSignature InputLangMethSign){
		String res = null;
		boolean matchFound=false;
		for(String GoolLib: EnabledGoolLibs){
			try{
				InputStream ips= new FileInputStream(getPathOfInputMethodMatchFile(GoolLib)); 
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				String line;
				while ((line=br.readLine())!=null){
					line=removeSpaces(line);
					if(isInputMatchLine(line)){
						String GoolFullMethodName=getLeftPartOfInputMatchLine(line);
						String GoolClassName=GoolFullMethodName.substring(0, GoolFullMethodName.lastIndexOf("."));
						MethodSignature MethSign=new MethodSignature(getRightPartOfInputMatchLine(line));
						if(GoolClassName.equals(GoolClass) && MethSign.isCompatibleWith(InputLangMethSign)){
							res = GoolFullMethodName.substring(GoolClassName.length()+1);
							matchFound=true;
							break;
						}
					}	
				}
				br.close(); 
				if(matchFound)
					break;
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
		}
		return res;
	}









	/*
	 *  methods used by the GoolMatcher to parse each line of a match file
	 */
	static private String removeSpaces(String line){
		for(int i=0; i<line.length(); i++){
			if(line.charAt(i)==' ' || line.charAt(i)=='\t'){
				line=line.substring(0,i)+line.substring(i+1);
				i-=1;
			}
		}
		return line;
	}
	static private boolean isCommentLine(String line){
		return line.startsWith("#");
	}
	static private boolean isInputMatchLine(String line){
		return !isCommentLine(line) && line.contains("<-");
	}
	static private boolean isOutputMatchLine(String line){
		return !isCommentLine(line) && line.contains("->");
	}
	static private String getLeftPartOfInputMatchLine(String InputMatchLine){
		return InputMatchLine.substring(0, InputMatchLine.indexOf("<-"));
	}
	static private String getRightPartOfInputMatchLine(String InputMatchLine){
		return InputMatchLine.substring(InputMatchLine.indexOf("<-")+2);
	}
	static private String getLeftPartOfOutputMatchLine(String OutputMatchLine){
		return OutputMatchLine.substring(0, OutputMatchLine.indexOf("->"));
	}
	static private String getRightPartOfOutputMatchLine(String OutputMatchLine){
		return OutputMatchLine.substring(OutputMatchLine.indexOf("->")+2);
	}




	/*
	 *  methods used by the GoolMatcher to compute the path to match files
	 */
	static private String getPathOfInputMatchDir(String GoolLibName){
		return "src/gool/recognizer/" + langToString(InputLang).toLowerCase() + "/matching/" + GoolLibName + "/";
	}
	static private String getPathOfInputImportMatchFile(){
		return "src/gool/recognizer/" + langToString(InputLang).toLowerCase() + "/matching/ImportMatching.properties";
	}
	static private String getPathOfInputClassMatchFile(String GoolLibName){
		return getPathOfInputMatchDir(GoolLibName) + "ClassMatching.properties";
	}
	static private String getPathOfInputMethodMatchFile(String GoolLibName){
		return getPathOfInputMatchDir(GoolLibName) + "MethodMatching.properties";
	}



	// translation of a Language to a String
	static private String langToString(Language lang){
		String res = "";
		switch(lang){
		case JAVA:
			res = "Java";
			break;
		case CPP:
			res = "Cpp";
			break;
		case CSHARP:
			res = "CSharp";
			break;
		case OBJC:
			res = "Objc";
			break;
		case PYTHON:
			res = "Python";
			break;
		case ANDROID:
			res = "Android";
			break;
		}
		return res;
	}
}