package com.pluralsight.models;

public class Actor {

    //properties for the actor
    int actorID;
    String firstName;
    String lastName;

    //constructor to create an instance of the actor
    public Actor(int actorID, String firstName, String lastName) {
        this.actorID = actorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    //to string to print out the product details
    @Override
    public String toString(){
        return "Actor{" +
                "actorID=" + actorID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
