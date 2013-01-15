package gool.generator.python;

import gool.ast.constructs.ArrayNew;
import gool.ast.constructs.Block;
import gool.ast.constructs.CastExpression;
import gool.ast.constructs.ClassDef;
import gool.ast.constructs.ClassFree;
import gool.ast.constructs.ClassNew;
import gool.ast.constructs.Comment;
import gool.ast.constructs.CustomDependency;
import gool.ast.constructs.Dependency;
import gool.ast.constructs.EnhancedForLoop;
import gool.ast.constructs.EqualsCall;
import gool.ast.constructs.ExpressionUnknown;
import gool.ast.constructs.Field;
import gool.ast.constructs.For;
import gool.ast.constructs.Identifier;
import gool.ast.constructs.If;
import gool.ast.constructs.MainMeth;
import gool.ast.constructs.MapEntryMethCall;
import gool.ast.constructs.MapMethCall;
import gool.ast.constructs.MemberSelect;
import gool.ast.constructs.Meth;
import gool.ast.constructs.Modifier;
import gool.ast.constructs.NewInstance;
import gool.ast.constructs.Package;
import gool.ast.constructs.ParentCall;
import gool.ast.constructs.Return;
import gool.ast.constructs.Statement;
import gool.ast.constructs.This;
import gool.ast.constructs.ThisCall;
import gool.ast.constructs.ToStringCall;
import gool.ast.constructs.TypeDependency;
import gool.ast.constructs.UnaryOperation;
import gool.ast.constructs.VarDeclaration;
import gool.ast.constructs.While;
import gool.ast.list.ListAddCall;
import gool.ast.list.ListContainsCall;
import gool.ast.list.ListGetCall;
import gool.ast.list.ListGetIteratorCall;
import gool.ast.list.ListIsEmptyCall;
import gool.ast.list.ListRemoveAtCall;
import gool.ast.list.ListRemoveCall;
import gool.ast.list.ListSizeCall;
import gool.ast.map.MapContainsKeyCall;
import gool.ast.map.MapEntryGetKeyCall;
import gool.ast.map.MapEntryGetValueCall;
import gool.ast.map.MapGetCall;
import gool.ast.map.MapGetIteratorCall;
import gool.ast.map.MapIsEmptyCall;
import gool.ast.map.MapPutCall;
import gool.ast.map.MapRemoveCall;
import gool.ast.map.MapSizeCall;
import gool.ast.system.SystemCommandDependency;
import gool.ast.system.SystemOutDependency;
import gool.ast.system.SystemOutPrintCall;
import gool.ast.type.TypeArray;
import gool.ast.type.TypeBool;
import gool.ast.type.TypeByte;
import gool.ast.type.TypeChar;
import gool.ast.type.TypeClass;
import gool.ast.type.TypeDecimal;
import gool.ast.type.TypeEntry;
import gool.ast.type.TypeInt;
import gool.ast.type.TypeList;
import gool.ast.type.TypeMap;
import gool.ast.type.TypeMethod;
import gool.ast.type.TypeNone;
import gool.ast.type.TypeNull;
import gool.ast.type.TypeObject;
import gool.ast.type.TypePackage;
import gool.ast.type.TypeString;
import gool.ast.type.TypeUnknown;
import gool.ast.type.TypeVar;
import gool.ast.type.TypeVoid;
import gool.generator.common.CommonCodeGenerator;
import gool.generator.common.Platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class PythonGenerator extends CommonCodeGenerator {
	
	public PythonGenerator() {
		super();
		indentation = "    ";
	}

	private Map<Meth, String> methodsNames = new HashMap<Meth, String>();
	
	private String getName(Meth meth) {
		return methodsNames.get(meth);
	}
	
	@Override
	public void addCustomDependency(String key, Dependency value) {
	}
	
	@Override
	public String getCode(ArrayNew arrayNew) {
		return String.format("%s[%s]", arrayNew.getType(), StringUtils
				.join(arrayNew.getDimesExpressions(), ", "));
	}
	
	@Override
	public String getCode(Block block) {
		StringBuilder result = new StringBuilder();
		for (Statement statement : block.getStatements()) {
			result.append(statement + "\n");
		}
		return result.toString();
	}
	
	@Override
	public String getCode(CastExpression cast) {
		return String.format("%s(%s)", cast.getType(), cast
				.getExpression());
	}

	@Override
	public String getCode(ClassNew classNew) {
		return String.format("%s(%s)", classNew.getName(), StringUtils
				.join(classNew.getParameters(), ", "));
	}

	@Override
	public String getCode(Comment comment) {
		return comment.getValue().replaceAll("(^ *)([^ ])", "$1# $2");
	}
	
	@Override
	public String getCode(EnhancedForLoop enhancedForLoop) {
		return formatIndented("for %s in %s:%1", enhancedForLoop.getVarDec(),
				enhancedForLoop.getExpression() ,enhancedForLoop.getStatements());
	}

	@Override
	public String getCode(EqualsCall equalsCall) {
		return String.format("%s == %s", equalsCall.getTarget(), equalsCall.getParameters().get(0));
	}

	@Override
	public String getCode(Field field) {
		String value;
		if (field.getDefaultValue() != null) {
			value = field.getDefaultValue().toString();
		}
		else {
			value = "None";
		}
		
		return String.format("%s = %s\n", field.getName(), value);
	}

//	@Override
//	public String getCode(FieldAccess sfa) {
//		// TODO Auto-generated method stub
//		return "";
//	}

	@Override
	public String getCode(For forr) {
		return formatIndented("%s\nwhile %s:%1%1",
				forr.getInitializer(),
				forr.getCondition(),
				forr.getWhileStatement(),
				forr.getUpdater());
	}

//	@Override
//	public String getCode(GoolCall goolCall) {
//		// TODO Auto-generated method stub
//		return "";
//	}

	@Override
	public String getCode(If pif) {
		String out = formatIndented ("if %s:%1", pif.getCondition(), pif.getThenStatement());
		if (pif.getElseStatement() != null){
			if (pif.getElseStatement() instanceof If) {
				out += formatIndented ("el%s", pif.getElseStatement());
			}
			else {
				out += formatIndented ("else:%1", pif.getElseStatement());
			}
		}
		return out;
	}

	@Override
	public String getCode(Collection<Modifier> modifiers) {
		return "";
	}

	@Override
	public String getCode(ListAddCall lac) {
		switch (lac.getParameters().size()) {
		case 1:
			return String.format("%s.append(%s)",
					lac.getExpression(), lac.getParameters().get(0));
		case 2:
			return String.format("%s.insert(%s, %s)",
					lac.getExpression(), lac.getParameters().get(1), lac.getParameters().get(0));
		default:
			return String.format("%s.add(%s) # Unrecognized by GOOL, passed on",
					lac.getExpression(), StringUtils.join(lac.getParameters(), ", "));
		}
	}

	@Override
	public String getCode(ListContainsCall lcc) {
		return String.format("%s in %s", StringUtils.join(lcc.getParameters(), ", "), lcc.getExpression());
	}

	@Override
	public String getCode(ListGetCall lgc) {
		return String.format("%s[%s]", lgc.getExpression(), lgc.getParameters().get(0));
	}

	@Override
	public String getCode(ListGetIteratorCall lgic) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(ListIsEmptyCall liec) {
		return String.format("(not %s)", liec.getExpression());
	}

//	@Override
//	public String getCode(ListMethCall lmc) {
//		// TODO Auto-generated method stub
//		return "";
//	}

	@Override
	public String getCode(ListRemoveAtCall lrc) {
		return String.format("%s.pop(%s)", lrc.getExpression(), StringUtils
				.join(lrc.getParameters(), ", "));
	}

	@Override
	public String getCode(ListRemoveCall lrc) {
		return String.format("%s.remove(%s)", lrc.getExpression(), StringUtils
				.join(lrc.getParameters(), ", "));
	}

	@Override
	public String getCode(ListSizeCall lsc) {
		return String.format("len(%s)", lsc.getExpression());
	}

	@Override
	public String getCode(MainMeth mainMeth) {
		return mainMeth.getBlock().toString();
	}

	@Override
	public String getCode(MapContainsKeyCall mapContainsKeyCall) {
		return String.format("%s in %s",
				mapContainsKeyCall.getParameters().get(0), mapContainsKeyCall.getExpression());
	}

	@Override
	public String getCode(MapEntryGetKeyCall mapEntryGetKeyCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapEntryGetValueCall mapEntryGetKeyCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapEntryMethCall mapEntryMethCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapGetCall mapGetCall) {
		return String.format("%s[%s]", mapGetCall.getExpression(), mapGetCall.getParameters().get(0));
	}

	@Override
	public String getCode(MapGetIteratorCall mapGetIteratorCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapIsEmptyCall mapIsEmptyCall) {
		return String.format("(not %s)", mapIsEmptyCall.getExpression());
	}

	@Override
	public String getCode(MapMethCall mapMethCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapPutCall mapPutCall) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(MapRemoveCall mapRemoveCall) {
		return String.format("%s.pop(%s, None)", mapRemoveCall.getExpression(),
				StringUtils.join(mapRemoveCall.getParameters(), ", "));
	}

	@Override
	public String getCode(MapSizeCall mapSizeCall) {
		return String.format("len(%s)", mapSizeCall.getExpression());
	}

	@Override
	public String getCode(Meth meth) {
		if(meth.isMainMethod()) {
			return meth.getBlock().toString();
		}
		else {
			return formatIndented("def %s(self%s%s):%1",
					methodsNames.get(meth),
					meth.getParams().size()>0?", ":"",
					StringUtils.join(meth.getParams(),", ").replaceAll("\n", ""),
					meth.getBlock().getStatements().isEmpty()?"pass":meth.getBlock());
		}
	}

	@Override
	public String getCode(MemberSelect memberSelect) {
		return String.format("%s.%s", memberSelect.getTarget().toString().equals("this")?"self":memberSelect.getTarget(), memberSelect
				.getIdentifier());
	}
	
	@Override
	public String getCode(Modifier modifier) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(NewInstance newInstance) {
		return String.format("%s = %s( %s )", newInstance.getVariable(),
				newInstance.getVariable().getType().toString().replaceAll(
						"\\*$", ""), StringUtils.join(newInstance
						.getParameters(), ", "));
	}

	@Override
	public String getCode(ParentCall parentCall) {
		String out = parentCall.getTarget() + "(";
		if (parentCall.getParameters() != null) {
			out += StringUtils.join(parentCall.getParameters(), ", ");
		}
		out += ")";
		return out;
	}

	@Override
	public String getCode(Return returnExpr) {
		return String.format("return %s", returnExpr.getExpression());
	}

	@Override
	public String getCode(SystemOutDependency systemOutDependency) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(SystemOutPrintCall systemOutPrintCall) {
		return String.format("print %s", StringUtils.join(
				systemOutPrintCall.getParameters(), ","));
	}

	@Override
	public String getCode(This pthis) {
		return "self";
	}

	@Override
	public String getCode(ThisCall thisCall) {
		return "";
	}

	@Override
	public String getCode(ToStringCall tsc) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeBool typeBool) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeByte typeByte) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeClass typeClass) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeDecimal typeReal) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeDependency typeDependency) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeEntry typeEntry) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeInt typeInt) {
		return "int";
	}

	@Override
	public String getCode(TypeList typeList) {
		return "list";
	}

	@Override
	public String getCode(TypeMap typeMap) {
		return "dict";
	}

	@Override
	public String getCode(TypeNone type) {
		return "None";
	}

	@Override
	public String getCode(TypeNull type) {
		return "None";
	}

	@Override
	public String getCode(TypeObject typeObject) {
		return "object";
	}

	@Override
	public String getCode(TypeString typeString) {
		return "str";
	}

	@Override
	public String getCode(TypeVoid typeVoid) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(UnaryOperation unaryOperation) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(VarDeclaration varDec) {
		String value;
		if(varDec.getInitialValue() != null) {
			value = varDec.getInitialValue().toString();
		}
		else {
			value = "None";
		}
		
		return String.format("%s = %s", varDec.getName(), value);
	}

	@Override
	public String getCode(While whilee) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeArray typeArray) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(CustomDependency customDependency) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(Identifier identifier) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeUnknown typeUnknown) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(ExpressionUnknown unknownExpression) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(ClassFree classFree) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(Platform platform) {
		return platform.getName();
	}
	
	@Override
	public String getCode(Package _package) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(SystemCommandDependency systemCommandDependency) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypePackage typePackage) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeMethod typeMethod) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCode(TypeVar typeVar) {
		// TODO Auto-generated method stub
		return "";
	}
	
	@Override
	public String printClass(ClassDef classDef) {
		String code = String.format("class %s%s:", classDef.getName(),
				(classDef.getParentClass() != null) ? "(" + classDef.getParentClass().getName() + ")" : "");
		
		for(Field f : classDef.getFields()) {
			code = code + formatIndented("%1", f);
		}

		List<Meth> meths = new ArrayList<Meth>();
		for(Meth method : classDef.getMethods()) { //On parcourt les méthodes
			if(getName(method) == null) {	//Si la méthode n'a pas encore été renommée
				meths.clear();
				
				for(Meth m : classDef.getMethods()) { //On récupère les méthodes de mêmes noms
					if(m.getName().equals(method.getName())) {
						meths.add(m);
					}
				}
				
				if(meths.size()>1) { //Si il y a plusieurs méthodes de même nom
					String block = "";
					String newName = method.getName();
					int i = 0;
					boolean first = true;
					
					for(Meth m2 : meths) {
						
						newName = method.getName() + i++;
						while(methodsNames.containsValue(newName)) {
							newName = method.getName() + i++;
						}
						methodsNames.put(m2, newName);
						
						String conditions = "";
						int cpt = 0;
						for(VarDeclaration p : m2.getParams()) {
							conditions += String.format(" and isinstance(args[%s],%s)", cpt++, p.getType());
							
						}
						
						block += formatIndented("%sif len(args) == %s%s:%1", first?"":"el", m2.getParams().size(), conditions, "self." + newName + "(*args)");  
						first = false;
					}
					
					String name = method.getName();
					if(method.isConstructor()) {
						name = "__init__";
					}
					
					code += formatIndented("%1", formatIndented("def %s(self, *args):%1", name, block));
					
				}
				else {
					if(method.isConstructor()) {
						methodsNames.put(method, "__init__");
					}
					else {
						methodsNames.put(method, method.getName());
					}
				}
			}
		}
		
		for(Meth method : classDef.getMethods()) {
			code = code + formatIndented("%1", method);
		}

		return code;
	}


	@Override
	public String getCode(TypeChar typeChar) {
		// TODO Auto-generated method stub
		return null;
	}

}
