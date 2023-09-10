package com.amorabot.rpgelements.components.Items.DataStructures;

import com.amorabot.rpgelements.components.Items.Abstract.Item;
import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

public record GenericItemContainerDataType<T extends Item>(Class<T> type) implements PersistentDataType<byte[], T> {

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<T> getComplexType() {
        return type();
    }

    @Override
    public byte[] toPrimitive(T complex, PersistentDataAdapterContext context) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;

        try {

            out = new ObjectOutputStream(byteOut);
            out.writeObject(complex);
            out.flush();

            byteOut.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    @Override
    public T fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in;

        try {

            in = new ObjectInputStream(byteIn);

            return (type.cast(in.readObject()));

        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        Utils.log("Erro na de-serialização do container item-data");
        return null;
    }
}
