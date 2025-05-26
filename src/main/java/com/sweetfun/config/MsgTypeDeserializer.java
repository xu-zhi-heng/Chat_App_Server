package com.sweetfun.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.sweetfun.emun.MsgType;

import java.io.IOException;

public class MsgTypeDeserializer extends JsonDeserializer<MsgType> {
    @Override
    public MsgType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getText();
        for (MsgType msgType : MsgType.values()) {
            if (msgType.name().equalsIgnoreCase(value)) {
                return msgType;
            }
        }
        return MsgType.UNKNOWN; // 返回默认值
    }
}
