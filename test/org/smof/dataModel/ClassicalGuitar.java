package org.smof.dataModel;

import org.smof.annnotations.SmofNumber;

@SuppressWarnings("javadoc")
public class ClassicalGuitar extends AcousticGuitar {

	@SmofNumber(name=AGE)
	private int age;
	
	ClassicalGuitar(Model model, int age) {
		super(model, TypeGuitar.CLASSIC);
		this.age = age;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
