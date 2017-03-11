package com.dilpreet2028.fragmenter_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String packageName;
        for(FragModuleContainer fragModule : processorMap.values()) {
            packageName = getPackageName(fragModule.getTypeElement() , elementUtils);

            List<MethodSpec> methodSpecList = generateMethods(fragModule);

            TypeSpec generatedClass = generateClass(fragModule, methodSpecList);

            JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private List<MethodSpec> generateMethods(FragModuleContainer fragModule) {
        List<MethodSpec> methodSpecList = new ArrayList<>();

        for(Element element : fragModule.getElements()) {

            MethodSpec methodSpec=MethodSpec.methodBuilder("get"+element.getSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(fieldMapper.get(element.asType().toString()))
                    .addStatement("return $L",element.getSimpleName())
                    .build();

            methodSpecList.add(methodSpec);
        }

        return methodSpecList;
    }

    private TypeSpec generateClass(FragModuleContainer fragModule, List<MethodSpec> methodSpecList) {
        ClassName parentClass= ClassName.get(fragModule.getTypeElement());

        return TypeSpec.classBuilder(fragModule.getTypeElement().getSimpleName().toString() + "Builder")
                .superclass(parentClass)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methodSpecList)
                .build();
    }

    private String getPackageName(TypeElement element, Elements elementsUtils) {
        return elementsUtils.getPackageOf(element).toString();
    }
}
