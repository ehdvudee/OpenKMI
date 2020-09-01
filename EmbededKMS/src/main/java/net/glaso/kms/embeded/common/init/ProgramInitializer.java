package net.glaso.kms.embeded.common.init;

import net.glaso.kms.embeded.common.exceptions.KmsNullPointerException;
import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;
import net.glaso.kms.embeded.common.mapper.key.KeyMapper;
import net.glaso.kms.embeded.common.properties.ConfGetter;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.sql.DataSource;
import javax.swing.text.Keymap;
import java.io.*;
import java.net.URL;
import java.sql.JDBCType;
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

    public SqlSessionFactory initMybatis( DataSource dataSource ) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment( "EmbeddedKms", transactionFactory, dataSource );
        Configuration configuration = new Configuration( environment );

        configuration.getTypeAliasRegistry().registerAliases( "net.glaso.kms.embeded.common.domain" );
        configuration.getMapperRegistry().addMapper( KeyMapper.class );
        configuration.setJdbcTypeForNull( JdbcType.NULL );

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build( configuration );

        return sqlSessionFactory;
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
