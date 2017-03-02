package org.smof.test.dataModel;

@SuppressWarnings("javadoc")
public class AcousticGuitar extends AbstractGuitar {

	AcousticGuitar(Model model) {
		super(model, TypeGuitar.ACOUSTIC);
	}

	protected AcousticGuitar(Model model, TypeGuitar type) {
		super(model, type);
	}
}
