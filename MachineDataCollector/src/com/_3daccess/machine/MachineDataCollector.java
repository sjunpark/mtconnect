package com._3daccess.machine;

import java.sql.*;

public class MachineDataCollector {

    public static Connection conn;

    public static void main(String[] args) {
        String message=null;
        connectDB("TEST MESSAGE");

    }
    public static void connectDB(String message){
        try {
            conn = getConnection();
            PreparedStatement ps = null;
                if (conn != null) {
                    DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                    //System.out.println("Driver name: " + dm.getDriverName());
                    //System.out.println("Driver version: " + dm.getDriverVersion());
                    //System.out.println("Product name: " + dm.getDatabaseProductName());
                    //System.out.println("Product version: " + dm.getDatabaseProductVersion());

                    //System.out.println("MESSAGE=="+message);

                    ps = conn.prepareStatement("INSERT INTO [ERP].[dbo].[testing] VALUES (2, 5, ?, GETDATE())");
                    ps.setString(1, message);
                    ps.executeUpdate();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                        System.out.println("conn close");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = null;

        //String connectionUrl = "jdbc:sqlserver://10.1.10.252\\SQLEXPRESS;user=sa;password=46000HotchKiss";
        conn connection = DriverManager.getConnection("jdbc:mariadb://10.1.10.254:3306/JakeTest?user=jake&password=3Daccess#1");

        //conn = DriverManager.getConnection(connectionUrl);
        System.out.println("Connected to database");
        return conn;
    }



}
