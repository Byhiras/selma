package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class BuilderBeanOut3 {
    private final String str;
    private final int intVal;

    BuilderBeanOut3(int intVal, String str) {
        this.intVal = intVal;
        this.str = str;
    }

    public int getIntVal() {
        return intVal;
    }

    public String getStr() {
        return str;
    }

    public static class OtherBuilder {
        private int intVal;
        private String str;

        public OtherBuilder setIntVal(int intVal) {
            this.intVal = intVal;
            return this;
        }

        public OtherBuilder setStr(String str) {
            this.str = str + " bean3";
            return this;
        }

        public BuilderBeanOut3 build() {
            return new BuilderBeanOut3(intVal, str);
        }
    }
}
