/**
 * Need to change all of the tables -> make as HW2
 * Need to change insert in website
 */

package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import connection.DBConnection;

public class Main {

//	public static void instantiateJDBC() {
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


    public static void processCommand(DBConnection connection, String[] args) throws SQLException {
        if (args.length == 2) {
            // it must be check command
            if (args[1].equalsIgnoreCase("check")) {
                // TODO implement your logic here

                if(connection.check_connection())
                    System.out.println("Connection established");
                else
                    System.out.println("Connection failed");

            } else {
                System.out.println("Unexpected command!");
            }


        } else if (args.length == 3) {
            // it can be 'add products' or 'query'
            if (args[1].equalsIgnoreCase("add") && args[2].equalsIgnoreCase("products")) {
                Scanner scanner = new Scanner(System.in);
                boolean check = true;
                ArrayList<Integer> ids = new ArrayList<>();


                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String tempId = "";

                    for(int i = 0; i < line.length(); i++) {
                        if(line.charAt(i) == ',')
                            break;
                        else
                            tempId += line.charAt(i);
                    }

                    if(!scanner.hasNextLine()) {
                        line = line.substring(0,line.length() - 5);
                    }

                    check = connection.insert_data("product",line);

                    if(check)
                        ids.add(Integer.parseInt(tempId));
                    else  {
                        for(int i = 0; i < ids.size(); i++) {
                            System.out.println(ids.get(i));
                            connection.delete_data("product",String.valueOf(ids.get(i)));
                        }
                        System.out.println("Bulk data cannot be processed");
                        return;
                    }
                }





            } else if (args[1].equalsIgnoreCase("query")) {
                if(args[2].equalsIgnoreCase("1")) {
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
                        System.out.println(set.getString("name"));
                    }
                }
                else if(args[2].equalsIgnoreCase("3")){
                    ResultSet set = connection.send_query("select *\n" +
                            "from product P\n" +
                            "where NOT EXISTS (select S.PID\n" +
                            "\t\t\t\t from sell S\n" +
                            "\t\t\t\t where S.PID = P.ID); ");

                    while(set.next()) {
                        System.out.println(set.getString("Id") + "|" + set.getString("name") + "|" + set.getString("description") + "|" + set.getString("brandname") );
                    }
                }

            } else {
                System.out.println("Unexpected command!");
            }
        }

        else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("add")) {
                if (args[2].equalsIgnoreCase("product")) {
                    connection.insert_data("product",args[3]);


                } else if (args[2].equalsIgnoreCase("productKeyword")) {
                    connection.insert_data("productKeyword", args[3]);

                } else if (args[2].equalsIgnoreCase("website")) {
                    connection.insert_data("website", args[3]);

                } else if (args[2].equalsIgnoreCase("websitephone")) {
                    connection.insert_data("websitephone", args[3]);

                } else if (args[2].equalsIgnoreCase("externalsupplier")) {
                    connection.insert_data("externalsupplier", args[3]);

                } else if (args[2].equalsIgnoreCase("sell")) {
                    connection.insert_data("sell", args[3]);
                } else {
                    System.out.println("Unexpected command!");
                }

            } else if(args[1].equalsIgnoreCase("delete")) {
                if (args[2].equalsIgnoreCase("product")) {
                    connection.delete_data("product",args[3]);

                } else if (args[2].equalsIgnoreCase("productKeyword")) {
                    connection.delete_data("productKeyword",args[3]);
                } else if (args[2].equalsIgnoreCase("website")) {
                    // TODO implement your logic here
                } else if (args[2].equalsIgnoreCase("websitephone")) {
                    // TODO implement your logic here
                } else if (args[2].equalsIgnoreCase("externalsupplier")) {
                    // TODO implement your logic here
                } else if (args[2].equalsIgnoreCase("sell")) {
                    // TODO implement your logic here
                } else {
                    System.out.println("Unexpected command!");
                }

            } else {
                System.out.println("Unexpected command!");
            }

        } else {
            System.out.println("Unexpected command!");
        }
    }


    public static String readConfig() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Esat\\IdeaProjects\\untitled2\\src\\properties.conf"));
        try {
            String ip = reader.readLine();
            String port = reader.readLine();
            String username = reader.readLine();
            String password = reader.readLine();

            String url = String.format("jdbc:mysql://%s/%s?user=%s&password=%s",
                    ip, port, username, password);
            return url;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String[] arguments = {"1", "query","3"};


        DBConnection database_connection = null;

//		instantiateJDBC();

//		if(args.length < 2) {
//			System.err.println("Wrong number of arguments!");
//		}

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
