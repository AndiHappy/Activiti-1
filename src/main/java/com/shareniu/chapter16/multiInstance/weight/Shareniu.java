package com.shareniu.chapter16.multiInstance.weight;

public class Shareniu {
	private String name;
	private int weight;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Shareniu [name=" + name + ", weight=" + weight + "]";
	}
	public Shareniu(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}
}
