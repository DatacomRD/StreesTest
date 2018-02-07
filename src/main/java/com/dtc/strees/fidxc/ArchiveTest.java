package com.dtc.strees.fidxc;

import java.io.IOException;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

import com.dtc.strees.util.AccessionNumberGenerator;
import com.dtc.strees.util.StudyIdGenerator;

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
		String studyInstanceUID = StudyIdGenerator.next();
		attrs.setString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID); //lv1

		String seriesInstanceUID = studyInstanceUID + "." + 1;
		attrs.setString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUID); //lv1.lv2

		String sopInstanceUid = seriesInstanceUID + "." + 2;
		attrs.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sopInstanceUid); //lv1.lv2.lv3
		attrs.setString(Tag.SOPInstanceUID, VR.UI, sopInstanceUid);

		attrs.setString(Tag.AccessionNumber, VR.UI, AccessionNumberGenerator.next()); //15碼以內, 英數
		return attrs;
	}
}
