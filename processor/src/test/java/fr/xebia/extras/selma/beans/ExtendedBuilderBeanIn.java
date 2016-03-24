package fr.xebia.extras.selma.beans;

/**
 * Bean used to test Builder-style properties.
 */
public class ExtendedBuilderBeanIn extends BuilderBeanIn {
    private EnumA enumVal;

    public EnumA getEnumVal() {
        return enumVal;
    }

    public void setEnumVal(EnumA enumVal) {
        this.enumVal = enumVal;
    }
}
