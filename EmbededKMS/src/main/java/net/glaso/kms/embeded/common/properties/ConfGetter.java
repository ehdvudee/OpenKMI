package net.glaso.kms.embeded.common.properties;

import net.glaso.kms.embeded.common.utils.app.DataTypeConverter;

import java.util.Properties;

public class ConfGetter {

    private final String filePbkdfAlgo;
    private final byte[] filePbkdfSalt;
    private final int filePbkdfItr;

    public ConfGetter( Properties prop ) {
        this.filePbkdfAlgo = prop.getProperty( "app.embeded.kms.initconf.file.dec.pbkdf.algo" ).trim();
        this.filePbkdfSalt = DataTypeConverter.parseHexBinary( prop.getProperty( "app.embeded.kms.initconf.file.dec.pbkdf.salt" ).trim() );
        this.filePbkdfItr = Integer.parseInt( prop.getProperty( "app.embeded.kms.initconf.file.dec.pbkdf.itr" ).trim() );
    }

    public String getFilePbkdfAlgo() {
        return filePbkdfAlgo;
    }

    public byte[] getFilePbkdfSalt() {
        return filePbkdfSalt;
    }

    public int getFilePbkdfItr() {
        return filePbkdfItr;
    }
}
