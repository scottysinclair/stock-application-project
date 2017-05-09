package com.acme.spring.hibernate;

public class MethodCall {

  private String className;

  private String methodName;

  private String methodString;

  private Object arguments[];

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Object[] getArguments() {
    return arguments;
  }

  public void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }

  public void setMethodString(String methodString) {
    this.methodString = methodString;
  }

  public String getMethodString() {
    return methodString;
  }

}
