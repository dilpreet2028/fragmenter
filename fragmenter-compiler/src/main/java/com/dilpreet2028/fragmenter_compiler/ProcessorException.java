package com.dilpreet2028.fragmenter_compiler;

import javax.lang.model.element.Element;

/**
 * Created by dilpreet on 11/3/17.
 */

public class ProcessorException extends Exception {
    private String msg;
    private Object[] args;
    public ProcessorException(String msg,Object...  args) {
        this.msg=msg;
        this.args=args;
    }

    @Override
    public String getMessage() {
        return String.format(msg,args);
    }
}

