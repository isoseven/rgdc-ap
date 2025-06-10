package com.stkych.rivergreenap.old;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class OldControl {
    // sceneMain.fxml elements
    @FXML
    private Button preferencesButton;
    // --> sceneDr.fxml

    @FXML
    private Button fillPriorityButton;
    // --> scenePt.fxml

    @FXML
    private Label autoFillLabel;
    
    // sceneDr.fxml elements
    @FXML
    private Button drConfirmButton;
    @FXML
    private Button drBackButton;
    // --> sceneMain.fxml

    @FXML
    private Label drNameLabel;
    @FXML
    private TableView<?> drTableView;
    @FXML
    private TableColumn<?, ?> drPriorityColumn;
    @FXML
    private TableColumn<?, ?> drToothColumn;
    @FXML
    private TableColumn<?, ?> drCodeColumn;
    
    // sceneDrEdit.fxml elements
    @FXML
    private Button editButton;
    @FXML
    private Button editBackButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<String> drNameComboBox;
    @FXML
    private TableView<?> editTableView;
    @FXML
    private TableColumn<?, ?> editPriorityColumn;
    @FXML
    private TableColumn<?, ?> editToothColumn;
    @FXML
    private TableColumn<?, ?> editCodeColumn;
    
    // sceneDrNew.fxml elements
    @FXML
    private Button newConfirmButton;
    @FXML
    private Button newBackButton;
    @FXML
    private Label newNameLabel;
    @FXML
    private Label codeLabel;
    @FXML
    private Label teethLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label diagLabel;
    @FXML
    private TextField codeTextField;
    @FXML
    private TextField priorityTextField;
    @FXML
    private TextField diagTextField;
    @FXML
    private CheckBox teethCheckBox1;
    @FXML
    private CheckBox teethCheckBox2;
    
    // Tooth checkboxes (32 of them)
    @FXML
    private CheckBox tooth1;
    @FXML
    private CheckBox tooth2;
    @FXML
    private CheckBox tooth3;
    @FXML
    private CheckBox tooth4;
    @FXML
    private CheckBox tooth5;
    @FXML
    private CheckBox tooth6;
    @FXML
    private CheckBox tooth7;
    @FXML
    private CheckBox tooth8;
    @FXML
    private CheckBox tooth9;
    @FXML
    private CheckBox tooth10;
    @FXML
    private CheckBox tooth11;
    @FXML
    private CheckBox tooth12;
    @FXML
    private CheckBox tooth13;
    @FXML
    private CheckBox tooth14;
    @FXML
    private CheckBox tooth15;
    @FXML
    private CheckBox tooth16;
    @FXML
    private CheckBox tooth17;
    @FXML
    private CheckBox tooth18;
    @FXML
    private CheckBox tooth19;
    @FXML
    private CheckBox tooth20;
    @FXML
    private CheckBox tooth21;
    @FXML
    private CheckBox tooth22;
    @FXML
    private CheckBox tooth23;
    @FXML
    private CheckBox tooth24;
    @FXML
    private CheckBox tooth25;
    @FXML
    private CheckBox tooth26;
    @FXML
    private CheckBox tooth27;
    @FXML
    private CheckBox tooth28;
    @FXML
    private CheckBox tooth29;
    @FXML
    private CheckBox tooth30;
    @FXML
    private CheckBox tooth31;
    @FXML
    private CheckBox tooth32;
    
    // scenePt.fxml elements
    @FXML
    private Button ptConfirmButton;
    @FXML
    private Button ptBackButton;
    @FXML
    private Label ptNameLabel;
    @FXML
    private TableView<?> ptTableView;
    @FXML
    private TableColumn<?, ?> ptPriorityColumn;
    @FXML
    private TableColumn<?, ?> ptToothColumn;
    @FXML
    private TableColumn<?, ?> ptCodeColumn;
    
    // Event handlers for sceneMain.fxml
    @FXML
    protected void onPreferencesButtonClick() {
        // Handle preferences button click
    }
    
    @FXML
    protected void onFillPriorityButtonClick() {
        // Handle fill priority button click
    }
    
    // Event handlers for sceneDr.fxml
    @FXML
    protected void onDrConfirmButtonClick() {
        // Handle doctor confirm button click
    }
    
    @FXML
    protected void onDrBackButtonClick() {
        // Handle doctor back button click
    }
    
    // Event handlers for sceneDrEdit.fxml
    @FXML
    protected void onEditButtonClick() {
        // Handle edit button click
    }
    
    @FXML
    protected void onEditBackButtonClick() {
        // Handle edit back button click
    }
    
    @FXML
    protected void onDeleteButtonClick() {
        // Handle delete button click
    }
    
    // Event handlers for sceneDrNew.fxml
    @FXML
    protected void onNewConfirmButtonClick() {
        // Handle new confirm button click
    }
    
    @FXML
    protected void onNewBackButtonClick() {
        // Handle new back button click
    }
    
    // Event handlers for scenePt.fxml
    @FXML
    protected void onPtConfirmButtonClick() {
        // Handle patient confirm button click
    }
    
    @FXML
    protected void onPtBackButtonClick() {
        // Handle patient back button click
    }
}