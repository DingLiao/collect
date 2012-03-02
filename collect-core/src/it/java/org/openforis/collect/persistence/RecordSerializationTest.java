package org.openforis.collect.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectRecordContext;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.Schema;
import org.openforis.idm.metamodel.xml.InvalidIdmlException;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntitySchema;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.RecordContext;
import org.openforis.idm.model.RecordSerializer;
import org.openforis.idm.model.Time;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.dyuproject.protostuff.runtime.RuntimeFieldFactory;
import com.dyuproject.protostuff.runtime.RuntimeSchema;


public class RecordSerializationTest {
//	private final Log log = LogFactory.getLog(RecordSerializationTest.class);
	
	@Test
	public void testProto() throws Exception {
		OutputStream os = new FileOutputStream("test.bin");

		CollectSurvey survey = loadSurvey();
		CollectRecord record = createTestRecord(survey);
		Entity root = record.getRootEntity();
//		AttributeField<Integer> msg = AttributeField.newInstance(Integer.class);
//		msg.setValue(123);
//		AttributeFieldSchema protoSchema = new AttributeFieldSchema();
//		AttributeField msg2 = protoSchema.newMessage();
//		EntitySchema protoSchema = new EntitySchema();
		RuntimeSchema<Entity> protoSchema = (RuntimeSchema<Entity>) RuntimeSchema.getSchema(Entity.class);
		InputStream is = new FileInputStream("test.bin");
		
//		CollectSurvey survey = loadSurvey();
//		Schema schema = survey.getSchema();
//		TextAttributeDefinition txtDefn = (TextAttributeDefinition) schema.getByPath("/cluster/map_sheet");
//		TextAttribute msg = new TextAttribute(txtDefn);
//		msg.setValue("Test text");
//		TextASchema protoSchema = new TestSchema();
//		com.dyuproject.protostuff.Schema<TextAttribute> protoSchema = (RuntimeSchema<TextAttribute>) RuntimeSchema.getSchema(TextAttribute.class);
//		RuntimeSchema.register(AttributeField.class, new FieldSchema());
//		RuntimeSchema.register(Object.class, new TestSchema());
//		protoSchema = new CSchema<TextAttribute>(protoSchema);
//		TextAttribute msg2 = protoSchema.newMessage();
//		TextAttribute msg2 = (TextAttribute) txtDefn.createNode();

		RecordContext recordContext = record.getContext();
		CollectRecord record2 = new CollectRecord(recordContext, survey, "2.0");
		Entity root2 = record2.createRootEntity("cluster");
		LinkedBuffer lb = LinkedBuffer.allocate(250000);
		int size = ProtostuffIOUtil.writeTo(os, root, protoSchema, lb);
		// you can prefix the message with the size (delimited message)
		os.flush();
		os.close();
		lb.clear();
			
		ProtostuffIOUtil.mergeFrom(is, root2, protoSchema);
		lb.clear();
		
	}

//	@Test
	public void testSerialization() throws Exception {
		CollectSurvey survey = loadSurvey();
		Schema schema = survey.getSchema();
		assignFakeNodeDefinitionIds(schema);
		CollectRecord record = createTestRecord(survey);
		String r1 = record.getRootEntity().toString();
		RecordSerializer serializer = new RecordSerializer(CollectRecord.class, 300000);		
//		RecordDeserializer deserializer = new RecordDeserializer(record.getContext(), survey);
		
		String filename = "cluster.bin";
		serializer.serialize(record, filename);
		CollectRecord record2 = (CollectRecord) serializer.deserialize(record.getContext(), survey, filename);
		String r2 = record2.getRootEntity().toString();
		Assert.assertEquals(r1, r2);
	}

	private void assignFakeNodeDefinitionIds(Schema schema) {
		Collection<NodeDefinition> defns = schema.getAllDefinitions();
		int defnId = 1;
		for (NodeDefinition defn : defns) {
			defn.setId(defnId++);
		}
	}
	
	private CollectSurvey loadSurvey() throws IOException, SurveyImportException, InvalidIdmlException {
		URL idm = ClassLoader.getSystemResource("test.idm.xml");
		InputStream is = idm.openStream();
		CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext();
		SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
		CollectSurvey survey = (CollectSurvey) surveyUnmarshaller.unmarshal(is);
		survey.setName("archenland1");
		Schema schema = survey.getSchema();
		List<EntityDefinition> rootEntityDefinitions = schema.getRootEntityDefinitions();
		int nextId = 0;
		for (EntityDefinition entityDefinition : rootEntityDefinitions) {
			nextId = initIds(entityDefinition, nextId);
		}
		return survey;
	}

	private int initIds(NodeDefinition nodeDefinition, int nextId) {
		nodeDefinition.setId(nextId++);
		if ( nodeDefinition instanceof EntityDefinition ) {
			EntityDefinition entityDefinition = (EntityDefinition) nodeDefinition;
			List<NodeDefinition> childDefinitions = entityDefinition.getChildDefinitions();
			for (NodeDefinition childDefn : childDefinitions) {
				nextId = initIds(childDefn, nextId);
			}
		}
		return nextId;
		
	}

	private CollectRecord createTestRecord(CollectSurvey survey) {
		CollectRecordContext recordContext = new CollectRecordContext();
		CollectRecord record = new CollectRecord(recordContext, survey, "2.0");
		Entity cluster = record.createRootEntity("cluster");
		record.setCreationDate(new GregorianCalendar(2011, 12, 31, 23, 59).getTime());
		//record.setCreatedBy("ModelDAOIntegrationTest");
		record.setStep(Step.ENTRY);
		String id = "123_456";
		
		addTestValues(cluster, id);
			
		//set counts
		record.getEntityCounts().add(2);
		
		//set keys
		record.getRootEntityKeys().add(id);
		
		return record;
	}

	private void addTestValues(Entity cluster, String id) {
		cluster.addValue("id", new Code(id));
		cluster.addValue("gps_realtime", Boolean.TRUE);
		cluster.addValue("region", new Code("001"));
		cluster.addValue("district", new Code("002"));
		cluster.addValue("crew_no", 10);
		cluster.addValue("map_sheet", "value 1");
		cluster.addValue("map_sheet", "value 2");
		cluster.addValue("vehicle_location", new Coordinate((double)432423423l, (double)4324324l, "srs"));
		cluster.addValue("gps_model", "TomTom 1.232");
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,14));
			ts.addValue("start_time", new Time(8,15));
			ts.addValue("end_time", new Time(15,29));
		}
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,15));
			ts.addValue("start_time", new Time(8,32));
			ts.addValue("end_time", new Time(11,20));
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("1"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 54.2);
			tree1.addValue("total_height", 2.0);
//			tree1.addValue("bole_height", (Double) null).setMetadata(new CollectAttributeMetadata('*',null,"No value specified"));
			RealAttribute boleHeight = tree1.addValue("bole_height", (Double) null);
			boleHeight.getField().setSymbol('*');
			boleHeight.getField().setRemarks("No value specified");
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 82.8);
			tree2.addValue("total_height", 3.0);
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("2"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 34.2);
			tree1.addValue("total_height", 2.0);
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 85.8);
			tree2.addValue("total_height", 4.0);
		}
	}
}
