package com.dtc.strees.fidxc;


import java.util.Date;
import java.util.UUID;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.dtc.strees.util.SerNoGenerator;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.ImagingStudy;
import ca.uhn.fhir.model.dstu2.resource.ImagingStudy.Series;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.DiagnosticOrderPriorityEnum;
import ca.uhn.fhir.model.dstu2.valueset.DiagnosticOrderStatusEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * 模擬 DAS & Archive 對 FHIR 存取的壓力測試，
 * 目前只做簡單的新增 Patient、ImageStudy、DxOrder、DxReport，
 * 實際上，每一個影像進來的流程還會參雜一些查詢。
 */
public class FhirTest extends AbstractJavaSamplerClient {
	//參數設定區
	private static final String FHIR_BASE_URL = "fhirBaseUrl";
	private String fhirBaseUrl;

	@Override
	public final Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument(FHIR_BASE_URL, "");
		return params;
	}

	@Override
	public final void setupTest(JavaSamplerContext ctx) {
		fhirBaseUrl = ctx.getParameter(FHIR_BASE_URL);
	}
	////

	private static SerNoGenerator snGenerator = new SerNoGenerator();
	private static String modality = "CR";
	private static String studyId = "PC9801";
	private FhirContext fhirContext;
	private String id;
	private IGenericClient client;

	public FhirTest() {
		try {
			fhirContext = FhirContext.forDstu2();
			fhirContext.getRestfulClientFactory().setConnectionRequestTimeout(120000);
			fhirContext.getRestfulClientFactory().setConnectTimeout(120000);
			fhirContext.getRestfulClientFactory().setSocketTimeout(120000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SampleResult runTest(JavaSamplerContext ctx) {
		SampleResult result = new SampleResult();
		result.sampleStart();
		client = fhirContext.newRestfulGenericClient(fhirBaseUrl);

		id = snGenerator.next(); // as AccessionNumber
		MethodOutcome outcome;
		int sendCount = 0;
		int successCount = 0;

		try {
			//Patient
			Patient patient = createPatient();
			outcome = client.update().resource(patient).execute();
			sendCount++;
			if (outcome.getCreated()) { successCount++; }


			//ImagingStudy
			ImagingStudy imagingStudy = createImagingStudy();
			imagingStudy.getIdentifierFirstRep().setValue(ctx.getParameter("TestElement.name"));
			imagingStudy.getPatient().setResource(patient);
			outcome = client.update().resource(imagingStudy).execute();
			sendCount++;
			if (outcome.getCreated()) { successCount++; }


			//DxOrder
			DiagnosticOrder dxOrder = createDxOrder();
			dxOrder.getSubject().setResource(patient);
			dxOrder.getSubject().setDisplay(patient.getNameFirstRep().getText());
			outcome = client.update().resource(dxOrder).execute();
			sendCount++;
			if (outcome.getCreated()) { successCount++; }


			//DxReport
			DiagnosticReport dxReport = createDxReport();
			dxReport.getSubject().setResource(patient);
			dxReport.getSubject().setDisplay(patient.getNameFirstRep().getText());
			dxReport.addImagingStudy().setResource(imagingStudy);
			outcome = client.update().resource(dxReport).execute();
			sendCount++;
			if (outcome.getCreated()) { successCount++; }


			result.setSuccessful(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setSentBytes(sendCount); //紀錄送出 request 的次數
		result.setBytes((long)successCount); //紀錄成功新增的次數
		result.sampleEnd();
		return result;
	}

	private Patient createPatient() {
		Patient data = new Patient();
		String pateintId = "PATT.TEST." + id;
		data.setId(pateintId);
		data.getNameFirstRep().setText("臨時演員");
		data.getNameFirstRep().addGiven("臨時演員");
		data.setGender(AdministrativeGenderEnum.UNKNOWN);
		data.getTelecomFirstRep().setValue("02-22250891");
		data.getIdentifierFirstRep().setSystem("idNumber");
		data.getIdentifierFirstRep().setValue("Z" + id);
		return data;
	}

	private ImagingStudy createImagingStudy() {
		ImagingStudy data = new ImagingStudy();
		data.setId(UUID.randomUUID().toString());
		data.getAccession().setValue(id);
		data.setDescription("其實這是測試用的 ImagingStudy");
		data.setStartedWithSecondsPrecision(new Date());
		data.getIdentifierFirstRep().setValue(studyId);
		Series series = data.getSeriesFirstRep();
		series.getModality().setCode(modality);
		series.getUidElement().setValue(UUID.randomUUID().toString());
		series.getInstanceFirstRep().getUidElement().setValue(UUID.randomUUID().toString());
		return data;
	}

	private DiagnosticOrder createDxOrder() {
		DiagnosticOrder data = new DiagnosticOrder();
		String dxOrderId = "DXOD.TEST." + id;
		data.setId(dxOrderId);
		data.getIdentifierFirstRep().setSystem("modality");
		data.getIdentifierFirstRep().setValue(modality);
		data.setStatus(DiagnosticOrderStatusEnum.DRAFT);
		data.setPriority(DiagnosticOrderPriorityEnum.ASAP);
		data.getEventFirstRep().setStatus(DiagnosticOrderStatusEnum.DRAFT);
		data.getEventFirstRep().setDateTimeWithSecondsPrecision(new Date());
		return data;
	}

	private DiagnosticReport createDxReport() {
		DiagnosticReport data = new DiagnosticReport();
		data.setId(UUID.randomUUID().toString());
		data.addIdentifier(new IdentifierDt("modality", modality));
		data.addIdentifier(new IdentifierDt("accessionNumber", id));
		data.addIdentifier(new IdentifierDt("totalPage", "1"));
		data.addIdentifier(new IdentifierDt("organization", "not exist"));
		data.getCode().setText(studyId);
		data.setIssuedWithMillisPrecision(new Date());
		return data;
	}
}
