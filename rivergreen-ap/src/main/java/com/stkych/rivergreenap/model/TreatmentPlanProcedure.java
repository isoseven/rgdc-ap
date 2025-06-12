package com.stkych.rivergreenap.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing a procedure in a treatment plan.
 * This class is designed to work with JavaFX TableView.
 */
public class TreatmentPlanProcedure {
    private final StringProperty priority;
    private final StringProperty toothNumber;
    private final StringProperty surface;
    private final StringProperty procedureCode;
    private final StringProperty diagnosis;
    private final StringProperty description;
    private final DoubleProperty fee;

    /**
     * Constructs a new TreatmentPlanProcedure with the specified values.
     *
     * @param priority The priority of the procedure
     * @param toothNumber The tooth number
     * @param surface The surface
     * @param procedureCode The procedure code
     * @param diagnosis The diagnosis
     * @param description The description
     * @param fee The fee
     */
    public TreatmentPlanProcedure(String priority, String toothNumber, String surface, String procedureCode,
                                 String diagnosis, String description, double fee) {
        this.priority = new SimpleStringProperty(priority);
        this.toothNumber = new SimpleStringProperty(toothNumber);
        this.surface = new SimpleStringProperty(surface);
        this.procedureCode = new SimpleStringProperty(procedureCode);
        this.diagnosis = new SimpleStringProperty(diagnosis);
        this.description = new SimpleStringProperty(description);
        this.fee = new SimpleDoubleProperty(fee);
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

    // Tooth number property
    public StringProperty toothNumberProperty() {
        return toothNumber;
    }
    
    public String getToothNumber() {
        return toothNumber.get();
    }
    
    public void setToothNumber(String toothNumber) {
        this.toothNumber.set(toothNumber);
    }

    // Surface property
    public StringProperty surfaceProperty() {
        return surface;
    }
    
    public String getSurface() {
        return surface.get();
    }
    
    public void setSurface(String surface) {
        this.surface.set(surface);
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

    // Fee property
    public DoubleProperty feeProperty() {
        return fee;
    }
    
    public double getFee() {
        return fee.get();
    }
    
    public void setFee(double fee) {
        this.fee.set(fee);
    }
}