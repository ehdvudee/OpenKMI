package net.glaso.kms.embeded.common.init;

import net.glaso.kms.embeded.common.exceptions.KmsNullPointerException;
import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;
import net.glaso.kms.embeded.common.properties.ConfGetter;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public final class ProgramInitializer {

    private final static Logger logger = Logger.getGlobal();

    private final String kekPassword;

    private static final String PROPERTIES_PATH = "conf/application.properties";
    private static final String MYBATIS_CONF_PATH = "conf/mybatis-config.xml";

    public ProgramInitializer( String kekPassword ) {
        this.kekPassword = kekPassword;
    }

    public ConfGetter initProperties() throws IOException {
        Properties prop = new Properties();

        URL resource = this.getClass().getClassLoader().getResource( PROPERTIES_PATH );

        if ( resource == null ) {
            throw new KmsNullPointerException( EnumErrorCode.INIT_ERROR, "Initializing Properties file is not found."  );
        }

        File propFile = new File( resource.getFile() );
        try ( InputStream input = new BufferedInputStream( new FileInputStream( propFile ) ) ) {
            prop.load( input );
        }

        ConfGetter confGetter = new ConfGetter( prop );

        logger.info( "success to initializing EmbededKMS app Conf." );

        return confGetter;
    }


}
