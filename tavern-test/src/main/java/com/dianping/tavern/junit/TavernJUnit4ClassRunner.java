package com.dianping.tavern.junit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yihua.huang@dianping.com
 *
 * TODO: 暂不可用
 */
public class TavernJUnit4ClassRunner extends JUnit4ClassRunner {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TavernJUnit4ClassRunner.class);

    public TavernJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }


    @Override
    /**
     * Check whether the test is enabled in the first place. This prevents classes with
     * a non-matching <code>@IfProfileValue</code> annotation from running altogether,
     * even skipping the execution of <code>prepareTestInstance</code> listener methods.
     * @see org.springframework.test.annotation.IfProfileValue
     * @see org.springframework.test.context.TestExecutionListener
     */
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    /**
     * Delegates to {@link JUnit4ClassRunner#createTest()} to create the test
     * instance and then to a {@link TestContextManager} to
     * {@link TestContextManager#prepareTestInstance(Object) prepare} the test
     * instance for Spring testing functionality.
     * @see JUnit4ClassRunner#createTest()
     * @see TestContextManager#prepareTestInstance(Object)
     */
    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        return testInstance;
    }


    /**
     * Invokes the supplied {@link java.lang.reflect.Method test method} and notifies the supplied
     * {@link RunNotifier} of the appropriate events.
     * @see #createTest()
     * @see JUnit4ClassRunner#invokeTestMethod(java.lang.reflect.Method,RunNotifier)
     */
    @Override
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking test method [" + method.toGenericString() + "]");
        }

        // The following is a 1-to-1 copy of the original JUnit 4.4 code, except
        // that we use custom implementations for TestMethod and MethodRoadie.

        Description description = methodDescription(method);
        Object testInstance;
        try {
            testInstance = createTest();
        }
        catch (InvocationTargetException ex) {
            notifier.testAborted(description, ex.getCause());
            return;
        }
        catch (Exception ex) {
            notifier.testAborted(description, ex);
            return;
        }

    }
}
