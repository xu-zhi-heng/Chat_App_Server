package com.sweetfun.emun;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum MsgType {
    @JsonEnumDefaultValue
    UNKNOWN(-1, "未知"),
    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    VOICE(2, "语音"),
    VIDEO(3, "视频"),
    REFERENCE(4, "引用"),
    ;

    private Integer code;
    private String desc;

    MsgType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
