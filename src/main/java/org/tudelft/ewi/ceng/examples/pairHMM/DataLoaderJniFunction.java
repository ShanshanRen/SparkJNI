package org.tudelft.ewi.ceng.examples.pairHMM;
import org.tudelft.ewi.ceng.sparkjni.jniFunctions.JniMapFunction;

/**
 * Created by root on 8/17/16.
 */
public class DataLoaderJniFunction extends JniMapFunction{
    public DataLoaderJniFunction(){}
    public DataLoaderJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native ByteArrBean callDataLoader(WorkloadPairHmmBean bean);
}
