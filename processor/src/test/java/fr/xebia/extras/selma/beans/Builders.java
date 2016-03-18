package fr.xebia.extras.selma.beans;

import fr.xebia.extras.selma.beans.BuilderBeanOut2.AbstractBuilder;

import java.util.Random;

public class Builders {
    public static final int EXTERNAL_INT_VALUE = new Random().nextInt();
    public static final String EXTERNAL_STR = "externalStr";

    public static class BuilderBeanOut2Builder extends AbstractBuilder {
        public BuilderBeanOut2Builder setIntVal(int intVal) {
            doSetIntVal(intVal);
            return this;
        }

        public BuilderBeanOut2Builder setStr(String str) {
            doSetStr(str);
            return this;
        }

        public BuilderBeanOut2 build() {
            return new BuilderBeanOut2(EXTERNAL_INT_VALUE, EXTERNAL_STR);
        }
    }
}
