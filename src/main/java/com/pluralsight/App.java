package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    //scanner
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {


        //did we pass in a username and password
        //if not, the application must die
        if (args.length != 2) {
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we don't have a username and password from the command line
            System.exit(1);
        }
        //get the username and password from args[]
        String username = args[0];
        String password = args[1];

        //get the connection from the datasource
        //create the connection (kinda like opening mySQL Workbench)
        try (

                //create the basic datasource
                BasicDataSource dataSource = new BasicDataSource()
        ){

            //setting it's configuration
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            while (true) {
                System.out.println("""
                        What do you want to do?
                            1)Search actors by last name
                            2)See movies by actor(first and last name)
                            0)Exit
                        """);

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        searchByLastName(dataSource);
                        break;
                    case 2:
                        searchByFullName(dataSource);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice");
                }
            }

        } catch (SQLException e) {

            System.out.println("Something went wrong" + e);
            System.exit(1);
        }
    }

    //method to search by last name
    public static void searchByLastName(BasicDataSource dataSource){

        //ask the user for the last name
        System.out.println("Enter the last name: ");
        String lastName = scanner.nextLine();

        try (

                //get a connection from the data pool
                Connection connection = dataSource.getConnection();

                //create the prepared statement using the passed connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            actor_id,
                            first_name,
                            last_name
                        FROM
                            actor
                        WHERE
                            last_name = ?;
                        """
                )){

            //replace the first ? with the value the user typed
            preparedStatement.setString(1, lastName);

            //running the query that represents the returned rows
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                //if the results return false, no rows exist
                if (!resultSet.next()) {
                    System.out.println("No actors found with that last name.");
                    System.out.println("Press enter to return to main menu");
                    scanner.nextLine();
                    return;
                }

                System.out.println("Actors with last name " + lastName + ":");

                do {

                    int actorID = resultSet.getInt("actor_id");
                    String first = resultSet.getString("first_name");
                    String last = resultSet.getString("last_name");

                    //print the actor
                    System.out.printf(" %d - %s %s%n", actorID, first, last);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            System.out.println("Error searching by last name: " + e.getMessage());
        }
    }

     //method to print the last name the user searches
    public static void searchByFullName(BasicDataSource dataSource){

        System.out.println("Enter the actor's first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.println("Enter the actor's last name: ");
        String lastName = scanner.nextLine().trim();

        try (

                Connection connection = dataSource.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            f.film_id,
                            f.title
                        FROM
                            film f
                        JOIN
                            film_actor fa ON f.film_id = fa.film_id
                        JOIN
                            actor a ON fa.actor_id = a.actor_id
                        WHERE
                            a.first_name = ?
                            AND a.last_name = ?
                        ORDER BY
                            f.title;
                        """
        )){

            //replace the ? with the value the user typed
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {



                //if statement to check if there are any results
                if (!resultSet.next()) {
                    System.out.println("No films found for that actor.");
                    System.out.println("Press enter to return to main menu");
                    scanner.nextLine();
                    return;
                }

                System.out.println("\nFilms featuring " + firstName + " " + lastName + ":");

                //use do while loop to print all the matching films
                do {
                    int filmId = resultSet.getInt("film_id");
                    String title = resultSet.getString("title");

                    System.out.printf("  %d - %s%n", filmId, title);

                //keep looping while there is another row in the database
                } while (resultSet.next());
            }

            } catch (SQLException e) {
            System.out.println("Error searching films for actor: " + e.getMessage());
        }
    }

}
