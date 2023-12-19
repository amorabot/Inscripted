package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.utils.Utils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class MobStatsContainer implements PersistentDataType<byte[], MobStats> {
    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<MobStats> getComplexType() {
        return MobStats.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull MobStats mobStats, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;

        try {

            out = new ObjectOutputStream(byteOut);
            out.writeObject(mobStats);
            out.flush();

            byteOut.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    @NotNull
    @Override
    public MobStats fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in;

        try {

            in = new ObjectInputStream(byteIn);

            return (MobStats) (in.readObject());

        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        Utils.log("Erro na de-serialização: MobStatsContainer");
        return null;
    }
}
