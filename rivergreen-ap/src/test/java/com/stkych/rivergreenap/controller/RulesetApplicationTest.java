package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.model.RulesetItem;
import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import com.stkych.rivergreenap.util.TeethNotationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RulesetApplicationTest {

    private static final String TEST_RULESET_NAME = "Test";
    private static final String TEST_RULESET_FILE = "ruleset" + TEST_RULESET_NAME + ".csv";

    @BeforeEach
    public void setUp() {
        // Create a test ruleset file
        createTestRulesetFile();
    }

    @Test
    public void testRulesetApplication() {
        // Create a list of procedures
        List<TreatmentPlanProcedure> procedures = createTestProcedures();

        // Create a list of ruleset items
        List<RulesetItem> ruleset = createTestRuleset();

        // Apply the ruleset to the procedures
        applyRuleset(ruleset, procedures);

        // Verify that the priorities and diagnoses were updated correctly
        assertEquals("1", procedures.get(1).getPriority(), "Priority should be updated for procedure with matching code and tooth");
        assertEquals("Prophylaxis", procedures.get(1).getDiagnosis(), "Diagnosis should be updated for procedure with matching code and tooth");

        assertEquals("10", procedures.get(2).getPriority(), "Priority should be updated for procedure with matching code and tooth");
        assertEquals("Composite Restoration", procedures.get(2).getDiagnosis(), "Diagnosis should be updated for procedure with matching code and tooth");

        assertEquals("", procedures.get(3).getPriority(), "Priority should not be updated for procedure with non-matching tooth");
        assertEquals("", procedures.get(3).getDiagnosis(), "Diagnosis should not be updated for procedure with non-matching tooth");
    }

    private List<TreatmentPlanProcedure> createTestProcedures() {
        List<TreatmentPlanProcedure> procedures = new ArrayList<>();

        // Add a header procedure
        procedures.add(new TreatmentPlanProcedure("Priority", "Tooth", "Surface", "Code", "Diagnosis", "Description", 0.0, 0));

        // Add test procedures
        TreatmentPlanProcedure proc1 = new TreatmentPlanProcedure("", "1", "B", "D1110", "", "Prophylaxis", 100.00, 1);
        TreatmentPlanProcedure proc2 = new TreatmentPlanProcedure("", "2", "MOD", "D2393", "", "Composite Restoration", 200.00, 2);
        TreatmentPlanProcedure proc3 = new TreatmentPlanProcedure("", "20", "B", "D1110", "", "Prophylaxis", 100.00, 3);

        procedures.add(proc1);
        procedures.add(proc2);
        procedures.add(proc3);

        return procedures;
    }

    private List<RulesetItem> createTestRuleset() {
        List<RulesetItem> ruleset = new ArrayList<>();

        // Add a header item
        ruleset.add(new RulesetItem("Header", "Header", "Description", "Teeth"));

        // Add test ruleset items
        RulesetItem item1 = new RulesetItem("1", "D1110", "Prophylaxis", "1-2-3-4-5-6-7-8");
        item1.setDiagnosis("Prophylaxis");

        RulesetItem item2 = new RulesetItem("10", "D2393", "Composite Restoration", "2-18-19-30-31");
        item2.setDiagnosis("Composite Restoration");

        RulesetItem item3 = new RulesetItem("2", "D3347", "Root Canal", "26-8-24-10-22");
        item3.setDiagnosis("Root Canal");

        RulesetItem item4 = new RulesetItem("Next", "D0363", "Cone Beam CT", "5-6");
        item4.setDiagnosis("3D Imaging");

        RulesetItem item5 = new RulesetItem("3", "D2740", "Crown", "12-13-14");
        item5.setDiagnosis("Porcelain Crown");

        ruleset.add(item1);
        ruleset.add(item2);
        ruleset.add(item3);
        ruleset.add(item4);
        ruleset.add(item5);

        return ruleset;
    }

    private void applyRuleset(List<RulesetItem> ruleset, List<TreatmentPlanProcedure> procedures) {
        // Skip the header item (index 0)
        for (int i = 1; i < ruleset.size(); i++) {
            RulesetItem item = ruleset.get(i);
            String procedureCode = item.getProcedureCode();
            String priority = item.getPriority();
            String diagnosis = item.getDiagnosis();

            // If diagnosis is empty, use description as fallback
            if (diagnosis == null || diagnosis.isEmpty()) {
                diagnosis = item.getDescription();
            }

            // Get teeth information from the teethNumbers property
            List<String> ruleTeeth = new ArrayList<>();
            String teethNumbers = item.getTeethNumbers();
            if (teethNumbers != null && !teethNumbers.isEmpty()) {
                List<Integer> expanded = TeethNotationUtil.expandTeeth(teethNumbers);
                for (Integer tooth : expanded) {
                    ruleTeeth.add(String.valueOf(tooth));
                }
            }

            // Update the priority and diagnosis of procedures with matching procedure code and teeth
            for (int j = 1; j < procedures.size(); j++) { // Skip the header item (index 0)
                TreatmentPlanProcedure procedure = procedures.get(j);

                // Check if procedure code matches
                if (procedure.getProcedureCode().equals(procedureCode)) {
                    // If the rule has teeth specified, check if the procedure's tooth matches any of them
                    if (!ruleTeeth.isEmpty()) {
                        String procedureTooth = procedure.getToothNumber();
                        if (procedureTooth != null && !procedureTooth.isEmpty() && ruleTeeth.contains(procedureTooth)) {
                            // Update priority and diagnosis if both procedure code and tooth match
                            procedure.setPriority(priority);

                            // Use the diagnosis property
                            if (diagnosis != null && !diagnosis.isEmpty()) {
                                procedure.setDiagnosis(diagnosis);
                            }
                        }
                    } else {
                        // If the rule doesn't specify teeth, update all procedures with matching code
                        procedure.setPriority(priority);

                        // Also update diagnosis even if no teeth are specified
                        if (diagnosis != null && !diagnosis.isEmpty()) {
                            procedure.setDiagnosis(diagnosis);
                        }
                    }
                }
            }
        }
    }

    private void createTestRulesetFile() {
        File file = new File(TEST_RULESET_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("1,D1110,1-2-3-4-5-6-7-8,Prophylaxis");
            writer.newLine();
            writer.write("10,D2393,2-18-19-30-31,Composite Restoration");
            writer.newLine();
            writer.write("2,D3347,26-8-24-10-22,Root Canal");
            writer.newLine();
            writer.write("Next,D0363,5-6,3D Imaging");
            writer.newLine();
            writer.write("3,D2740,12-13-14,Porcelain Crown");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
