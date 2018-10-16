package br.com.anteros.nosql.persistence.proxy;

import br.com.anteros.core.log.LogLevel;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;

public final class LazyFeatureDependencies {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(LazyFeatureDependencies.class);
    private static Boolean fulFilled;

    private LazyFeatureDependencies() {
    }

 
    public static boolean assertDependencyFullFilled() {
        final boolean fulfilled = testDependencyFullFilled();
        if (!fulfilled) {
            LOG.log(LogLevel.WARN,"Lazy loading impossible due to missing dependencies.");
        }
        return fulfilled;
    }

    public static boolean testDependencyFullFilled() {
        if (fulFilled != null) {
            return fulFilled;
        }
        try {
            fulFilled = Class.forName("net.sf.cglib.proxy.Enhancer") != null
                        && Class.forName("com.thoughtworks.proxy.toys.hotswap.HotSwapping")
                           != null;
        } catch (ClassNotFoundException e) {
            fulFilled = false;
        }
        return fulFilled;
    }


    public static LazyProxyFactory createDefaultProxyFactory() {
        if (testDependencyFullFilled()) {
            final String factoryClassName = "br.com.anteros.nosql.persistence.proxy.CGLibLazyProxyFactory";
            try {
                return (LazyProxyFactory) Class.forName(factoryClassName).newInstance();
            } catch (Exception e) {
                LOG.error("While instantiating " + factoryClassName, e);
            }
        }
        return null;
    }
}
