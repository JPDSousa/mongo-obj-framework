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
package org.smof.bson.codecs.number;

import static java.lang.String.format;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

class FloatCodec implements Codec<Float> {
	
    @Override
    public void encode(final BsonWriter writer, final Float value, final EncoderContext encoderContext) {
        writer.writeDouble(value);
    }

    @Override
    public Float decode(final BsonReader reader, final DecoderContext decoderContext) {
        double value = decodeDouble(reader);
        if (value < -Float.MAX_VALUE || value > Float.MAX_VALUE) {
            throw new BsonInvalidOperationException(format("%s can not be converted into a Float.", value));
        }
        return (float) value;
    }

    @Override
    public Class<Float> getEncoderClass() {
        return Float.class;
    }
    
    private static double decodeDouble(final BsonReader reader) {
        double doubleValue;
        BsonType bsonType = reader.getCurrentBsonType();
        switch (bsonType) {
            case INT32:
                doubleValue = reader.readInt32();
                break;
            case INT64:
                long longValue = reader.readInt64();
                doubleValue = longValue;
                if (longValue != (long) doubleValue) {
                    throw invalidConversion(Double.class, longValue);
                }
                break;
            case DOUBLE:
                doubleValue = reader.readDouble();
                break;
            default:
                throw new BsonInvalidOperationException(format("Invalid numeric type, found: %s", bsonType));
        }
        return doubleValue;
    }

    private static  <T extends Number> BsonInvalidOperationException invalidConversion(final Class<T> clazz, final Number value) {
        return new BsonInvalidOperationException(format("Could not convert `%s` to a %s without losing precision", value, clazz));
    }
}
