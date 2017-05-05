package com.acme.spring.hibernate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.Ordered;

import com.thoughtworks.xstream.XStream;

public class ServiceRecorder implements MethodBeforeAdvice, Ordered {

  @SuppressWarnings("unused")
  private int order;

  public ServiceRecorder(int order) {
    this.order = order;
  }

  @PostConstruct
  public void init() {

  }

  @PreDestroy
  public void destroy() {

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
    mc.setClassName(method.getDeclaringClass().getName());
    mc.setMethodName(method.getName());
    mc.setArguments(args);

    appendToFile( convertToXml(mc) );

    System.out.println("\n\nBEFORE");
  }

  private String convertToXml(MethodCall mc) {
    XStream xstream = new XStream();
    return xstream.toXML(mc);
  }

  private void appendToFile(String data) throws IOException {
    File file = new File("/tmp/test_case_calls.xml");
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
