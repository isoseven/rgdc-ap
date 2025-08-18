package com.stkych.rivergreenap.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;

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
    private final BooleanProperty dependent;
    private final StringProperty conditionalPriority;
    private final StringProperty newPriority;

    /**
     * Constructs a new RulesetItem with the specified values.
     *
     * @param priority The priority of the item
     * @param procedureCode The procedure code
     * @param description The description of the procedure code
     * @param teethNumbers The teeth numbers as a comma-separated list
     */
    public RulesetItem(String priority, String procedureCode, String description, String teethNumbers) {
        this(priority, procedureCode, description, teethNumbers, "", false, "", "");
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
        this(priority, procedureCode, description, teethNumbers, diagnosis, false, "", "");
    }

    /**
     * Constructs a new RulesetItem with all available values.
     *
     * @param priority            The priority of the item
     * @param procedureCode       The procedure code
     * @param description         The description of the procedure code
     * @param teethNumbers        The teeth numbers as a comma-separated list
     * @param diagnosis           The diagnosis for the item
     * @param isDependent         Whether this rule is dependent on the existing priority
     * @param conditionalPriority The priority that must be present for the rule to apply
     * @param newPriority         The priority to set when the condition is met
     */
    public RulesetItem(String priority, String procedureCode, String description, String teethNumbers, String diagnosis,
                       boolean isDependent, String conditionalPriority, String newPriority) {
        this.priority = new SimpleStringProperty(priority);
        this.procedureCode = new SimpleStringProperty(procedureCode);
        this.description = new SimpleStringProperty(description);
        this.teethNumbers = new SimpleStringProperty(teethNumbers);
        this.diagnosis = new SimpleStringProperty(diagnosis);
        this.dependent = new SimpleBooleanProperty(isDependent);
        this.conditionalPriority = new SimpleStringProperty(conditionalPriority);
        this.newPriority = new SimpleStringProperty(newPriority);
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

    // Dependent property
    public BooleanProperty dependentProperty() {
        return dependent;
    }

    public boolean isDependent() {
        return dependent.get();
    }

    public void setDependent(boolean isDependent) {
        this.dependent.set(isDependent);
    }

    // Conditional priority property
    public StringProperty conditionalPriorityProperty() {
        return conditionalPriority;
    }

    public String getConditionalPriority() {
        return conditionalPriority.get();
    }

    public void setConditionalPriority(String conditionalPriority) {
        this.conditionalPriority.set(conditionalPriority);
    }

    // New priority property
    public StringProperty newPriorityProperty() {
        return newPriority;
    }

    public String getNewPriority() {
        return newPriority.get();
    }

    public void setNewPriority(String newPriority) {
        this.newPriority.set(newPriority);
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
                ", isDependent='" + isDependent() + '\'' +
                ", conditionalPriority='" + getConditionalPriority() + '\'' +
                ", newPriority='" + getNewPriority() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RulesetItem)) return false;
        RulesetItem that = (RulesetItem) o;
        return Objects.equals(getPriority(), that.getPriority()) &&
                Objects.equals(getProcedureCode(), that.getProcedureCode()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getTeethNumbers(), that.getTeethNumbers()) &&
                Objects.equals(getDiagnosis(), that.getDiagnosis()) &&
                Objects.equals(isDependent(), that.isDependent()) &&
                Objects.equals(getConditionalPriority(), that.getConditionalPriority()) &&
                Objects.equals(getNewPriority(), that.getNewPriority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPriority(), getProcedureCode(), getDescription(), getTeethNumbers(), getDiagnosis(),
                isDependent(), getConditionalPriority(), getNewPriority());
    }
}
