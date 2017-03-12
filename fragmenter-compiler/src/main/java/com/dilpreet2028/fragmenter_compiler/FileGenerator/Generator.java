package com.dilpreet2028.fragmenter_compiler.FileGenerator;

import com.dilpreet2028.fragmenter_compiler.FragModuleContainer;

import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

/**
 * Created by dilpreet on 12/3/17.
 */

public interface Generator {
    public void generateClass(Map<String,FragModuleContainer> processorMap ,
                             Filer filer , Elements elementUtils);
}
