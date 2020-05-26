package connection;

import java.sql.*;

public class DBConnection {

    private Connection conn = null;


    public DBConnection(String url) throws SQLException {
        conn = DriverManager.getConnection(url);
    }

    public void close() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * A method to send select statement to the underlying DBMS (e.g., "select * from Table1")
     * @param query_statement A query to run on the underlying DBMS
     * @return Resultset the query result.
     */
    public ResultSet send_query(String query_statement) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query_statement);

        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
//		finally {
//			// it is a good idea to release
//			// resources in a finally{} block
//			// in reverse-order of their creation
//			// if they are no-longer needed
//
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException sqlEx) { } // ignore
//
//				rs = null;
//			}
//
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException sqlEx) { } // ignore
//
//				stmt = null;
//			}
//		}
        return rs;
    }

    // TODO Write a method to insert new data into the DBMS


    public boolean check_connection() throws SQLException {
        return conn.isValid(1);		// Checks connection every 10 seconds
    }


    public boolean insert_data(String table, String data) throws SQLException {
        try {
            System.out.println(data);

            String query = "INSERT INTO " + table + " VALUES(";
            String split_data = "";

            if(table.equalsIgnoreCase("product") || table.equalsIgnoreCase("productkeyword")) {
                int x = 0;

                while (true) {
                    split_data += data.charAt(x);

                    if (data.charAt(x) == ',') {
                        break;
                    }
                    x++;
                }

                query += split_data;
                x++;
                split_data = "";

                for (int i = x; i < data.length(); i++) {
                    if (data.charAt(i) != ',') {
                        split_data += data.charAt(i);
                    } else {
                        query = query + "\"" + split_data + "\"";
                        split_data = "";

                        if (i != data.length() - 1) {
                            query += ",";
                        }
                    }
                }

                query = query + "\"" + split_data + "\"" + ");";

            } else if(table.equalsIgnoreCase("ExternalSupplier")){
                int x = 0;
                split_data = "";
                for(;x<data.length();x++){

                    if(data.charAt(x) == ','){

                        query += "\"" + split_data + "\"" + ",";
                        split_data = "";
                        continue;
                    }
                    split_data += data.charAt(x);
                    if(x == data.length()-1){
                        query +=  "\"" + split_data + "\"";
                    }
                }
                query += ");";
            }
            else if(table.equalsIgnoreCase("Website")){
                int counter = 0; //has to be 4
                int x = 0;
                split_data = "";
                for(; x < data.length(); x++){

                    if(data.charAt(x) == ','){
                        counter++;
                        if(counter == 4){
                            query +=  split_data  + ",";
                        }
                        else {
                            query += "\"" + split_data + "\"" + ",";
                        }
                        split_data = "";
                        continue;
                    }
                    split_data += data.charAt(x);
                    if(x == data.length()-1){
                        query +=  "\"" + split_data + "\"";
                    }
                }
                query += ");";
            }
            else if(table.equalsIgnoreCase("WebsitePhone")){
                int x = 0;
                split_data = "";
                for(;x<data.length();x++){

                    if(data.charAt(x) == ','){

                        query += "\"" + split_data + "\"" + ",";
                        split_data = "";
                        continue;
                    }
                    split_data += data.charAt(x);
                    if(x == data.length()-1){
                        query +=  "\"" + split_data + "\"";
                    }
                }
                query += ");";
            }
            else if(table.equalsIgnoreCase("Sell")){
                int counter = 0;
                int x = 0;
                split_data = "";
                for(;x<data.length();x++){

                    if(data.charAt(x) == ',') {
                        counter++;
                        if(counter == 0) {
                            query += split_data + "\"" ;
                            split_data = "";
                            continue;
                        }
                        else if(counter == 3) {
                            query += "'" + split_data + "'" + ",";
                            split_data = "";
                            continue;
                        }

                        query += "\"" + split_data + "\"" + ",";
                        split_data = "";
                        continue;
                    }
                    split_data += data.charAt(x);

                    if(x == data.length()-1) {
                        query +=  "\"" + split_data + "\"";
                    }
                }
                query += ");";
            }

            System.out.println(query);

            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("FOLLOW PRIMARY KEY CONSTRAINTS");
            return false;

        } catch (SQLException e) {
            System.out.println("ERROR : " + e.getStackTrace());
            return false;
        }
    }

    public void delete_data(String table,String data) throws SQLException {
        String split_data = "";
        String query ="DELETE FROM " + table + " WHERE ";

        if(table.equalsIgnoreCase("Product")){
            query += "Id=" + data + ";";
        } else if(table.equalsIgnoreCase("productKeyword")) {
            int x = 0;

            while (true) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);
                x++;
            }

            query += "productId=" + split_data + " ";
            x++;
            split_data = "";

            split_data = data.substring(x,data.length());

            query += "AND typeKeyword=\"" + split_data + "\";";

        } else if (table.equalsIgnoreCase("Website")) {
            int x = 0;
            split_data = data.substring(0, data.length());
            query += "URL= " + "\"" + split_data + "\"";

            //System.out.println(query);

        } else if (table.equalsIgnoreCase("WebsitePhone")) {
            int x = 0;

            for (; x < data.length(); x++) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);
            }
            x++;
            query += "URL= " + "\"" + split_data + "\"";
            //System.out.println(query);
            split_data = "";

            for (; x < data.length(); x++) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);
            }

            query += " AND " + "Phone_Number= " + "\"" + split_data + "\"";

            //System.out.println(query);

        } else if (table.equalsIgnoreCase("ExternalSupplier")) {
            int x = 0;

            for (; x < data.length(); x++) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);
            }
            x++;
            query += "URL= " + "\"" + split_data + "\"";
            //System.out.println(query);
            split_data = "";

            for (; x < data.length(); x++) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);
            }
            x++;
            query += " AND " + "name= " + "\"" + split_data + "\"";

        } else if (table.equalsIgnoreCase("sell")) {
            int x = 0;

            for (; x < data.length(); x++) {
                if (data.charAt(x) == ',') {
                    break;
                }
                split_data += data.charAt(x);

            }
            query += "PID=" + split_data;
            x += 1;
            split_data = "";
            split_data = "\"" + data.substring(x, data.length()) + "\"";


            query += " AND " + "WURL= " + split_data;
        }

        System.out.println(query);

        Statement statement = conn.createStatement();
        statement.executeUpdate(query);
    }
}