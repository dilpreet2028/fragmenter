package com.dilpreet2028.fragmenter_compiler;

import com.dilpreet2028.fragmenter_annotations.annotations.FragModule;
import com.dilpreet2028.fragmenter_compiler.FileGenerator.FieldInjectorGenerator;
import com.dilpreet2028.fragmenter_compiler.FileGenerator.FragGenerator;
import com.google.auto.service.AutoService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedAnnotationTypes("com.dilpreet2028.fragmenter_annotations.annotations.FragModule")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FragmenterProcessor extends AbstractProcessor {

    private Map<String,FragModuleContainer> processorMap;
    private Types typeUtil;
    private Filer filer;
    private Messager messager;
    private Elements elements;
    private FragGenerator fragGenerator;
    private FieldInjectorGenerator injectorGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtil = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        processorMap = new LinkedHashMap<>();
        fragGenerator = new FragGenerator();
        injectorGenerator = new FieldInjectorGenerator();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for(Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(FragModule.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                onError(annotatedElement,"%s does not appears to be a class " ,
                        annotatedElement.getSimpleName().toString());
                return false;
            }

            FragModuleContainer fragModuleContainer=new
                    FragModuleContainer( (TypeElement) annotatedElement);

            processorMap.put(fragModuleContainer.getTypeElement().getSimpleName().toString(),
                            fragModuleContainer);
        }


        fragGenerator.generateClass(processorMap , filer , elements);
        injectorGenerator.generateClass(processorMap , filer , elements);
        return true;
    }

    private void onError(Element e,String msg,Object... args){
        messager.printMessage(Diagnostic.Kind.ERROR,String.format(msg,args),e);
    }

    private void onPrompt(Element e,String msg,Object... args){
        messager.printMessage(Diagnostic.Kind.NOTE,String.format(msg,args),e);
    }
}
