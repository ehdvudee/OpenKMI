package net.glaso.kms.embeded.common.exceptions;

import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;

@SuppressWarnings("serial")
public abstract class EmbededKmsException extends RuntimeException {
		
	private final EnumErrorCode errCode;

	public EmbededKmsException( EnumErrorCode errCode, String errMsg, Exception e ) {
		super( errMsg, e );
		this.errCode = errCode;
	}
	
	public EmbededKmsException( EnumErrorCode errCode, String errMsg ) {
		super( errMsg );
		this.errCode = errCode;
	}
	
	public EmbededKmsException( EnumErrorCode errCode, Exception e ) {
		super(e);
		this.errCode = errCode;
	}

	public EnumErrorCode getErrCode() {
		return errCode;
	}
}
