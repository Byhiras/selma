package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class BuilderBeanOut2 {
    private final String str;
    private final int intVal;

    BuilderBeanOut2(int intVal, String str) {
        this.intVal = intVal;
        this.str = str;
    }

    public int getIntVal() {
        return intVal;
    }

    public String getStr() {
        return str;
    }

    /**
     * Abstract class used for various test builders.
     */
    static abstract class AbstractBuilder {
        private int intVal;
        private String str;

        public int getIntVal() {
            return intVal;
        }

        protected void doSetIntVal(int intVal) {
            this.intVal = intVal;
        }

        public String getStr() {
            return str;
        }

        protected void doSetStr(String str) {
            this.str = str;
        }
    }

    public static class OtherBuilder extends AbstractBuilder {
        public OtherBuilder setIntVal(int intVal) {
            doSetIntVal(intVal);
            return this;
        }

        public OtherBuilder setStr(String str) {
            doSetStr(str + " bean2");
            return this;
        }

        public BuilderBeanOut2 build() {
            return new BuilderBeanOut2(getIntVal(), getStr());
        }
    }

    public static class FixedIntValBuilder extends AbstractBuilder {
        public static final int FIXED_VALUE = 12345;

        public FixedIntValBuilder setIntVal(int intVal) {
            doSetIntVal(intVal);
            return this;
        }

        public FixedIntValBuilder setStr(String str) {
            doSetStr(str);
            return this;
        }

        public BuilderBeanOut2 build() {
            return new BuilderBeanOut2(FIXED_VALUE, getStr());
        }
    }

    public static class FieldsBuilder extends AbstractBuilder {
        public FieldsBuilder setIntVal(int intVal) {
            doSetIntVal(intVal);
            return this;
        }

        public FieldsBuilder setString(String str) {
            doSetStr(str + " other field");
            return this;
        }

        public BuilderBeanOut2 build() {
            return new BuilderBeanOut2(getIntVal(), getStr());
        }
    }

    public static class Maker extends AbstractBuilder {
        public Maker setIntVal(int intVal) {
            doSetIntVal(intVal);
            return this;
        }

        public Maker setStr(String str) {
            doSetStr(str + " made");
            return this;
        }

        public BuilderBeanOut2 make() {
            return new BuilderBeanOut2(getIntVal(), getStr());
        }
    }
}
