package it.polito.tdp.crimes.model;

import java.time.Month;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		for (Month s : m.getMesi())
			System.out.println(s);

	}

}
