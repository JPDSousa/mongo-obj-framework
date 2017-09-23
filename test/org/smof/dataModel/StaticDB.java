package org.smof.dataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("javadoc")
public final class StaticDB {

	public static final String MODELS = "models";
	public static final String BRANDS = "brands";
	public static final String GUITARS = "guitars";
	
	public static final String GUITARS_PIC_BUCKET = "guitarsPics";
	
	public static final Brand BRAND_1 = Brand.create("Gibson", new Location("Nashville", "USA"), Arrays.asList("Me", "Myself", "I"));
	
	public static final Model MODEL_1 = Model.create("Manhattan", "Tyler", 1000, BRAND_1, Arrays.asList("red", "blue"));
	public static final Model MODEL_2 = Model.create("BeeGees", "Tyler", 5463, BRAND_1, Arrays.asList("sunburst", "ebony"));
	
	public static final Guitar GUITAR_3 = Guitar.create(MODEL_1, TypeGuitar.ACOUSTIC, 0, 1989);
	public static final Guitar GUITAR_2 = Guitar.create(MODEL_1, TypeGuitar.CLASSIC, 0, 1960);
	public static final Guitar GUITAR_1 = Guitar.create(MODEL_2, TypeGuitar.ELECTRIC, 1, 0);
	public static final List<Guitar> ALL_GUITARS = new ArrayList<>();
	
	static {
		ALL_GUITARS.add(GUITAR_1);
		ALL_GUITARS.add(GUITAR_2);
		ALL_GUITARS.add(GUITAR_3);
	}
}
