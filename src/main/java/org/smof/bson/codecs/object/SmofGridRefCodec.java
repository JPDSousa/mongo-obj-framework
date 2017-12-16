package org.smof.bson.codecs.object;

import static org.smof.collection.UpdateOperators.SET;
import static com.google.common.base.Preconditions.*;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonElement;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.smof.annnotations.SmofObject;
import org.smof.bson.codecs.SmofCodec;
import org.smof.bson.codecs.SmofEncoderContext;
import org.smof.collection.SmofDispatcher;
import org.smof.field.PrimaryField;
import org.smof.field.SmofField;
import org.smof.gridfs.SmofGridRef;
import org.smof.gridfs.SmofGridRefFactory;
import org.smof.parsers.SmofParser;

import com.mongodb.client.gridfs.model.GridFSFile;

@SuppressWarnings("javadoc")
public class SmofGridRefCodec implements SmofCodec<SmofGridRef> {

	private final SmofDispatcher dispatcher;

	public SmofGridRefCodec(SmofParser topParser) {
		super();
		this.dispatcher = topParser.getDispatcher();
	}

	@Override
	public void encode(BsonWriter writer, SmofGridRef value, EncoderContext encoderContext) {
		checkArgument(value.getBucketName() != null, "Must specify a bucket name");
		if(!nullIfEmpty(writer, value) && value.getId() == null) {
			writer.pipe(new BsonDocumentReader(insert(value)));
		}
	}

	@Override
	public void encode(BsonWriter writer, SmofGridRef value, SmofEncoderContext context) {
		final SmofField field = context.getField();
		if(!nullIfEmpty(writer, value) && value.getId() == null) {
			// TODO this only works for primary fields. What if secondary field????
			if(field instanceof PrimaryField) {
				encodeAsPrimaryField(writer, value, context, (PrimaryField) field);
			}
			else {
				encode(writer, value, EncoderContext.builder().build());
			}
		}
	}

	private void encodeAsPrimaryField(BsonWriter writer, SmofGridRef value, SmofEncoderContext context,
			PrimaryField field) {
		final SmofObject annotation = field.getSmofAnnotationAs(SmofObject.class);
		if(value.getBucketName() == null) {
			value.setBucketName(annotation.bucketName());
		}
		if(!annotation.preInsert()) {
			context.addPosInsertionHook(() -> {
				final BsonDocument doc = new BsonDocument(field.getName(), insert(value));
				return new BsonElement(SET.getOperator(), doc);
			});
		}
		else {
			writer.pipe(new BsonDocumentReader(insert(value)));
		}
	}

	private BsonDocument insert(SmofGridRef value) {
		dispatcher.insert(value);
		return new BsonDocument()
				.append(SmofGridRef.ID, new BsonObjectId(value.getId()))
				.append(SmofGridRef.BUCKET, new BsonString(value.getBucketName()));
	}

	private boolean nullIfEmpty(BsonWriter writer, SmofGridRef value) {
		final boolean isEmpty = value.isEmpty();
		if(isEmpty) {
			writer.writeNull();
		}
		return isEmpty;
	}

	@Override
	public Class<SmofGridRef> getEncoderClass() {
		return SmofGridRef.class;
	}

	@Override
	public SmofGridRef decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		final ObjectId id = reader.readObjectId(SmofGridRef.ID);
		final String bucketName = reader.readString(SmofGridRef.BUCKET);
		final SmofGridRef ref = SmofGridRefFactory.newFromDB(id, bucketName);
		final GridFSFile file = dispatcher.loadMetadata(ref);
		ref.putMetadata(file.getMetadata());
		return ref;
	}

}
