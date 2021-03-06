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
package org.tudelft.ewi.ceng.sparkjni.methods;

import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

/**
 * Created by root on 8/16/16.
 */
public class EmptyCppConstructor extends NativeMethod{
    public EmptyCppConstructor(CppClass cppClass) {
        super(cppClass);
    }

    public EmptyCppConstructor() {
    }

    public String generateNonArgConstructorPrototype(){
        return String.format(JniUtils.CONSTRUCTOR_WITH_NATIVE_ARGS_PROTOTYPE_STR,
                ownerClass.getCppClassName(), "");
    }

    public String generateNonArgConstructorImpl(){
        return String.format(JniUtils.CONSTRUCTOR_WITH_NATIVE_ARGS_IMPL_STR,
                ownerClassName, ownerClassName, "", "");
    }
}