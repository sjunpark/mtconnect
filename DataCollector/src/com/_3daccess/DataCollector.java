package com._3daccess;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DataCollector {

    public static Connection conn;
    public static String dburl;
    public static String command;
    public static String commandCount;
    static InputStream inputStream;
    public static String message;
    public static String[] value;
    public static String machineid;
    public static String pickLatestFileFromDownloads;

    public static void main(String[] args) {
	    // write your code here




        killprocess();

        try {
            Thread.sleep(5000);
            getPropValues(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        execute();

    }

    public static void killprocess(){
        ProcessBuilder processKiller = new ProcessBuilder();
        processKiller.command("cmd.exe", "/c", "taskkill /F /im plink.exe");
        try {
            processKiller.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Killed Process");
    }

    public static String timecal(String prev, String curr)
    {
        String time1 = prev;
        String time2 = curr;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = date2.getTime() - date1.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        difference -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        difference -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);

//        long diffSeconds = difference / 1000 % 60;
//        long diffMinutes = difference / (60 * 1000) % 60;
//        long diffHours = difference / (60 * 60 * 1000) % 24;
//        long diffDays = difference / (24 * 60 * 60 * 1000);1000


        System.out.print(hours + " hours, ");
        System.out.print(minutes + " minutes, ");
        System.out.print(seconds + " seconds.");
        String sthours="";
        String stmin = "";
        String stsecond="";
        if(hours<10)
        {
            sthours= "0"+String.valueOf(hours);
        }
        else{
            sthours = String.valueOf(hours);
        }
        if(minutes<10)
        {
            stmin= "0"+String.valueOf(minutes);
        }
        else{
            stmin = String.valueOf(minutes);
        }
        if(seconds<10)
        {
            stsecond= "0"+String.valueOf(seconds);
        }
        else{
            stsecond = String.valueOf(seconds);
        }

        return sthours+":"+stmin+":"+stsecond;
    }


    public static void execute(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", command);

            try{

                System.out.println("command=="+command);
                String recentwork = pickLatestFileFromDownloads("\\\\FLOOR3\\cnc\\Haas4");
                //String recentwork = pickLatestFileFromDownloads("C:\\p");
                System.out.println("RECENT WORK - "+recentwork);

                Path path = Paths.get("\\\\FLOOR3\\cnc\\Haas4\\"+recentwork);
                //Path path = Paths.get("C:\\p\\"+recentwork);
                FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
                UserPrincipal owner = ownerAttributeView.getOwner();
                System.out.println("owner: " + owner.getName());

                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                int resultCont=0;
                String line;
                while ((line = reader.readLine()) != null) {
                    message = line;
                    if (message.equals("") || message.equals(">")) {

                    } else {

                        message = message.replace("", "");
                        message = message.replace("", "");
                        System.out.println("MESSAGE==="+message);
                        //value = message.split("," , 0);
                        value = message.split("," , 0);

                        System.out.println("Before DB Connect");
                        connectDB(value, recentwork);
                        System.out.println("Finish DB Connect");

                        resultCont++;
                    }

                    if (resultCont >= Integer.parseInt(commandCount)) {
                        System.out.println("CommandCount reached. Killed Process()");
                        killprocess();
                    }
                }

                    System.out.println("message = "+message);

                    //For Finding a Error
                    BufferedReader reader2 =
                            new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String line2;
                    String message2 = "";
                    while ((line2 = reader2.readLine()) != null) {
                        //System.out.println(line2);
                        message2 += line2;
                    }
                    System.out.println("Error message = "+message2);

                    if(message==null && message2!=null)
                    {
                        String[] m2 = new String[2];
                        m2[1]=message2;
                        m2[0]="Error";
                        connectDB(m2, "");
                    }

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

    public static void getPropValues(String fileURL) throws IOException {
        try {
            Properties prop = new Properties();
            //String propFileName = "config.properties";
            String propFileName = fileURL;
            //inputStream = DataCollector.class.getClassLoader().getResourceAsStream(propFileName);
            inputStream = new FileInputStream(fileURL);
            //ClassLoader loader = Thread.currentThread().getContextClassLoader();
            //InputStream inputStream = loader.getResourceAsStream("config.properties");
            //prop.load(inputStream);
            //System.out.println(prop.getProperty("name"));

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            // get the property value and print it out
            dburl = prop.getProperty("database");
            command = prop.getProperty("command");
            commandCount = prop.getProperty("commandCount");
            machineid = prop.getProperty("machineID");

            System.out.println("database=" + dburl);
            System.out.println("command=" + command);
            System.out.println("commandCount=" + commandCount);
            System.out.println("machineid=" + machineid);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }


    public static void connectDB(String[] value, String recentwork){
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (conn != null) {
                DatabaseMetaData dm = conn.getMetaData();
                //System.out.println("Driver name: " + dm.getDriverName());
                //System.out.println("Driver version: " + dm.getDriverVersion());
                //System.out.println("Product name: " + dm.getDatabaseProductName());
                //System.out.println("Product version: " + dm.getDatabaseProductVersion());
                //System.out.println("MESSAGE=="+message);

                //IF VALUE[0]=='C.S. TIME'
                // timecal();


                //long millis=System.currentTimeMillis()-24*60*60*1000;//yesterday
                long millis=System.currentTimeMillis();
                java.sql.Date date=new java.sql.Date(millis);

                System.out.println("today="+String.valueOf(date));

                String timegap = "";
                String timediff = "";
                HashMap<Integer, Date> hs = new HashMap<>();

                if(value[0].equals("C.S. TIME"))
                {
                    if(hs.containsKey(Integer.parseInt(machineid)))
                    {
                        timegap = timecal(String.valueOf(hs.get(Integer.parseInt(machineid))), String.valueOf(date));
                        hs.put(Integer.parseInt(machineid), date);
                    }
                    else{
                        hs.put(Integer.parseInt(machineid), date);
                    }

                    /*ps = conn.prepareStatement("  SELECT TOP (1) [ID]\n" +
                            "      ,[NAME]\n" +
                            "      ,[VALUE]\n" +
                            "      ,[TIME]\n" +
                            "      ,[TIMEGAP]\n" +
                            "  FROM machine_status\n" +
                            "  WHERE [NAME]='C.S. TIME'\n" +
                            "  AND TIME <= ? \n" +
                            "  ORDER BY VALUE DESC ");
                    ps.setString(1, String.valueOf(date));
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                         timediff = rs.getString("VALUE");
                    }

                    if(!timediff.equals("")) {
                        System.out.println("timediff=" + timediff);
                        timegap = timecal(timediff, value[1]);
                    }*/

                }

                //ps = conn.prepareStatement("INSERT INTO machine_status VALUES (?, ?, ?, SYSDATETIME(), ?, ? )");


                ps = conn.prepareStatement("INSERT INTO machine_status (machineid, name ,value ,time ,timegap, work) VALUES ( ?, ?, ?, SYSDATE(), ?, ? )");

                for(String c: value) {
                    System.out.println(c);
                }
                ps.setInt(1, Integer.parseInt(machineid));
                if(value[0]=="PROGRAM" || value[0].equals("PROGRAM") || value[0].equals("STATUS") || value[0]=="STATUS") {
                    ps.setString(2, value[0]);
                }
                else{
                    ps.setString(2, value[1]);
                }
                System.out.println("VALUE.length==="+value.length);

               try{
                   if(value[2]==null)
                   {
                       ps.setString(3, "");
                   }else {
                       ps.setString(3, value[2]);
                   }
               }
               catch (IndexOutOfBoundsException ex){
                    System.out.println("  " + ex);
                    ps.setString(3, value[1]);
                }

                ps.setString(4, timegap);
                ps.setString(5, recentwork);

                ps.executeUpdate();
                conn.commit();
                System.out.println("Successfully Inserted to DB");
            }
            else{
                System.out.println("Connection is Null");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("SQLException for rollback");
            rollback(conn);
        } finally {
            System.out.println("Finally before closed to DB");
            close(conn);
            System.out.println("Finally after closed to DB");
            try {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("Connection closed from DB");
                    conn.close();
                    System.out.println("Connection closed from DB");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("SQL EXCEPTION: " +ex);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Making a new Connection");
        Connection conn = null;
        System.out.println("Checking connection is null: "+conn);
        //String connectionUrl = dburl;
        System.out.println("Checking dburl: "+dburl);
        conn = DriverManager.getConnection(dburl);


        System.out.println("Checking connection is not null: "+conn);
        System.out.println("Connected to database.");
        return conn;
    }

    public static void close(Connection connection)
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public static void close(Statement st)
    {
        try
        {
            if (st != null)
            {
                st.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs)
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection connection)
    {
        try
        {
            if (connection != null)
            {
                connection.rollback();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static String pickLatestFileFromDownloads(String url) {


        String downloadFolder = url;

        File dir = new File(downloadFolder);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("There is no file in the folder");
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        String k = lastModifiedFile.toString();

        System.out.println(lastModifiedFile);
        Path p = Paths.get(k);
        String file = p.getFileName().toString();
        return file;

    }




}
