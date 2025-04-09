package com.militaryservices.app.entity;

import java.io.Serializable;

public class OptionId implements Serializable {

    private String tableName;
    private String columnName;
    private String value;

    public OptionId() {

    }

    public OptionId(String tableName, String columnName, String value) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
    }

    public String getTable() {
        return tableName;
    }

    public String getColumn() {
        return columnName;
    }

    public String getValue() {
        return value;
    }

    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    public void setColumn(String columnName) {
        this.columnName = columnName;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
