package com.dtc.strees.fidxc;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomOutputStream;

import com.dtc.dicom.operation.Storer;
import com.dtc.dicom.util.AttributeUtil;

public abstract class AttributeModifier extends AbstractStoreClient {

	protected abstract Attributes modifyAttrs(Attributes attrs);

	/**
	 * 將 {@link AbstractStoreClient#getSrcDicom()} 送出，
	 * 要注意 {@link Storer} 不會因為錯誤（如連線不到）噴 Exception
	 * @return sent file size (byte)
	 */
	protected long sendDicom() throws IOException {
		File srcDicom = getSrcDicom();

		if (!srcDicom.exists()) { return -1; }

		File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".dicom");
		tempFile.deleteOnExit();

		DicomOutputStream dos = new DicomOutputStream(tempFile);

		try {
			dos.writeDataset(
				AttributeUtil.fmiFrom(srcDicom),
				modifyAttrs(AttributeUtil.from(srcDicom))
			);
			dos.flush();

			getStorer()
				.send(tempFile)
				.release();

			return tempFile.length();
		} finally {
			if (dos != null) {
				try { dos.close(); } catch (Exception e) {}
			}
		}
	}
}
