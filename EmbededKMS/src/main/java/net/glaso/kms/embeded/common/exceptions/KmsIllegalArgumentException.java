package net.glaso.kms.embeded.common.exceptions;

import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;

@SuppressWarnings("serial")
public class KmsIllegalArgumentException extends EmbededKmsException {

	public KmsIllegalArgumentException(EnumErrorCode errCode, String errMsg, Exception e ) {
		super( errCode, errMsg, e );
	}

	public KmsIllegalArgumentException(EnumErrorCode errCode, String errMsg ) {
		super( errCode, errMsg );
	}

	public KmsIllegalArgumentException(EnumErrorCode errCode, Exception e ) {
		super( errCode, e );
	}
}
