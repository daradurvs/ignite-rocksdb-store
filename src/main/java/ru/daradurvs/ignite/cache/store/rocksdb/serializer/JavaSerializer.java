package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class JavaSerializer implements Serialiazer {
    private static final int BUFFER_SIZE = 4096;

    @Override public byte[] serialize(Object obj) {
        if (obj == null)
            return null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(obj);
            out.flush();

            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Couldn't serialize object of the class: " + obj.getClass().getSimpleName(), e);
        }
    }

    @Override public Object deserialize(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Couldn't deserialize bytes array: " + Arrays.toString(bytes), e);
        }
    }
}
