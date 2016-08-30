package org.tudelft.ewi.ceng.methods;

import org.tudelft.ewi.ceng.CppClass;
import org.tudelft.ewi.ceng.JniUtils;
import org.tudelft.ewi.ceng.fields.CppField;
import org.tudelft.ewi.ceng.fields.CppRawTypeField;
import org.tudelft.ewi.ceng.fields.CppReferenceField;

/**
 * Created by root on 8/16/16.
 */
public class JniConstructor extends NativeMethod {
    public static final String MUTEX_CPP_FIELD_NAME = "mtx";
    public JniConstructor(CppClass cppClass) {
        super(cppClass);
    }

    public JniConstructor() {
    }
    // Include it in constructor
    // Here we have unique indentation
    public String generateConstructorImpl() {
        StringBuilder sb = new StringBuilder();
        sb.append(ownerClassName+"::"+ownerClassName);
        sb.append("(" + JniUtils.JNI_CLASS + " replaceMeClassName, " + JniUtils.JNI_OBJECT + " replaceMeObjectName, JNIEnv* " + "env" + "){\n");

        sb.append(JniUtils.CLASS_REF_ASSIGN_STR);

        /*
        Critical section - synchronized for all instances.
        Here we serialize access to the critical arrays sections from Java.
         */
        sb.append(objectAssignmentSection());
        sb.append("\n");
        sb.append("\t");
        sb.append(MUTEX_CPP_FIELD_NAME);
        sb.append(".lock();\n");

        sb.append(arrayAssignmentSection());

        sb.append("\t");
        sb.append(MUTEX_CPP_FIELD_NAME);
        sb.append(".unlock();\n");

        sb.append("\tthis->env = env;\n");
        sb.append("\tjniCreated = 1;\n");
        sb.append("}");
        return sb.toString();
    }

    private String arrayAssignmentSection() {
        StringBuilder sb = new StringBuilder();
        for (CppField field : ownerClass.getCppFields()) {
            if (field instanceof CppRawTypeField) {
                CppRawTypeField cppRawTypeField = (CppRawTypeField) field;
                if (cppRawTypeField.isArray()) {
                    String jniArrFieldName = field.getName() + "Arr";
                    sb.append(String.format(JniUtils.REINTERPRET_OBJ_CAST_STR,
                            jniArrFieldName,
                            JniUtils.getArrayTypeDecl(field.getReadableType()),
                            field.getName()));

                    sb.append(String.format(JniUtils.ENV_GET_ARRAY_LENGTH_STR,
                            field.getName() + "_length", field.getName() + "Arr"));

                    if (cppRawTypeField.isMemoryAligned()) {
                        if (cppRawTypeField.isSafe()) {
                            sb.append(String.format("\t%s temp%s = env->Get%sArrayElements(%s, NULL);\n",
                                    cppRawTypeField.getTypeOfArrayElement().toLowerCase() + "*", cppRawTypeField.getName(), cppRawTypeField.getTypeOfArrayElement(), jniArrFieldName));
                            sb.append(String.format("\tif(posix_memalign((void **)&%s, %d, sizeof(%s) *  %s_length)) " +
                                            "{\n\t\tperror(\"Could not allocate memory..\\n\");\n" +
                                            "\t\treturn;\n" +
                                            "\t}\n",
                                    field.getName(), cppRawTypeField.getMemoryAlignment(), cppRawTypeField.getTypeOfArrayElement().toLowerCase(), field.getName()));

                            sb.append(String.format("\tmemcpy(%s, temp%s, sizeof(%s) * %s_length);\n",
                                    field.getName(), field.getName(), cppRawTypeField.getTypeOfArrayElement().toLowerCase(), cppRawTypeField.getName()));
                        }
                    } else {
                        String typeCast = String.format("(%s)", field.getNativeType());
                        sb.append(String.format(JniUtils.GET_ARRAY_ELEMENTS_STR,
                                cppRawTypeField.getName(), typeCast, cppRawTypeField.getTypeOfArrayElement(), jniArrFieldName));
                    }
                }
            }
        }

        return sb.toString();
    }

    private String objectAssignmentSection(){
        StringBuilder sb = new StringBuilder();
        // Use annotations to select which fields are to be used
        // in native
        for (CppField field : ownerClass.getCppFields()) {
            if(field instanceof CppRawTypeField) {
                CppRawTypeField cppRawTypeField = (CppRawTypeField) field;
                if (field.isPrimitive() || (field.isArray() && cppRawTypeField.isPrimitiveArray())) {
                    if (!cppRawTypeField.isTranslatedField())
                        continue;

                    sb.append(String.format(JniUtils.GET_FIELD_ID_STMT_STR,
                            cppRawTypeField.getName(), "replaceMeClassName", cppRawTypeField.getName(),
                            cppRawTypeField.getTypeSignature()));

                    String formatString = (cppRawTypeField.isArray()) ? "jobject %s_obj" : "%s";
                    sb.append(String.format(formatString, cppRawTypeField.getName()));
                    sb.append(String.format(JniUtils.GET_FIELD_STMT_STR,
                            "env", cppRawTypeField.getJniTypePlaceholderName(),
                            "replaceMeObjectName", cppRawTypeField.getName()));
                }
            } else {
                CppReferenceField cppReferenceField = (CppReferenceField) field;
                sb.append(String.format(JniUtils.GET_FIELD_ID_STMT_STR,
                        cppReferenceField.getName(), "replaceMeClassName", cppReferenceField.getName(),
                        cppReferenceField.getTypeSignature()));
                sb.append(String.format(JniUtils.NULL_PTR_CHECK_STR,
                        "j_"+cppReferenceField.getName(), String.format("FieldID object j_%s is null..",
                                cppReferenceField.getName())));
                sb.append(String.format("jobject %s_obj = %s->Get%sField(%s, j_%s);\n",
                        cppReferenceField.getName(), "env", "Object",
                        "replaceMeObjectName", cppReferenceField.getName()));
                sb.append(String.format("\tjclass %sClass = env->GetObjectClass(%s_obj);\n",
                        cppReferenceField.getName(), cppReferenceField.getName()));
                sb.append(String.format("\t%s = new %s(%sClass, %s_obj, %s);\n",
                        cppReferenceField.getName(), cppReferenceField.getReadableType(),
                        cppReferenceField.getName(), cppReferenceField.getName(), "env"));
            }
        }
        return sb.toString();
    }
}
