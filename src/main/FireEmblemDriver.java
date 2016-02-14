package main;

import ui.FireEmblemUI;

import java.sql.Connection;
import java.sql.DriverManager;

public class FireEmblemDriver {

    public static Connection dbConnection = null;


    public static void main(String[] args) {
        // Connect to Local SQL Lite Database
        try{
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection("jdbc:sqlite:src/data/FireEmblem.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Launch the interface
        FireEmblemUI.main(args);
    }
}