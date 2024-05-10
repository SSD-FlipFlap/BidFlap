package com.ssd.bidflap.domain.enums;

public enum Category {
    MOBILE("mobile"),
    TABLET("tablet"),
    CAMERA("camera"),
    PC_PARTS("pc_parts"),
    WEARABLE("wearable"),
    PC_NOTEBOOK("pc_notebook"),
    ACCESSORY("accessory"),
    ETC("etc");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
