package org.tudelft.ewi.ceng.examples.pairHMM;

import org.apache.spark.util.SerializableBuffer;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by root on 8/17/16.
 */
public class RawBufferBean extends Bean implements Serializable {
    public int capacity = 0;
    public int alignment = 0;
    public long address = 0L;
    public SerializableBuffer buffer;
    public byte[] bytes;

    private RawBufferBean(){}

    public byte[] getBytes(){
        return bytes;
    }

    public RawBufferBean(byte[] bytes){
        this.bytes = bytes;
    }

    public ByteBuffer getByteBuffer(){
        return buffer.buffer();
    }

    public byte[] getByteArray(){
        byte[] ret = new byte[buffer.buffer().limit()];
        buffer.buffer().get(ret);
        buffer.buffer().flip();
        return ret;
    }
}
