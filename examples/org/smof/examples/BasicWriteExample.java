/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.examples;

import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofParam;
import org.smof.annnotations.SmofString;
import org.smof.collection.Smof;
import org.smof.element.AbstractElement;

@SuppressWarnings("javadoc")
public final class BasicWriteExample {

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
		smof.replace(Bottle.class, bottle);
		
		smof.close();
	}

	/**
	 * Represents a fillable bottle
	 * 
	 * @author Joao
	 *
	 */
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
		
		/**
		 * Returns a new empty bottle
		 * 
		 * @param liquid liquid type
		 * @param capacity total capacity
		 */
		public Bottle(String liquid, double capacity) {
			this(liquid, capacity, 0.0);
		}
		
		/**
		 * General constructor
		 * 
		 * @param liquid liquid type
		 * @param capacity total capacity
		 * @param amount liquid amount
		 */
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
}
