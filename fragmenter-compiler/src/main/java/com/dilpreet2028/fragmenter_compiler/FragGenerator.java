package com.dilpreet2028.fragmenter_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by dilpreet on 11/3/17.
 */

public class FragGenerator {
    private final HashMap<String,Class> fieldMapper =
            new HashMap<>();
    private String packageName;
    private ClassName bundleClass=ClassName.get("android.os","Bundle");
    public FragGenerator() {
        fieldMapper.put("int",Integer.class);
        fieldMapper.put("java.lang.Integer",Integer.class);
        fieldMapper.put("java.lang.String",String.class);
        fieldMapper.put("float",Float.class);
        fieldMapper.put("java.lang.Float",Float.class);
        fieldMapper.put("long",Long.class);
        fieldMapper.put("java.lang.Long",Long.class);
        fieldMapper.put("double",Double.class);
        fieldMapper.put("java.lang.Double",Double.class);
        fieldMapper.put("boolean",Boolean.class);
        fieldMapper.put("java.lang.Boolean",Boolean.class);
        fieldMapper.put("byte",Byte.class);
        fieldMapper.put("java.lang.Byte",Byte.class);
        fieldMapper.put("short",Short.class);
        fieldMapper.put("java.lang.Short",Short.class);

    }

    public void generateClass(Map<String,FragModuleContainer> processorMap ,
                              Filer filer , Elements elementUtils) {

        for(FragModuleContainer fragModule : processorMap.values()) {
            packageName = getPackageName(fragModule.getTypeElement() , elementUtils);


            TypeSpec generatedClass = generateClassData(fragModule );

            JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private MethodSpec generateStaticFunction(FragModuleContainer fragModule) {

        ClassName fragmentClassName=ClassName.get(fragModule.getTypeElement());

        MethodSpec.Builder staticFunctionBuilder=MethodSpec.methodBuilder("newInstance")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(fragmentClassName)
                .addParameters(generateFields(fragModule))
                .addStatement("$T fragment=new $T()",fragmentClassName,fragmentClassName)
                .addStatement("$T bundle=new $T()",bundleClass,bundleClass);

        for(Element element : fragModule.getElements()) {

            staticFunctionBuilder.addStatement("bundle.put$N(\"$L\",$L)",
                    returnBundleFunc(element),element.getSimpleName(),element.getSimpleName());

        }

        staticFunctionBuilder.addStatement("fragment.setArguments(bundle)")
                .addStatement("return fragment");


        return staticFunctionBuilder.build();
    }

    private List<ParameterSpec> generateFields(FragModuleContainer fragModule) {
        List<ParameterSpec> specList=new ArrayList<>();
        ParameterSpec parameterSpec;
        String name;
        for(Element element : fragModule.getElements()) {
            name=element.getSimpleName().toString();
            parameterSpec=ParameterSpec.builder(fieldMapper.
                            get(element.asType().toString()),name)
                            .build();
            specList.add(parameterSpec);
        }
        return specList;
    }

    private TypeSpec generateClassData(FragModuleContainer fragModule) {
        return TypeSpec.classBuilder(fragModule.getTypeElement().
                getSimpleName().toString() + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateStaticFunction(fragModule))
                .addMethod(generateInjectMethods(fragModule))
                .build();
    }

    private MethodSpec generateInjectMethods(FragModuleContainer fragModule) {
        ClassName fragmentName=ClassName.get(fragModule.getTypeElement());
        String name;
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addParameter(fragmentName,"fragment")
                .addStatement("$T bundle=fragment.getArguments()",bundleClass)
                .beginControlFlow("if (bundle != null)");
        for(Element element : fragModule.getElements()) {
            name = element.getSimpleName().toString();
            injectMethodBuilder.addStatement("fragment.$L = bundle.get$N(\"$L\")",
                                                name,returnBundleFunc(element),name);
        }

        injectMethodBuilder.endControlFlow();
        return injectMethodBuilder.build();
    }

    private String getPackageName(TypeElement element, Elements elementsUtils) {
        return elementsUtils.getPackageOf(element).toString();
    }

    private String returnBundleFunc(Element element){
        if(element.asType().toString().compareTo("int") == 0 ||
                element.asType().toString().compareTo("java.lang.Integer") == 0)
            return "Int";
        else
            return fieldMapper.get(element.asType().toString()).getSimpleName();
    }
}
