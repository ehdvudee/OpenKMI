package net.glaso.kms.embeded.common.exceptions;

import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;

@SuppressWarnings("serial")
public interface IEmbededKmsException {

	default EnumErrorCode getErrCode() {
		return EnumErrorCode.UNCLASSIFIED_ERROR;
	}
}
