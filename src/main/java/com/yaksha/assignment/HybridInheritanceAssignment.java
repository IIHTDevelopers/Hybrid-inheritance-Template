package com.yaksha.assignment;

//Interface representing the ability to fly
interface Flyable {
	void fly(); // Method for flying
}

//Interface representing the ability to run
interface Runnable {
	void run(); // Method for running
}

//Animal class - Base class in the hierarchy
class Animal {
	String species;

	public Animal() {
		species = "Unknown species"; // Default species
	}

	public void speak() {
		System.out.println("The animal makes a sound.");
	}
}

//Dog class - Inherits from Animal, and implements Flyable and Runnable
class Dog extends Animal implements Flyable, Runnable {

	@Override
	public void speak() {
		System.out.println("The dog barks.");
	}

	@Override
	public void fly() {
		System.out.println("The dog tries to fly, but it can't.");
	}

	@Override
	public void run() {
		System.out.println("The dog runs fast.");
	}
}

public class HybridInheritanceAssignment {
	public static void main(String[] args) {
		Dog dog = new Dog(); // Creating a Dog object
		dog.speak(); // Should print "The dog barks." as overridden in Dog
		dog.fly(); // Should print "The dog tries to fly, but it can't."
		dog.run(); // Should print "The dog runs fast."
	}
}
