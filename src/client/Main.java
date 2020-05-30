
package client;

import java.io.*;
import java.nio.CharBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import connection.DBConnection;

public class Main {

	public static void instantiateJDBC() {
        try {
             Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=convertToNull",
                    "root", "root");
            Statement s = connection.createStatement();
            int r = s.executeUpdate("create database if not exists web_crawler");
            s.executeUpdate("use web_crawler");



        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void processCommand(DBConnection connection, String[] args) throws SQLException {
        if (args.length == 1) {
            // it must be check command
            if (args[0].equalsIgnoreCase("check")) {
                if(connection.isConnected())
                    System.out.println("connection established");
                else
                    System.out.println("connection failed");

            } else {
                System.out.println("Unexpected command1!");
            }


        } else if (args.length == 2) {
            // it can be 'add products' or 'query'
            if (args[0].equalsIgnoreCase("add") && args[1].equalsIgnoreCase("products")) {
                Scanner scanner = new Scanner(System.in);
                ArrayList<String> lines = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                while (true) {
                    String line = scanner.nextLine();

                    if (line.isEmpty())
                        break;

                    lines.add(line);
                }
                for (int i = 0 ; i < lines.size() ; i ++) {

                    ids.add(Integer.parseInt(lines.get(i).split(",")[0]));

                }
                int firstGarbage = 0;
                boolean check = true;
                for (int j = 0 ; j < lines.size() ; j++) {

                    check = connection.insertData("product", lines.get(j));

                    if (!check) {
                        firstGarbage = j;
                        break;
                    }

                }

                for (int k = 0; k < firstGarbage ; k ++) {
                    connection.deleteData("product",String.valueOf(ids.get(k)));
                }




            } else if (args[0].equalsIgnoreCase("query")) {
                if(args[1].equalsIgnoreCase("1")) {
                    // Find the most popular product in all websites. I.E., the product sold in the highest number of websites

                    ResultSet set = connection.send_query("SELECT P.name\n" +
                            "FROM product P \n" +
                            "WHERE P.Id = (SELECT S.PID\n" +
                            "\t\t\t  FROM sell S \n" +
                            "\t\t\t  GROUP BY S.PID \n" +
                            "\t\t\t  ORDER BY COUNT(1) desc\n" +
                            "\t\t\t  LIMIT 1\n" +
                            "\t\t\t  );");

                    while(set.next()) {
                        System.out.println(set.getString("Id") + "," + set.getString("name") + "," + set.getString("description") + "," + set.getString("brandname"));
                    }
                }
                else if(args[1].equalsIgnoreCase("2")){
                    ResultSet set = connection.send_query("select S.PID,S.WURL,S.date\n" +
                            "from sell S\n" +
                            "group by S.PID,S.WURL\n" +
                            "order by  S.PID asc , S.Initial_price-S.Discounted_price asc ; ");

                    while(set.next()) {
                        System.out.println(set.getString("PID") + "," + set.getString("WURL") + "," + set.getString("Date"));
                    }
                }
                else if(args[1].equalsIgnoreCase("3")) {
                    ResultSet set = connection.send_query("select *\n" +
                            "from product P\n" +
                            "where NOT EXISTS (select S.PID\n" +
                            "\t\t\t\t from sell S\n" +
                            "\t\t\t\t where S.PID = P.ID); ");

                    while(set.next()) {
                        System.out.println(set.getString("Id") + "," + set.getString("name") + "," + set.getString("description") + "," + set.getString("brandname") );
                    }
                }


            } else {
                System.out.println("Unexpected command2!");
            }
        }

        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equalsIgnoreCase("product")) {
                    connection.insertData("product",args[2]);

                } else if (args[1].equalsIgnoreCase("productKeyword")) {
                    connection.insertData("productKeyword", args[2]);

                } else if (args[1].equalsIgnoreCase("website")) {
                    connection.insertData("website", args[2]);

                } else if (args[1].equalsIgnoreCase("websitephone")) {
                    connection.insertData("websitephone", args[2]);

                } else if (args[1].equalsIgnoreCase("externalsupplier")) {
                    connection.insertData("externalsupplier", args[2]);

                } else if (args[1].equalsIgnoreCase("sell")) {
                    connection.insertData("sell", args[2]);
                } else {
                    System.out.println("Unexpected command3!");
                }

            } else if(args[0].equalsIgnoreCase("delete")) {
                if (args[1].equalsIgnoreCase("product")) {
                    connection.deleteData("product",args[2]);

                } else if (args[1].equalsIgnoreCase("productKeyword")) {
                    connection.deleteData("productKeyword",args[2]);
                } else if (args[1].equalsIgnoreCase("website")) {
                    connection.deleteData("website",args[2]);
                } else if (args[1].equalsIgnoreCase("websitephone")) {
                    connection.deleteData("websitephone",args[2]);
                } else if (args[1].equalsIgnoreCase("externalsupplier")) {
                    connection.deleteData("externalsupplier",args[2]);
                } else if (args[1].equalsIgnoreCase("sell")) {
                    connection.deleteData("sell",args[2]);
                } else {
                    System.out.println("Unexpected command4!");
                }

            } else {
                System.out.println("Unexpected command5!");
            }

        } else {
            System.out.println("Unexpected command6!");
        }
    }


    public static String readConfig() throws IOException {
        InputStream inputStream =  Main.class.getResourceAsStream("/properties.conf");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            String ip = reader.readLine();
            String port = reader.readLine();
            String username = reader.readLine();
            String password = reader.readLine();

            String url = String.format("jdbc:mysql://%s/%s?user=%s&password=%s",
                    ip, port, username, password);
            return url;
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            reader.close();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        //String[] arguments = {"add","sell","2,something5,1999-10-10 10:10:10,25,5"};  //  deneme


        DBConnection database_connection = null;

		instantiateJDBC();

		if(args.length < 1) {
			System.err.println("Wrong number of arguments!");
		}

        String url = readConfig();

        try {
            database_connection = new DBConnection(url);

            processCommand(database_connection, args);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(database_connection != null){
                database_connection.close();
            }
        }

    }
}
