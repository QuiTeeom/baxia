package com.quitee.baxia.exceptions;

public class NotASpaceClass extends RuntimeException {
    Class aClass;

    public NotASpaceClass(Class aClass) {
        super("the class does not have a Space annotation:"+aClass);
        this.aClass = aClass;
    }
}
