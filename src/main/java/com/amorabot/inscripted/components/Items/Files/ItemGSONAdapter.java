package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.utils.Utils;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ItemGSONAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        //Note: .getClass().getSimpleName() doesnt work, since .forName() cant map it properly. The full name/path is needed in this case
        result.add("class", new JsonPrimitive(item.getClass().getName()));
        result.add("data", jsonSerializationContext.serialize(item, item.getClass()));

        return result;
    }

    @Override
    public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        //Element -> object (Generic/Tree -> Specific/Branch/Node)
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        //Retrieving the type meta-data inserted during serialization
        String typeData = jsonObject.get("class").getAsString();
        try {
            //Mapping the class based on the serialized key for that
            Class<?> mappedClass = Class.forName(typeData);
            //Accessing the "data" part of the serialized JSON object
            JsonElement innerData = jsonObject.get("data").getAsJsonObject();
            //Deserializing only that part of the object to mapped to its original class
            return jsonDeserializationContext.deserialize(innerData, mappedClass);
        } catch (ClassNotFoundException e) {
            Utils.error("Erro na deserializacao de item :(");
            throw new RuntimeException(e);
        }
    }
}
