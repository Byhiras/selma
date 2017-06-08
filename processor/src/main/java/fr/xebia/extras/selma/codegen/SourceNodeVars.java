/*
 * Copyright 2013 Xebia and Séven Le Mesle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package fr.xebia.extras.selma.codegen;

/**
 * Created with IntelliJ IDEA.
 * User: slemesle
 * Date: 20/11/2013
 * Time: 02:10
 * To change this template use File | Settings | File Templates.
 */
public class SourceNodeVars {

    String outFieldGetter;
    String field;

    BeanWrapper inBean;

    BeanWrapper outBean;

    String inField;
    String outField;

    InOutType inOutType;

    boolean assign = false;
    boolean useGetterForDestination = false;
    private String inFieldPrefix;
    private byte ptr = 'a';

    public SourceNodeVars(String inF, String outF, BeanWrapper inBean, BeanWrapper outBean) {
        this.field = inF;
        this.inBean = inBean;
        this.outBean = outBean;
        inField = (inBean == null ? "in" : inBean.getInGetterFor(inF));
        outField = (outBean == null ? "out" : outBean.getOutSetterPathFor(outF));
        outFieldGetter = (outBean == null ? "out" : outBean.getOutGetterPathFor(outF));
        inFieldPrefix = "";
    }

    public SourceNodeVars(String inF, String outF, BeanWrapper outBean) {
        this.field = inF;
        this.inBean = null;
        this.outBean = null;
        inField = inF;
        outField = (outBean == null ? "out" : outBean.getOutSetterPathFor(outF));
        outFieldGetter = (outBean == null ? "out" : outBean.getOutGetterPathFor(outF));
        inFieldPrefix = "";
    }


    public SourceNodeVars(String inF, String outF) {
        this.field = inF;
        this.inBean = null;
        this.outBean = null;
        inField = inF;
        outField = outF;
        outFieldGetter = "out";
        inFieldPrefix = "";
    }


    public SourceNodeVars() {
        this.field = null;
        this.inBean = null;
        this.outBean = null;
        inField = "in";
        outField = "out";
        outFieldGetter = "out";
        inFieldPrefix = "";
    }


    public SourceNodeVars withInField(String _inField) {
        inField = _inField;
        return this;
    }

    public SourceNodeVars withOutField(String _outField) {
        outField = _outField;
        return this;
    }

    public SourceNodeVars withInOutType(InOutType inOutType) {
        this.inOutType = inOutType;
        return this;
    }

    public SourceNodeVars withAssign(boolean assign) {
        this.assign = assign;
        return this;
    }

    public String inGetter() {
        return inField;
    }

    public String outSetterPath() {
        return outField;
    }

    public MappingSourceNode setOrAssign(String value) {

        String formattedValue = inFieldPrefix + String.format(value, inGetter());

        return (assign ? MappingSourceNode.assign(outField, formattedValue) : MappingSourceNode.set(outField, formattedValue));
    }

    public MappingSourceNode setOrAssignWithOutPut(String value) {

        String formattedValue = inFieldPrefix + String.format(value, inGetter(), outFieldGetter +
                ( outFieldGetter.contains(".") ? "()" : ""));

        return (assign ? MappingSourceNode.assign(outField, formattedValue) : MappingSourceNode.set(outField, formattedValue));
    }

    public String itemVar() {
        return String.format("%s%sItem", (char) ptr, (field == null ? "out" : field.replaceAll("\\.|\\(\\)", "")));
    }


    public String itemEntry() {
        return String.format("%s%sEntry", (char) ptr, (field == null ? "out" : field.replaceAll("\\.|\\(\\)", "")));
    }

    public String indexVar() {
        return indexVar((char) ptr);
    }

    public String indexVar(char indexChar) {
        return String.format("%c%sIndex", indexChar, (field == null ? "out" : field.replaceAll("\\.|\\(\\)", "")));
    }

    public String tmpVar(String suffix) {
        return String.format("%s%sTmp%s", (char) ptr, (field == null ? "out" : field.replaceAll("\\.|\\(\\)", "")), suffix);
    }

    public String totalCountVar() {
        return String.format("%s%sTotalCount", (char) ptr, (field == null ? "out" : field.replaceAll("\\.|\\(\\)", "")));
    }

    public SourceNodeVars withInFieldPrefix(String inFieldPrefix) {
        this.inFieldPrefix = inFieldPrefix;
        return this;
    }

    public boolean isOutPrimitive() {
        return inOutType.outIsPrimitive();
    }

    public boolean isInPrimitive() {
        return inOutType.inIsPrimitive();
    }

    public char nextPtr() {
        return (char) (ptr + 1);
    }

    public SourceNodeVars withIndexPtr(char _ptr) {
        this.ptr = (byte) _ptr;
        return this;
    }

    public SourceNodeVars withUseGetterForDestination(boolean useGetterForDestination) {
        this.useGetterForDestination = useGetterForDestination;
        return this;
    }

    public String outGetter() {
        return  outFieldGetter + ( outFieldGetter.contains(".") ? "()" : "");
    }
}
