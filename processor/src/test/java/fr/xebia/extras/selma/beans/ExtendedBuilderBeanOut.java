package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class ExtendedBuilderBeanOut extends BuilderBeanOut {
    private final EnumB enumVal;

    private ExtendedBuilderBeanOut(int intVal, String str, EnumB enumVal) {
        super(intVal, str);
        this.enumVal = enumVal;
    }

    public EnumB getEnumVal() {
        return enumVal;
    }

    public static class Builder extends BuilderBeanOut.Builder {
        private EnumB enumVal;

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

        public Builder setEnumVal(EnumB enumVal) {
            this.enumVal = enumVal;
            return this;
        }

        public ExtendedBuilderBeanOut build() {
            return new ExtendedBuilderBeanOut(getIntVal(), getStr(), enumVal);
        }
    }
}
