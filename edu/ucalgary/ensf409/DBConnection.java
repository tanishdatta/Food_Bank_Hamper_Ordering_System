package edu.ucalgary.ensf409;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    public static final String DBURL = "jdbc:mysql://10.0.0.11/FOOD_INVENTORY";
    public static final String USERNAME = "student";
    public static final String PASSWORD = "ensf";    
    
    private Connection dbConnect;
    private ResultSet results;
    


//Must create a connection to the database, no arguments, no return value    
    /**
     * Starts a connection to the MySQL Database 
     * and stores it in dbConnect.
     * 
     * @param DBURL,USERNAME,PASSWORD passed in as arguments,
     * 
     */
    public void initializeConnection(){

    try {
        this.dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    } 
    catch (SQLException e) {
        GUIViewController.genericError("Could not initialize Connection");
        System.out.println("HAMPER APP - SQL DEBUG FOR TA's: ");
        System.out.println(e.toString());
    }
    }


    public void preparedQuery(String query, String... args ){

        try {
            PreparedStatement myStmt = dbConnect.prepareStatement(query);
            
            int i = 1;
            for (String arg : args){
                myStmt.setString(i, arg);
                i++;
            }
            myStmt.executeUpdate();
            
            myStmt.close();
            }   
        catch (SQLException e) {
            GUIViewController.genericError("Error processing prepared statement");
            System.out.println("HAMPER APP - SQL DEBUG FOR TA's: ");
            System.out.println(e.toString());
            }
        }

        public ResultSet customQuery(String query){

            try {
                Statement myStmt = dbConnect.createStatement();
                ResultSet results = myStmt.executeQuery(query);
                return results;
                
                }   
            catch (SQLException e) {
                GUIViewController.genericError("Error processing custom query");
                System.out.println("HAMPER APP - SQL DEBUG FOR TA's: ");
                System.out.println(e.toString());
                return null;
                }
            }

    public static boolean testDBConnection(){
            try {
                Connection dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
                return true;
            } 
            catch (SQLException e) {
                System.out.println("HAMPER APP - SQL DEBUG FOR TA's: ");
                System.out.println(e.toString());
                return false;
            }
        }    

    
}
