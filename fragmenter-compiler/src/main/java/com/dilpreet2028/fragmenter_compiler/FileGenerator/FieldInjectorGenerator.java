package com.dilpreet2028.fragmenter_compiler.FileGenerator;

import com.dilpreet2028.fragmenter_annotations.Injector;
import com.dilpreet2028.fragmenter_compiler.FragModuleContainer;
import com.dilpreet2028.fragmenter_compiler.ProcessorException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by dilpreet on 12/3/17.
 */

public class FieldInjectorGenerator implements Generator {

    private static final String MAPPING_PACKAGE = "com.dilpreet2028.fragmenter";
    private static final String MAPPING_CLASS = "FieldInjector";
    private Elements elementUtils;
    private Map<String, FragModuleContainer> processorMap;

    @Override
    public void generateClass(Map<String, FragModuleContainer> processorMap, Filer filer, Elements elementUtils)
            throws ProcessorException{

        this.processorMap = processorMap;
        this.elementUtils = elementUtils;

        TypeSpec generatedClass = generateClassData();

        JavaFile javaFile = JavaFile.builder(MAPPING_PACKAGE, generatedClass).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MethodSpec generateMethod() {

        String fragName;
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
                .addParameter(Object.class,"fragment")
                .addModifiers(Modifier.PUBLIC);
        for(FragModuleContainer fragModule : processorMap.values()) {
            fragName = getPackageName(fragModule.getTypeElement(), elementUtils)+"."
                                        +fragModule.getTypeElement().getSimpleName();
            methodBuilder.beginControlFlow("if ($N.class.getSimpleName()." +
                                            "compareTo(fragment.getClass().getSimpleName())==0)",fragName);
            methodBuilder.addStatement("$NBuilder.inject(($N) fragment)",fragName,fragName);
            methodBuilder.addStatement("return");
            methodBuilder.endControlFlow();
        }
        return methodBuilder.build();
    }

    private TypeSpec generateClassData() {
        return TypeSpec.classBuilder(MAPPING_CLASS)
                .addSuperinterface(Injector.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateMethod())
                .build();
    }

    private String getPackageName(TypeElement element, Elements elementsUtils) {
        return elementsUtils.getPackageOf(element).toString();
    }
}
