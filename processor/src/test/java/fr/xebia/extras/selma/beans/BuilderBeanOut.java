package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class BuilderBeanOut {
    private final String str;
    private final int intVal;

    BuilderBeanOut(int intVal, String str) {
        this.intVal = intVal;
        this.str = str;
    }

    public int getIntVal() {
        return intVal;
    }

    public String getStr() {
        return str;
    }

    public static class Builder extends AbstractBuilderOut<Builder> {
        private int intVal;

        public int getIntVal() {
            return intVal;
        }

        public Builder setIntVal(int intVal) {
            this.intVal = intVal;
            return this;
        }

        public BuilderBeanOut build() {
            return new BuilderBeanOut(intVal, getStr());
        }
    }

    public static FactoryBuilder createFactoryBuilder() {
        return new FactoryBuilder();
    }

    public static class FactoryBuilder extends Builder {
        private FactoryBuilder() {
        }

        @Override
        public FactoryBuilder setIntVal(int intVal) {
            super.setIntVal(intVal);
            return this;
        }

        @Override
        public FactoryBuilder setStr(String str) {
            super.setStr(str);
            return this;
        }

        public BuilderBeanOut build() {
            return new BuilderBeanOut(getIntVal() + 1, getStr() + "factory");
        }
    }
}
