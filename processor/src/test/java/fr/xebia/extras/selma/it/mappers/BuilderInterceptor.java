package fr.xebia.extras.selma.it.mappers;

import fr.xebia.extras.selma.beans.BuilderBeanIn;
import fr.xebia.extras.selma.beans.BuilderBeanOut;

/**
 * @author Christopher Ng
 */
public class BuilderInterceptor {
    /**
     * Should be unused.
     */
    public void builderBeanInterceptor(BuilderBeanIn in, BuilderBeanOut out) {
        throw new UnsupportedOperationException("Should not be used");
    }

    public void builderBeanBuilderInterceptor(BuilderBeanIn in, BuilderBeanOut.Builder out) {
        out.setStr(out.getStr() + " intercepted");
    }
}
