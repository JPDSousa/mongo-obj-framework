package org.smof.element.field;

import java.lang.annotation.Annotation;

@SuppressWarnings("javadoc")
public enum SmofField {
	
	STRING(SmofString.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofString.class.cast(annotation).name();
		}
	},
	NUMBER(SmofNumber.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofNumber.class.cast(annotation).name();
		}
	},
	DATE(SmofDate.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofDate.class.cast(annotation).name();
		}
	},
	OBJECT(SmofObject.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofObject.class.cast(annotation).name();
		}
	},
	OBJECT_ID(SmofObjectId.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofObjectId.class.cast(annotation).name();
		}
	},
	ARRAY(SmofArray.class) {
		@Override
		protected String getName(Annotation annotation) {
			return SmofArray.class.cast(annotation).name();
		}
	};
	
	private final Class<? extends Annotation> annotClass;
	
	private SmofField(Class<? extends Annotation> annotClass) {
		this.annotClass = annotClass;
	}

	public Class<? extends Annotation> getAnnotClass() {
		return annotClass;
	}

	public static Wrapper getFieldType(Annotation[] annotations) {
		for(Annotation annotation : annotations) {
			for(SmofField f : values()) {
				if(annotation.annotationType().equals(f.getAnnotClass())) {
					return new Wrapper(f.getName(annotation), f);
				}
			}
		}
		return null;
	}
	
	protected abstract String getName(Annotation annotation);

	public static class Wrapper {
		private final SmofField type;
		private final String name;
		
		private Wrapper(String name, SmofField type) {
			this.name = name;
			this.type = type;
		}

		public SmofField getType() {
			return type;
		}

		public String getName() {
			return name;
		}
	}
}
