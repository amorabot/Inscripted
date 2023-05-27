package pluginstudies.pluginstudies.CustomDataTypes.ItemInfo;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

import static pluginstudies.pluginstudies.utils.Utils.log;

@Deprecated
public class ItemInformationDataType implements PersistentDataType<byte[], BaseItem> {
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<BaseItem> getComplexType() {
        return BaseItem.class;
    }

    @Override
    public byte[] toPrimitive(BaseItem complex, PersistentDataAdapterContext context) {
        /*
        A classe ItemInformation deve implementar Serializable para o processo funcionar.

        Uma vez implementado (e definido um ID de serialização, idealmente), podemos serializar o objeto em forma de
        byte array.

        Isso será feito utilizando uma ByteOutputStream, que armazenará o byte[] com as informações da nossa classe

        Uma vez criada, a ByteOutputStream deve ser passada para um ObjectOutputStream, que irá recebê-la e poderá
        processar o objeto que desejamos serializar, salvando-o na ByteOutputStream

        Ao salvarmos o byte[] na ByteOutputStream, podemos retornar o valor serializado.
         */
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
    public BaseItem fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        /*
        Aqui será feito o processo reverso. Iremos pegar o byte[] serializado em toPrimitive() e transformá-lo em
        um objeto novamente.
         */

        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in = null;

        try {

            in = new ObjectInputStream(byteIn);

            return (BaseItem) in.readObject();

        } catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
        }

        log("Erro na de-serialização do container ItemInfo");
        return null;
    }
}
