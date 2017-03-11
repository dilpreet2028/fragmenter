package com.dilpreet2028.fragmenter_compiler;

import javax.lang.model.element.Element;

/**
 * Created by dilpreet on 11/3/17.
 */

public class ProcessorException extends Exception {
    private String msg;
    private Object[] args;
    private Element element;

    public ProcessorException(Element element, String msg, Object...  args) {
        this.msg=msg;
        this.args=args;
        this.element=element;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String getMessage() {
        return String.format(msg,args);

    }
}

