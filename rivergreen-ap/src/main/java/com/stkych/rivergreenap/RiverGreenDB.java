package com.stkych.rivergreenap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RiverGreenDB {
    private static final String URL = "jdbc:mysql://localhost:3306/opendental";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "test";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /*
    SQL commands:
    to get active treatment plan number:

    -- getting patient name
    SELECT LName, FName, MiddleI
    FROM patient
    WHERE PatNum = [Enter PatNum];

    -- getting active tx plan number
    SELECT DISTINCT pt.TreatPlanNum
    FROM proctp pt
    JOIN treatplan tp ON tp.TreatPlanNum = pt.TreatPlanNum
    WHERE pt.PatNum = [Enter PatNum]
    AND tp.TPStatus = 1;

    -- getting tooth #, surface, proc. code, description, fee, diagnosis
    SELECT ToothNumTP, Surf, ProcCode, Descript, FeeAmt, Dx
    FROM proctp
    WHERE TreatPlanNum = [Enter TreatPlanNum];

    -- getting priority (output)
    SELECT Priority
    FROM treatplanattach
    WHERE TreatPlanNum = [Enter TreatPlanNum];

    -- getting priority colors/definition #s
    SELECT ItemColor, DefNum
    FROM definition
    WHERE Category = 20;


     */
}
