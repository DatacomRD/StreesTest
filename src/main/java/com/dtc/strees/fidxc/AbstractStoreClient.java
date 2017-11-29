package com.dtc.strees.fidxc;

import java.io.File;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import com.dtc.dicom.operation.Storer;
import com.dtc.dicom.vo.AeInfo;

public abstract class AbstractStoreClient extends AbstractJavaSamplerClient {
	public static final String DEFAULT_SCU_AET = "SCU";
	public static final String DEFAULT_SCU_HOST = "127.0.0.1";
	public static final int DEFAULT_SCU_PORT = 1202;
	public static final String DEFAULT_SCP_AET = "DTC_STORE";
	public static final String DEFAULT_SCP_HOST = "localhost";
	public static final int DEFAULT_SCP_PORT = 55688;

	private static final String SCU_AET_NAME = "scuAet";
	private static final String SCU_HOST_NAME = "scuHost";
	private static final String SCU_PORT_NAME = "scuPort";
	private static final String SCP_AET_NAME = "scpAet";
	private static final String SCP_HOST_NAME = "scpHost";
	private static final String SCP_PORT_NAME = "scpPort";
	private static final String SRC_DICOM_NAME = "srcDicom";

	private String scuAet;
	private String scuHost;
	private int scuPort;
	private String scpAet;
	private String scpHost;
	private int scpPort;
	private File srcDicom;

	@Override
	public final Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument(SCU_AET_NAME, DEFAULT_SCU_AET);
		params.addArgument(SCU_HOST_NAME, DEFAULT_SCU_HOST);
		params.addArgument(SCU_PORT_NAME, String.valueOf(DEFAULT_SCU_PORT));
		params.addArgument(SCP_AET_NAME, DEFAULT_SCP_AET);
		params.addArgument(SCP_HOST_NAME, DEFAULT_SCP_HOST);
		params.addArgument(SCP_PORT_NAME, String.valueOf(DEFAULT_SCP_PORT));
		params.addArgument(SRC_DICOM_NAME, "");
		return params;
	}

	@Override
	public final void setupTest(JavaSamplerContext ctx) {
		scuAet = ctx.getParameter(SCU_AET_NAME);
		scuHost = ctx.getParameter(SCU_HOST_NAME);
		scuPort = (int)ctx.getLongParameter(SCU_PORT_NAME);
		scpAet = ctx.getParameter(SCP_AET_NAME);
		scpHost = ctx.getParameter(SCP_HOST_NAME);
		scpPort = (int)ctx.getLongParameter(SCP_PORT_NAME);
		srcDicom = new File(ctx.getParameter(SRC_DICOM_NAME));
	}

	protected Storer getStorer() {
		return new Storer()
			.configScp(new AeInfo(scpAet, scpHost, scpPort))
			.configScu(new AeInfo(scuAet, scuHost, scuPort));
	}

	public File getSrcDicom() {
		return srcDicom;
	}
}
