package com.stkych.rivergreenap.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing an item in a ruleset.
 * A ruleset item consists of a procedure code, a priority, and a description.
 * This class is designed to work with JavaFX ListView.
 */
public class RulesetItem {
    private final StringProperty priority;
    private final StringProperty procedureCode;
    private final StringProperty description;

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     * @param description The description of the procedure code
     */
    public RulesetItem(String priority, String procedureCode, String description) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCode = new SimpleStringProperty(procedureCode);
        this.description = new SimpleStringProperty(description);
    }

    /**
     * Constructs a new RulesetItem with the specified values and an empty description.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     */
    public RulesetItem(String priority, String procedureCode) {
        this(priority, procedureCode, "");
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

    // Description property
    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
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
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
