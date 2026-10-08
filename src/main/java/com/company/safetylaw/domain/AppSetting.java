package com.company.safetylaw.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppSetting {
    private String settingKey;
    private String settingValue;

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String value) {
        this.settingKey = value;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String value) {
        this.settingValue = value;
    }
}
