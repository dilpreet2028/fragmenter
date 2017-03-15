package com.dilpreet2028.fragmenter_compiler.FileGenerator;

import com.dilpreet2028.fragmenter_annotations.annotations.Arg;
import com.dilpreet2028.fragmenter_compiler.FragModuleContainer;
import com.dilpreet2028.fragmenter_compiler.FragmenterProcessor;
import com.dilpreet2028.fragmenter_compiler.ProcessorException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by dilpreet on 11/3/17.
 */

public class FragGenerator implements Generator {

    private final HashMap<String, Class> fieldMapper =
            new HashMap<>();
    private final HashMap<String, String> bundleListMapper =
            new HashMap<>();

    private String packageName;
    private ClassName bundleClass=ClassName.get("android.os","Bundle");
    private ClassName parcelableClass = ClassName.get("android.os.","Parcelable");

    public FragGenerator() {
        fieldMapper.put("int", Integer.class);
        fieldMapper.put("java.lang.Integer", Integer.class);
        fieldMapper.put("java.lang.String", String.class);
        fieldMapper.put("float", Float.class);
        fieldMapper.put("java.lang.Float", Float.class);
        fieldMapper.put("long", Long.class);
        fieldMapper.put("java.lang.Long", Long.class);
        fieldMapper.put("double", Double.class);
        fieldMapper.put("java.lang.Double", Double.class);
        fieldMapper.put("boolean", Boolean.class);
        fieldMapper.put("java.lang.Boolean", Boolean.class);
        fieldMapper.put("byte", Byte.class);
        fieldMapper.put("java.lang.Byte", Byte.class);
        fieldMapper.put("short", Short.class);
        fieldMapper.put("java.lang.Short", Short.class);
        fieldMapper.put("android.os.Parcelable", parcelableClass.getClass());
        fieldMapper.put("java.lang.CharSequence", CharSequence.class);

        bundleListMapper.put("String", "StringArrayList");
        bundleListMapper.put("Integer", "IntegerArrayList");
        bundleListMapper.put("CharSequence", "CharSequenceArrayList");
    }

    @Override
    public void generateClass(Map<String,FragModuleContainer> processorMap ,
                              Filer filer , Elements elementUtils) throws ProcessorException{



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

    /*
    * Generates newInstance() static function along with the parameters required
    * to be initialized and sets the arugments in the bundle
    */
    private MethodSpec generateStaticFunction(FragModuleContainer fragModule) throws ProcessorException {

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

    /*
    * Generates paramaters for newInstance static function
    */
    private List<ParameterSpec> generateFields(FragModuleContainer fragModule) {
        List<ParameterSpec> specList=new ArrayList<>();
        ParameterSpec parameterSpec;
        String name;

        for(Element element : fragModule.getElements()) {
            name=element.getSimpleName().toString();

            parameterSpec=ParameterSpec.builder(ClassName.get(element.asType()),name)
                            .build();
            specList.add(parameterSpec);
        }
        return specList;
    }


    private TypeSpec generateClassData(FragModuleContainer fragModule) throws ProcessorException {
        return TypeSpec.classBuilder(fragModule.getTypeElement().
                getSimpleName().toString() + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateStaticFunction(fragModule))
                .addMethod(generateInjectMethods(fragModule))
                .build();
    }

    /*
    * used for injecting arguments
    */
    private MethodSpec generateInjectMethods(FragModuleContainer fragModule) throws ProcessorException {
        ClassName fragmentName=ClassName.get(fragModule.getTypeElement());
        String name;
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
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

    /*
    *   Used for finding bundle function for a particular argument.
    */
    private String returnBundleFunc(Element element) throws ProcessorException{

        String elementTypeString = element.asType().toString();
        if(elementTypeString.contains("java.util.List")) {
            throw new ProcessorException(element, "List is not supported in bundle please use ArrayList for \"%s.\"",
                    element.getSimpleName());
        }

        if(!elementTypeString.contains(ArrayList.class.getSimpleName())) {
            return singleValueClass(element);
        } else {
            return multipleValueClass(element);
        }
    }

    /*
    * Used in case of a paramter with single value for e.g. Integer, String, etc.
    */
    private String singleValueClass(Element element) throws ProcessorException{
        String elementTypeString =  element.asType().toString();
        if(!fieldMapper.containsKey(elementTypeString))
            throw new ProcessorException(element, "%s is not supported right now ",
                    elementTypeString);

        if (elementTypeString.compareTo("int") == 0 ||
                elementTypeString.compareTo("java.lang.Integer") == 0)
            return "Int";
        else if (elementTypeString.compareTo("java.lang.Character") == 0)
            return "Char";
        else
            return fieldMapper.get(elementTypeString).getSimpleName();
    }

    /*
    * Used in case of a List item
    */
    private String multipleValueClass(Element element) throws ProcessorException {

        String elementTypeString =  element.asType().toString();

        Pattern pattern = Pattern.compile("java.lang.(.*?)>");
        Matcher matcher = pattern.matcher(elementTypeString);
        if(matcher.find()) {

            if (!bundleListMapper.containsKey(matcher.group(1))) {
                throw new ProcessorException(element,"%s type ArrayList is not supported in bundle. ",matcher.group(1));
            }
            return bundleListMapper.get(matcher.group(1));
        }

        return "";
    }



}
