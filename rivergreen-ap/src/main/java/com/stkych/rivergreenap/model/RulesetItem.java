package com.stkych.rivergreenap.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;

/**
 * Model class representing an item in a ruleset.
 * A ruleset item consists of procedure codes, a priority, a description, teeth numbers, and a diagnosis.
 * This class is designed to work with JavaFX ListView.
 * Procedure codes and teeth numbers can be multiple values stored as comma-separated lists.
 */
public class RulesetItem {
    private final StringProperty priority;
    private final StringProperty procedureCodes;
    private final StringProperty description;
    private final StringProperty teethNumbers;
    private final StringProperty diagnosis;

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCodes The procedure codes as a comma-separated list
     * @param description The description of the procedure codes
     * @param teethNumbers The teeth numbers as a comma-separated list
     */
    public RulesetItem(String priority, String procedureCodes, String description, String teethNumbers) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCodes = new SimpleStringProperty(procedureCodes);
        this.description = new SimpleStringProperty(description);
        this.teethNumbers = new SimpleStringProperty(teethNumbers);
        this.diagnosis = new SimpleStringProperty("");
    }

    /**
     * Constructs a new RulesetItem with the specified values and empty teeth numbers.
     *
     * @param priority The priority of the item
     * @param procedureCodes The procedure codes as a comma-separated list
     * @param description The description of the procedure codes
     */
    public RulesetItem(String priority, String procedureCodes, String description) {
        this(priority, procedureCodes, description, "");
    }

    /**
     * Constructs a new RulesetItem with the specified values and empty description and teeth numbers.
     *
     * @param priority The priority of the item
     * @param procedureCodes The procedure codes as a comma-separated list
     */
    public RulesetItem(String priority, String procedureCodes) {
        this(priority, procedureCodes, "", "");
    }

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCodes The procedure codes as a comma-separated list
     * @param description The description of the procedure codes
     * @param teethNumbers The teeth numbers as a comma-separated list
     * @param diagnosis The diagnosis for the item
     */
    public RulesetItem(String priority, String procedureCodes, String description, String teethNumbers, String diagnosis) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCodes = new SimpleStringProperty(procedureCodes);
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

    // Procedure codes property
    public StringProperty procedureCodesProperty() {
        return procedureCodes;
    }

    public String getProcedureCodes() {
        return procedureCodes.get();
    }

    public void setProcedureCodes(String procedureCodes) {
        this.procedureCodes.set(procedureCodes);
    }

    // For backward compatibility
    public StringProperty procedureCodeProperty() {
        return procedureCodes;
    }

    public String getProcedureCode() {
        return procedureCodes.get();
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCodes.set(procedureCode);
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
                ", procedureCodes='" + getProcedureCodes() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", teethNumbers='" + getTeethNumbers() + '\'' +
                ", diagnosis='" + getDiagnosis() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RulesetItem)) return false;
        RulesetItem that = (RulesetItem) o;
        return Objects.equals(getPriority(), that.getPriority()) &&
                Objects.equals(getProcedureCodes(), that.getProcedureCodes()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getTeethNumbers(), that.getTeethNumbers()) &&
                Objects.equals(getDiagnosis(), that.getDiagnosis());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPriority(), getProcedureCodes(), getDescription(), getTeethNumbers(), getDiagnosis());
    }
}
