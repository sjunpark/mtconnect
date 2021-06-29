package com._3daccess.machine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;


public class ProcessBuilderExample1 {


    public Connection conn = null;

    public static void main(String[] args) {


        for(int i = 0; i<1000; i++) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            // Windows
            //processBuilder.command("cmd.exe", "/c", "whoami");
            processBuilder.command("cmd.exe", "/c", "C:\\Users\\Hayley\\Desktop\\HELLO.bat | C:\\p\\plink.exe -load \"testcom\"");
            //processBuilder.command("cmd.exe", "/c", "C:\\Users\\Hayley\\Desktop\\HELLO.bat | C:\\p\\plink.exe -load \"testcom\"");

            try {

                Process process = processBuilder.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                String message = new String();
                System.out.println("1");
                int count = 0;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    message = line;
                    if (message.equals("") || message.equals(">")) {

                    } else {
                        LoadDriver(message);
                        count++;
                    }

                    System.out.println("count=" + count);
                    if (count >= 13 ) {
                        ProcessBuilder processBuilder2 = new ProcessBuilder();
                        processBuilder2.command("cmd.exe", "/c", "taskkill /F /im plink.exe");
                        processBuilder2.start();
                        System.out.println("Killed Process");
                    }
                }


                System.out.println("2");

                System.out.println(message);
                System.out.println("new line= " + line);

                //For Finding a Error
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line2;
                String message2 = new String();
                while ((line2 = reader2.readLine()) != null) {
                    //System.out.println(line2);
                    message2 += line2;
                }

                System.out.println(message2);

                int exitCode = process.waitFor();
                System.out.println("\nExited with error code : " + exitCode);

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


        public static void LoadDriver(String message) {

            //String connectionUrl = "jdbc:sqlserver://3DACCESS-BACKUP\\SQLEXPRESS;user=sa;password=46000HotchKiss";
            String connectionUrl = "jdbc:sqlserver://10.1.10.252\\SQLEXPRESS;user=sa;password=46000HotchKiss";

            Connection con =null;
            PreparedStatement ps = null;
            try {
                con = DriverManager.getConnection(connectionUrl);
                if (con != null) {
                    DatabaseMetaData dm = (DatabaseMetaData) con.getMetaData();
                    //System.out.println("Driver name: " + dm.getDriverName());
                    //System.out.println("Driver version: " + dm.getDriverVersion());
                    //System.out.println("Product name: " + dm.getDatabaseProductName());
                    //System.out.println("Product version: " + dm.getDatabaseProductVersion());

                    //System.out.println("MESSAGE=="+message);

                    ps = con.prepareStatement("INSERT INTO [ERP].[dbo].[testing] VALUES (2, 5, ?, GETDATE())");
                    ps.setString(1, message);
                    ps.executeUpdate();

                }
                System.out.println("success insert ="+ message);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        }
}


