package com.stkych.rivergreenap.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing an item in a ruleset.
 * A ruleset item consists of a procedure code, a priority, a description, teeth numbers, and a diagnosis.
 * This class is designed to work with JavaFX ListView.
 */
public class RulesetItem {
    private final StringProperty priority;
    private final StringProperty procedureCode;
    private final StringProperty description;
    private final StringProperty teethNumbers;
    private final StringProperty diagnosis;

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     * @param description The description of the procedure code
     * @param teethNumbers The teeth numbers as a comma-separated list
     */
    public RulesetItem(String priority, String procedureCode, String description, String teethNumbers) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCode = new SimpleStringProperty(procedureCode);
        this.description = new SimpleStringProperty(description);
        this.teethNumbers = new SimpleStringProperty(teethNumbers);
        this.diagnosis = new SimpleStringProperty("");
    }

    /**
     * Constructs a new RulesetItem with the specified values and empty teeth numbers.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     * @param description The description of the procedure code
     */
    public RulesetItem(String priority, String procedureCode, String description) {
        this(priority, procedureCode, description, "");
    }

    /**
     * Constructs a new RulesetItem with the specified values and empty description and teeth numbers.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     */
    public RulesetItem(String priority, String procedureCode) {
        this(priority, procedureCode, "", "");
    }

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     * @param description The description of the procedure code
     * @param teethNumbers The teeth numbers as a comma-separated list
     * @param diagnosis The diagnosis for the item
     */
    public RulesetItem(String priority, String procedureCode, String description, String teethNumbers, String diagnosis) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCode = new SimpleStringProperty(procedureCode);
        this.description = new SimpleStringProperty(description);
        this.teethNumbers = new SimpleStringProperty(teethNumbers);
        this.diagnosis = new SimpleStringProperty(diagnosis);
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

    // Teeth numbers property
    public StringProperty teethNumbersProperty() {
        return teethNumbers;
    }

    public String getTeethNumbers() {
        return teethNumbers.get();
    }

    public void setTeethNumbers(String teethNumbers) {
        this.teethNumbers.set(teethNumbers);
    }

    // Diagnosis property
    public StringProperty diagnosisProperty() {
        return diagnosis;
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis.set(diagnosis);
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
                ", teethNumbers='" + getTeethNumbers() + '\'' +
                ", diagnosis='" + getDiagnosis() + '\'' +
                '}';
    }
}
