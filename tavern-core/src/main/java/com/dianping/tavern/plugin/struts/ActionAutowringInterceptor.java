package com.dianping.tavern.plugin.struts;

import com.dianping.tavern.Tavern;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.spring.SpringObjectFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author yihua.huang@dianping.com
 */
public class ActionAutowringInterceptor extends AbstractInterceptor {

	public static final String APPLICATION_CONTEXT = "com.opensymphony.xwork2.spring.interceptor.ActionAutowiringInterceptor.applicationContext";

	private boolean initialized = false;
	private ApplicationContext context;
	private SpringObjectFactory factory;
	private Integer autowireStrategy;

	/**
	 * @param autowireStrategy
	 */
	public void setAutowireStrategy(Integer autowireStrategy) {
		this.autowireStrategy = autowireStrategy;
	}

	/**
	 * Looks for the <code>ApplicationContext</code> under the attribute that
	 * the Spring listener sets in the servlet context. The configuration is
	 * done the first time here instead of in init() since the
	 * <code>ActionContext</code> is not available during
	 * <code>Interceptor</code> initialization.
	 * <p/>
	 * Autowires the action to Spring beans and places the
	 * <code>ApplicationContext</code> on the <code>ActionContext</code>
	 * <p/>
	 * TODO Should this check to see if the <code>SpringObjectFactory</code> has
	 * already been configured instead of instantiating a new one? Or is there a
	 * good reason for the interceptor to have it's own factory?
	 * 
	 * @param invocation
	 * @throws Exception
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (!initialized) {
			ApplicationContext applicationContext = Tavern.getCurrentApplication(invocation.getAction().getClass())
					.getApplicationContext();
			if (applicationContext == null) {
			} else {
				setApplicationContext(applicationContext);
				factory = new SpringObjectFactory();
				factory.setApplicationContext(getApplicationContext());
				if (autowireStrategy != null) {
					factory.setAutowireStrategy(autowireStrategy.intValue());
				}
			}
			initialized = true;
		}

		if (factory != null) {
			Object bean = invocation.getAction();
			factory.autoWireBean(bean);

			ActionContext.getContext().put(APPLICATION_CONTEXT, context);
		}
		return invocation.invoke();
	}

	/**
	 * @param applicationContext
	 * @throws org.springframework.beans.BeansException
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	/**
	 * @return context
	 */
	protected ApplicationContext getApplicationContext() {
		return context;
	}
}
