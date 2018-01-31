package com.dtc.strees.fidxc;

import java.io.IOException;
import java.util.UUID;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

public class ArchiveTest extends AttributeModifier {

	public SampleResult runTest(JavaSamplerContext arg0) {
		SampleResult result = new SampleResult();
		result.sampleStart();

		try {
			result.setSentBytes(sendDicom());
		} catch (IOException e) {
			e.printStackTrace();
		}

		result.sampleEnd();
		return result;
	}

	@Override
	protected Attributes modifyAttrs(Attributes attrs) {
		attrs.setString(Tag.StudyInstanceUID, VR.UI, UUID.randomUUID().toString());
		return attrs;
	}
}
