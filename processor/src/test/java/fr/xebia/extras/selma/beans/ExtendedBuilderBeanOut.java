package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class ExtendedBuilderBeanOut extends BuilderBeanOut {
    public ExtendedBuilderBeanOut(int intVal, String str) {
        super(intVal, str);
    }

    public static class Builder extends BuilderBeanOut.Builder {
        @Override
        public Builder setIntVal(int intVal) {
            super.setIntVal(intVal);
            return this;
        }

        @Override
        public Builder setStr(String str) {
            super.setStr(str);
            return this;
        }

        public ExtendedBuilderBeanOut build() {
            return new ExtendedBuilderBeanOut(getIntVal(), getStr());
        }
    }
}
