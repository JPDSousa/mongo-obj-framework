package org.smof.annnotations;

/*******************************************************************************
 * Copyright (C) 2017 Joao
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * </p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *<p> 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * </p>
 ******************************************************************************/

@SuppressWarnings("javadoc")
public enum OperatorType {
    /**
    * $gt represents greaterThan.  
    **/
    greaterThan("$gt"),
    /**
    * $lt represents lessThan.
    */
    lessThan("$lt"),
    /**
    * $gte represents greaterThanEqual.
    */
    greaterThanEquals("$gte"),
    /**
    * $lte represents lessThanEquals.
    */
    lessThanEquals("$lte"),
    /**
    * $exists represents exists.
    */
    exists("$exists"),
	/**
	 * its for default.
	 */
	none("none");

  private final String mongoToken;

  private OperatorType(String mongoToken) {
    this.mongoToken = mongoToken;
  }

  /**
   * 
   * @param indexType.
   * @return OperatorType
   */
  public static OperatorType parse(String indexType) {
    for (OperatorType type : values()) {
      if (type.mongoToken.equals(indexType)) {
        return type;
      }
    }
    return null;
  }

  public String getMongoToken() {
    return mongoToken;
  }

}
