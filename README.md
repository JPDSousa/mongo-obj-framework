# Simple Mongo Object Framework

Simple Mongo Object Framework (SMOF) is an ORM built in Java for [MongoDB](https://www.mongodb.com/). Smof relieves the burden of dealing with object serialization and deserialization, as well as managing such objects in a data store. Furthermore, in order to achieve optimal performance, Smof caches objects through the [Guava cache](https://github.com/google/guava/wiki/CachesExplained) and uses [ByteBuddy](http://bytebuddy.net) to lazy load objects from the database. Unlike other MongoDB ORMs, Smof is able to deal with complex object hierarchy schemas.

## Example

So, consider this simple Bottle class:
```java
public static class Bottle extends AbstractElement {

		private static final String CAPACITY = "capacity";
		private static final String AMOUNT = "liquid_amount";
		private static final String LIQUID = "liquid";

		@SmofString(name = LIQUID)
		private String liquid;

		@SmofNumber(name = AMOUNT)
		private double amount;

		@SmofNumber(name = CAPACITY)
		private double capacity;

		public Bottle(String liquid, double capacity) {
			this(liquid, capacity, 0.0);
		}

		@SmofBuilder
		public Bottle(@SmofParam(name=LIQUID) String liquid,
				@SmofParam(name = CAPACITY) Double capacity,
				@SmofParam(name = AMOUNT) Double amount) {
			this.liquid = liquid;
			this.capacity = capacity;
			this.amount = amount;
		}

		public boolean isFull() {
			return capacity == amount;
		}

		public double fill(Double amount) {
			final double left = capacity-amount;
			if(left < amount) {
				this.amount = capacity;
				return amount-left;
			}
			this.amount += amount;
			return left-amount;
		}
}
```

In order to perform some write operations (insert, update), all we have to do is:
```java
public static void main(String[] args) {
		//create the smof object with host, port and database name
		final Smof smof = Smof.create("localhost", 27017, "myDB");
		//create a new bottle
		final Bottle bottle = new Bottle("water", 1.0);
		//create a collection and map it to a type
		smof.createCollection("bottles", Bottle.class);
		//saves the bottle
		smof.insert(bottle);
		//fill the bottle
		bottle.fill(0.5);
		//update the object on the database
		smof.update(Bottle.class).fromElement(bottle);

		smof.close();
}
```

The API is quite straightforward as this example shows. Check the Getting Started section for further information.

### That's how crazy it can get?

Not at all! This is by far the simplest scenario. With smof you can:
* **Have multiple subtypes pointing to the same collection** 
	* `Aa` and `Ab`both implement `A`
	* You map `A` to collection `ColA`
	* objects of type `Aa` and `Ab` are automatically saved to `ColA`
* **Use referencing**
	* Type `A` is mapped to `ColA` and `B` is mapped to `ColB`
	* `A` has a reference to `B`
	* When you store `A`, `B` is automatically stored in `ColB` (how cool/useful is that?)
* **Lazy-load**
	* Crazy models like: `A` references `B` that references `C` that references `D` .... that references `Z`
	* When you load `A` from the database, `B` (and so forth) will only be loaded the first time you access it!
* **So much more!** (see [Wiki](https://github.com/JPDSousa/mongo-obj-framework/wiki))

## More information

Despite being simple, Smof carries a lot of elaborate features that need to be documented in great detail, so i added a [Wiki](https://github.com/JPDSousa/mongo-obj-framework/wiki) which is encouraged if your planning in using the framework.

## Contributing

Smof is an open project, thus it welcomes new collaborators. Follow the general [Github Flow](https://guides.github.com/introduction/flow/index.html) to contribute to the project! Also, feel free to contact me :)
