package com.dilpreet2028.fragmenter_compiler;

import com.dilpreet2028.fragmenter_annotations.annotations.Arg;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by dilpreet on 11/3/17.
 */

public class FragModuleContainer {

    private TypeElement typeElement;

    public FragModuleContainer(TypeElement typeElement) {
        this.typeElement=typeElement;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public List<Element> getElements()  {
        List<Element> elementList=new ArrayList<>();


        for(Element element:typeElement.getEnclosedElements()){
            // If Arg annotation is present then add it to the list.
            if ((element.getAnnotation(Arg.class)) != null) {
                elementList.add(element);
            }
        }
        return elementList;
    }
}
