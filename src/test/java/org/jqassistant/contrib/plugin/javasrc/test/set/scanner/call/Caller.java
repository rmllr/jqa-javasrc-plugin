package org.jqassistant.contrib.plugin.javasrc.test.set.scanner.call;

public class Caller {

    public Caller() {
        this.calledMethod3();
    }

    public void callingMethod() {
        this.calledMethod0();
        this.calledMethod1();
        Callee callee = new Callee();
        callee.calledMethod2();
    }

    public void calledMethod0() {
    }

    public void calledMethod1() {
    }

    public void calledMethod3() {
    }

}
