package pluginstudies.pluginstudies.CustomDataTypes.WeaponStats;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

import static pluginstudies.pluginstudies.utils.Utils.log;

@Deprecated
public class WeaponStatsDataType implements PersistentDataType<byte[], Weapon> { //TODO: implementar generics
    //implementação com T extends SuperClasseDesejada
    // public class <T extends EquippableItem> ItemContainer implements PersistentDataType<byte[], T>... --> ItemContainer<Weapon>
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Weapon> getComplexType() {
        return Weapon.class;
    }

    @Override
    public byte[] toPrimitive(Weapon complex, PersistentDataAdapterContext context) {
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
    public Weapon fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in = null;

        try {

            in = new ObjectInputStream(byteIn);

            return (Weapon) in.readObject();

        } catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
        }

        log("Erro na de-serialização do container ItemInfo");
        return null;
    }
}
