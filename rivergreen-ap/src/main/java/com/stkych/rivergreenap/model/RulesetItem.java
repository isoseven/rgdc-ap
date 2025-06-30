package com.stkych.rivergreenap.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing an item in a ruleset.
 * A ruleset item consists of a procedure code and a priority.
 * This class is designed to work with JavaFX ListView.
 */
public class RulesetItem {
    private final StringProperty priority;
    private final StringProperty procedureCode;

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     */
    public RulesetItem(String priority, String procedureCode) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCode = new SimpleStringProperty(procedureCode);
    }

    // Priority property
    public StringProperty priorityProperty() {
        return priority;
    }

    public String getPriority() {
        return priority.get();
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    // Procedure code property
    public StringProperty procedureCodeProperty() {
        return procedureCode;
    }

    public String getProcedureCode() {
        return procedureCode.get();
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode.set(procedureCode);
    }

    /**
     * Returns a string representation of this RulesetItem.
     *
     * @return A string representation of this RulesetItem
     */
    @Override
    public String toString() {
        return "RulesetItem{" +
                "priority='" + getPriority() + '\'' +
                ", procedureCode='" + getProcedureCode() + '\'' +
                '}';
    }
}