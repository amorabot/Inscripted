package com.amorabot.rpgelements.CustomDataTypes;

import com.amorabot.rpgelements.utils.Utils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

@Deprecated
public class RPGElementsContainerDataType <T extends RPGElementsContainer> implements PersistentDataType<byte[], T> {
    /*
        A classe ItemInformation deve implementar Serializable para o processo funcionar.

        Uma vez implementado (e definido um ID de serialização, idealmente), podemos serializar o objeto em forma de
        byte array.

        Isso será feito utilizando uma ByteOutputStream, que armazenará o byte[] com as informações da nossa classe

        Uma vez criada, a ByteOutputStream deve ser passada para um ObjectOutputStream, que irá recebê-la e poderá
        processar o objeto que desejamos serializar, salvando-o na ByteOutputStream

        Ao salvarmos o byte[] na ByteOutputStream, podemos retornar o valor serializado.
         */
    private final Class<T> type;

    public RPGElementsContainerDataType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return this.type;
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<T> getComplexType() {
        return getType();
    }

    @Override
    public byte[] toPrimitive(T complex, PersistentDataAdapterContext context) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        try {

            out = new ObjectOutputStream(byteOut);
            out.writeObject(complex);
            out.flush();

            byteOut.close();

        } catch (IOException exception){
            exception.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    @Override
    public T fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in = null;

        try {

            in = new ObjectInputStream(byteIn);

            return (type.cast(in.readObject())); //TODO: check unchecked cast

        } catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
        }

        Utils.log("Erro na de-serialização do container ItemInfo");
        return null;
    }
}
