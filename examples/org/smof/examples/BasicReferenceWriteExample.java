package org.smof.examples;

import java.util.Arrays;

import org.smof.collection.Smof;
import org.smof.examples.dataModel.Brand;
import org.smof.examples.dataModel.Guitar;
import org.smof.examples.dataModel.Location;
import org.smof.examples.dataModel.Model;
import org.smof.examples.dataModel.TypeGuitar;

/**
 * Smof example of referencing with basic insert operations
 * 
 * @author Joao
 *
 */
public class BasicReferenceWriteExample {

	/**
	 * Main
	 * @param args args
	 */
	public static void main(String[] args) {
		//create the smof object with host, port and database name
		final Smof smof = Smof.create("localhost", 27017, "myDB");
		
		//Calling load collection on a non-existent collection is fine
		//(i.e. the same as calling createCollection)
		smof.loadCollection("guitars", Guitar.class);
		smof.loadCollection("brands", Brand.class);
		smof.loadCollection("models", Model.class);
		
		//creates a brand, two models and three guitars
		final Brand brand = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("Me", "Myself", "I"));
		final Model model1 = Model.create("Manhattan", "Tyler", 1000, brand, Arrays.asList("red", "blue"));
		final Model model2 = Model.create("BeeGees", "Tyler", 5463, brand, Arrays.asList("sunburst", "ebony"));
		final Guitar guitar1 = Guitar.create(model2, TypeGuitar.ELECTRIC, 1, 0);
		final Guitar guitar2 = Guitar.create(model1, TypeGuitar.ELECTRIC, 2, 0);
		final Guitar guitar3 = Guitar.create(model1, TypeGuitar.ELECTRIC, 3, 0);
		
		//by inserting guitar1, model2 and brand are automatically insert
		//since guitar1 references model2, and model2 references brand
		smof.insert(guitar1);
		//same logic -> both brand an model1 are inserted before guitar
		//but all these additional (though required) steps are performed
		//under the hood
		smof.insert(guitar2);
		smof.insert(guitar3);
		
		//a little sneak peak on read operations
		System.out.println("Brands:");
		smof.find(Brand.class).results().stream().forEach(System.out::println);
		System.out.println("Models:");
		smof.find(Model.class).results().stream().forEach(System.out::println);
		System.out.println("Guitars:");
		smof.find(Guitar.class).results().stream().forEach(System.out::println);
		
		//remember to close the connection
		smof.close();
	}

}
