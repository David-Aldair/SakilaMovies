package com.pluralsight.dao;

import com.pluralsight.models.Actor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActorDao {

    //we need a DAO so we know how to connect to the DB and get connections from the pool
    private DataSource dataSource;

    //constructor so when we create a dao, it has the datasource passed in so it knows how to connect to the DB
    public ActorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //this is the method that knows how to get all actors from the DB
    public ArrayList<Actor> getAllActors() {

        //start the empty list
        ArrayList<Actor> actors = new ArrayList<Actor>();

        try (
                //get a connection from the pool
                Connection connection = this.dataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            Actor_ID,
                            First_Name,
                            Last_Name
                        FROM
                            Actor;
                        """
                );

        ) {

            //get the result set and loop over it to create java objects and ad them to the list
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    //create the new product from the results returned from the database
                    Actor newActor = new Actor(
                            resultSet.getInt("Actor_ID"),
                            resultSet.getString("First_Name"),
                            resultSet.getString("Last_Name")
                    );

                    //add the actor to the list
                    actors.add(newActor);
                }
            } catch (Exception e) {
                System.out.println("stuff hit the fan" + e);
            }

        } catch (Exception e) {
            System.out.println("Could not get all actors" + e);
            System.exit(1);
        }

        //return the list
        return actors;
    }

    //this is the method that knows how to get all actors from the database
    public Actor getActorByID(int actorID) {

        Actor actor = null;

        try (
                //get a connection from the pool
                Connection connection = this.dataSource.getConnection();

                //create the prepared statement using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            Actor_ID,
                            First_Name,
                            Last_Name
                        FROM
                            Actor
                        WHERE
                            Actor_ID = ?
                        """
                );

        ) {

            preparedStatement.setInt(1, actorID);

            //get the result set and loop over it to create java objects and add them to the list
            try (ResultSet results = preparedStatement.executeQuery()) {

                while (results.next()) {

                    //create the new product from the results returned form the DB
                    actor = new Actor(
                            results.getInt("Actor_ID"),
                            results.getString("First_Name"),
                            results.getString("Last_Name")
                    );

                }
            } catch (SQLException e) {
                System.out.println("stuff hit the fan" + e);
            }

        } catch (SQLException e) {
            System.out.println("Could not get all the actors");
            System.exit(1);
        }

        //return the actor
        return actor;
    }
}
