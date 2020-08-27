package net.glaso.kms.embeded.common.exceptions;

import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;

@SuppressWarnings("serial")
public class KmsNullPointerException extends EmbededKmsException {
		
	public KmsNullPointerException( EnumErrorCode errCode, String errMsg, Exception e ) {
		super( errCode, errMsg, e );
	}
	
	public KmsNullPointerException( EnumErrorCode errCode, String errMsg ) {
		super( errCode, errMsg );		
	}
	
	public KmsNullPointerException( EnumErrorCode errCode, Exception e ) {
		super( errCode, e );
	}
}
