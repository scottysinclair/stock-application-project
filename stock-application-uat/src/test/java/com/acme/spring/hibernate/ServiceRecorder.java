package com.acme.spring.hibernate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.Ordered;

import com.thoughtworks.xstream.XStream;

public class ServiceRecorder implements MethodBeforeAdvice, Ordered {

  @SuppressWarnings("unused")
  private int order;
  private String rootFolder;

  public ServiceRecorder(int order, String rootFolder) {
    this.order = order;
    this.rootFolder = rootFolder;
  }

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public void before(Method method, Object[] args, Object target) throws Throwable {
    try {
      record(method, args, target);
    }
    catch(Exception x) {
      System.err.println("Could not record service call " + x.getMessage());
    }
  }


  private void record(Method method, Object[] args, Object target) throws IOException {
    MethodCall mc = new MethodCall();
    mc.setClassName( method.getDeclaringClass().getName() );
    mc.setMethodName(method.getName());
    mc.setMethodString(method.toGenericString());
    mc.setArguments(args);

    appendToFile( convertToXml(mc) );
  }

  private String convertToXml(MethodCall mc) {
    XStream xstream = new XStream();
    return xstream.toXML(mc);
  }

  private void appendToFile(String data) throws IOException {
    File file = new File(rootFolder + "/test_case_calls.xml");
    FileWriter out = new FileWriter(file, true);
    out.write(data);
    out.flush();
    out.close();
  }


  // @Override
  // public Object invoke(MethodInvocation invocation) throws Throwable {
  // try {
  // System.out.println("\n\nBEFORE");
  // Object o = invocation.proceed();
  // System.out.println("\n\nAFTER");
  // return o;
  // }
  // catch(Throwable t) {
  // System.out.println("\n\nERROR");
  // throw t;
  // }
  // }

}
