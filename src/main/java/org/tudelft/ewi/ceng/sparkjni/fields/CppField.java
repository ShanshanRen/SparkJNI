/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tudelft.ewi.ceng.sparkjni.fields;

import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

import java.lang.reflect.Field;

/**
 * Created by root on 8/16/16.
 */
public abstract class CppField {
    String name;
    String readableType;
    String type;
    String fieldDeclaration;
    boolean translatedField = false;
    String nativeTargetType = null;
    String ownerClassName;
    Field javaField = null;
    String arrayTypeJniName = null;
    int accessModifier;
    boolean validNativeMapper = false;

    CppField(){}

    /**
     * Constructor designed to be used for creating variables
     * that tell the length of the @field array.
     * @param cppType
     * @param fieldName
     * @param ownerClassName
     * @param ownerClassName
     */
    public CppField(String cppType, String fieldName, String ownerClassName){
        this.readableType = cppType;
        this.name = fieldName;
        this.type = cppType;
        this.ownerClassName = ownerClassName;
        fieldDeclaration = String.format(JniUtils.FIELD_DECLARATION_STR, type, "%s"+name);
    }

    public CppField(Field field){
        javaField = field;
        this.readableType = javaField.getType().getSimpleName();
        ownerClassName = javaField.getDeclaringClass().getSimpleName();
        accessModifier = javaField.getModifiers();
        this.name = javaField.getName();
        translatedField = true;
    }

    public boolean hadValidNativeMapper(){
        return validNativeMapper;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public String getName() {
        return name;
    }

    public String getReadableType() {
        return readableType;
    }

    public String getFieldDeclarationStmt(){
        return String.format(fieldDeclaration,"","");
    }

    public Field getJavaField(){
        return javaField;
    }

    public String getNativeType() {
        if(nativeTargetType == null)
            return type;
        else
            return nativeTargetType;
    }

    public String getFieldDeclarationCppStmt(String className){
        return String.format(fieldDeclaration, className+"::", className+"::");
    }
    public boolean isPrimitive(){
        if(javaField == null)
            return true;
        else
            return javaField.getType().isPrimitive();
    }

    public boolean isArray(){
        if(javaField == null)
            return false;
        else
            return javaField.getType().isArray();
    }

    public String getTypeSignature(){
        if(javaField == null)
            return "I";
        else return JniUtils.getSignatureForType(javaField.getType());
    }
}
