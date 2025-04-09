package com.militaryservices.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "options", schema = "ms")
@IdClass(OptionId.class)
public class Option {

    @Id
    @Column
    private String tableName;
    @Id
    @Column
    private String columnName;
    @Id
    @Column
    private String value;

    @Column
    private String option;

    public Option() {

    }

    public Option(String tableName, String columnName, String value, String option) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
        this.option = option;
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

    public String getOption() {
        return option;
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

    public void setOption(String option) {
        this.option = option;
    }
}
