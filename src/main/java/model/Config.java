package model;

import enumerate.ConfigType;

/**
 * Created by jakub on 29/10/15.
 */
public class Config<T> {

    String name;
    T value;
    T defaultValue;
    String description;

    public Config(ConfigType configType, T value, T defaultValue) {
        this.name = configType.getName();
        this.value = value;
        this.defaultValue = defaultValue;
        this.description = configType.getDescription();
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
