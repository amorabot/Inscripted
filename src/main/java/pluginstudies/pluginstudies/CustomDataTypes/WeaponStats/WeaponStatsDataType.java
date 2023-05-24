package pluginstudies.pluginstudies.CustomDataTypes.WeaponStats;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.CustomDataTypes.WeaponStats.WeaponStats;

import java.io.*;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class WeaponStatsDataType implements PersistentDataType<byte[], WeaponStats> {
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<WeaponStats> getComplexType() {
        return WeaponStats.class;
    }

    @Override
    public byte[] toPrimitive(WeaponStats complex, PersistentDataAdapterContext context) {
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
    public WeaponStats fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in = null;

        try {

            in = new ObjectInputStream(byteIn);

            return (WeaponStats) in.readObject();

        } catch (IOException | ClassNotFoundException exception){
            exception.printStackTrace();
        }

        log("Erro na de-serialização do container ItemInfo");
        return null;
    }
}
