package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class JavaSerializer {
    private static final int BUFFER_SIZE = 4096;

    public byte[] serialize(Object obj) throws IOException {
        if (obj == null)
            return null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(obj);
            out.flush();

            return baos.toByteArray();
        }
    }

    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }
}
