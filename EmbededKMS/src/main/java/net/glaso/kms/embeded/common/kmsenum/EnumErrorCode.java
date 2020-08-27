package net.glaso.kms.embeded.common.kmsenum;

public enum EnumErrorCode {
    UNCLASSIFIED_ERROR( "E_KMS-00000", "Unknown Embedded KMS Error" ),
    INIT_ERROR( "E_KMS-00001", "Error occurred while initializing Program." ),
    UNSUPPORT_FUNCTION( "E_KMS-00002", "Unsupport Operation Exception" ),
    ARGUMENT_ERROR( "E_KMS-00003", "Illegal arguments" );

    private final String errCode;
    private final String errMsg;

    EnumErrorCode( String errCode, String errMsg ) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
