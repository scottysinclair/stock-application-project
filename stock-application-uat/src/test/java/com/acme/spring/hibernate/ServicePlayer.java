package com.acme.spring.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.thoughtworks.xstream.XStream;

/*
 * executes the method calls recorded by the service recorder.
 */
public class ServicePlayer implements ApplicationContextAware {

  private static final Logger LOG = LoggerFactory.getLogger(ServicePlayer.class);

  private ApplicationContext ctx;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.ctx = applicationContext;
  }

  public void playTestCase(String xmlContent) {
    XStream xstream = new XStream();
    xstream.setClassLoader(this.getClass().getClassLoader());
    MethodCalls methodCalls = (MethodCalls)xstream.fromXML(xmlContent);
    for (MethodCall mc: methodCalls.getCalls()) {
      LOG.debug("Invoking service method {}.{}", mc.getClassName(), mc.getMethodString());
      invoke( mc );
    }
  }


  private void invoke(MethodCall mc) {
    try {
      Class<?> methodClass = getClass().getClassLoader().loadClass( mc.getClassName() );
      Object bean = ctx.getBean( methodClass);
      Method method = findMethod(mc, methodClass);
      method.invoke(bean, mc.getArguments());
    }
    catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException x) {
      throw new IllegalStateException("Could not invoke method call " + mc, x);
    }
  }

  private Method findMethod(MethodCall mc, Class<?> classDeclaringMethod) throws NoSuchMethodException {
    for (Method method: classDeclaringMethod.getMethods()) {
      if (mc.getMethodString().equals( method.toGenericString() )) {
        return method;
      }
    }
    throw new NoSuchMethodException("Could not find method matching " + mc);
  }

}
