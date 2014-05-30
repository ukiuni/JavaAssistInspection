package org.ukiuni.inspect.javassist;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

@SuppressWarnings("serial")
public class Main implements Serializable {
	public static void main(String[] args) throws Exception {
		CtClass cc = GenerateNewClass();
		Class<?> clazz = cc.toClass();
		Object obj = clazz.newInstance();
		Method method = clazz.getMethod("generateCurrentString");
		for (int i = 0; i < 3; i++) {
			String value = (String) method.invoke(obj);
			System.out.println(value);
		}
		java.lang.annotation.Annotation[] annotations = method.getAnnotations();
		for (java.lang.annotation.Annotation annotation : annotations) {
			System.out.println("\tannotation found " + annotation.toString());
		}
	}

	private static CtClass GenerateNewClass() throws CannotCompileException {
		CtClass cc = ClassPool.getDefault().makeClass("MyClass");

		CtField field = CtField.make("private int calledNumber = 0;", cc);
		cc.addField(field);
		
		CtMethod method = CtNewMethod.make("public String generateCurrentString() {return \" + currentNumber is + \" + calledNumber++;}", cc);
		cc.addMethod(method);
		
		ConstPool constpool = cc.getClassFile().getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation("javax.xml.ws.BindingType", constpool);
		annot.addMemberValue("value", new StringMemberValue("http://www.w3.org/2003/05/soap/bindings/HTTP/", constpool));
		attr.addAnnotation(annot);
		method.getMethodInfo().addAttribute(attr);
		
		return cc;
	}
}
