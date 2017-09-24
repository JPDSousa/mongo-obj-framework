package org.smof.dataModel;

import java.time.LocalDate;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofIndex;
import org.smof.annnotations.SmofIndexField;
import org.smof.annnotations.SmofIndexes;
import org.smof.annnotations.SmofParam;
import org.smof.element.Element;
import org.smof.index.IndexType;

@SuppressWarnings("javadoc")
@SmofIndexes({
	@SmofIndex(fields = {@SmofIndexField(name = "name", type = IndexType.TEXT)}, unique = true)
})
public interface Owner extends Element {
	
	String NAME = "name";
	String BIRTHDAY = "b_day";
	
	@SmofBuilder
	static Owner create(
			@SmofParam(name = NAME) String name,
			@SmofParam(name = BIRTHDAY) LocalDate birthday) {
		return new OwnerImpl(name, birthday);
	}
	
	String getName();
	
	LocalDate getBirthday();

}
