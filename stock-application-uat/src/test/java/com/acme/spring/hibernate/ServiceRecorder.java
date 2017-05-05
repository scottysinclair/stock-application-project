package com.acme.spring.hibernate;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.Ordered;

public class ServiceRecorder implements MethodBeforeAdvice, Ordered {

  @SuppressWarnings("unused")
  private int order;

  public ServiceRecorder(int order) {
    this.order = order;
  }

  @Override
  public int getOrder() {
    return 1;
  }

  public Object profile(ProceedingJoinPoint call) throws Throwable {
    System.out.println("\n\nBEFORE");
    Object o = call.proceed();
    System.out.println("\n\nAFTER");
    return o;
  }

  @Override
  public void before(Method method, Object[] args, Object target) throws Throwable {
    System.out.println("\n\nBEFORE");
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
