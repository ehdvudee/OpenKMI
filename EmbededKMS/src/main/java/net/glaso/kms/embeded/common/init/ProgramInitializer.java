package net.glaso.kms.embeded.common.init;

import net.glaso.kms.embeded.common.exceptions.KmsIllegalArgumentException;
import net.glaso.kms.embeded.common.exceptions.KmsNullPointerException;
import net.glaso.kms.embeded.common.kmsenum.EnumErrorCode;
import net.glaso.kms.embeded.common.mybatis.handler.SqliteBlobTypeHandler;
import net.glaso.kms.embeded.common.mybatis.mapper.KeyMapper;
import net.glaso.kms.embeded.common.properties.ConfGetter;
import net.glaso.kms.embeded.common.utils.app.DataTypeConverter;
import net.glaso.kms.embeded.common.utils.pbkdf2.Pbkdf2Util;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Properties;
import java.util.logging.Logger;

public final class ProgramInitializer {

    private final String propertiesPath = "conf/application.properties";
    private final String kekVerifyingStr = "Welcome to EmbededKMS";

    private static final Logger logger = Logger.getGlobal();

    private final ConfGetter confGetter;
    private final SqlSessionFactory sqlSessionfactory;
    private final SecretKey kek;
    private final AlgorithmParameterSpec encIv;

    public SqlSessionFactory getSqlSessionfactory() { return this.sqlSessionfactory; }
    public ConfGetter getConfGetter() { return this.confGetter; }
    public SecretKey getKek() { return this.kek; }
    public AlgorithmParameterSpec getEncIv() { return this.encIv; }

    public ProgramInitializer( String kekVerifyingVal, String kekPassword, DataSource dataSource ) {
        try {
            confGetter = initProperties();

            sqlSessionfactory = initMybatis(dataSource);

            byte[] derivedData = initKek(kekVerifyingVal, kekPassword);
            byte[] encodedKey = new byte[16];
            byte[] iv = new byte[16];

            System.arraycopy(derivedData, 0, encodedKey, 0, 16);
            System.arraycopy(derivedData, 16, iv, 0, 16);

            kek = new SecretKeySpec(encodedKey, "AES");
            encIv = new IvParameterSpec(iv);
        } catch ( Exception e ) {
            throw new KmsIllegalArgumentException( EnumErrorCode.INIT_ERROR, "Some error Occured during Initializing Program. please refer to the stacke trace.", e );
        }
    }

    private byte[] initKek( String kekVerifyingVal, String kekPassword ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encodedKey = new byte[16];
        byte[] iv = new byte[16];

        byte[] derivedData = new Pbkdf2Util.Builder().password( kekPassword.getBytes() )
                .salt( confGetter.getFilePbkdfSalt() )
                .iteration( confGetter.getFilePbkdfItr() )
                .derivedKeyLength( 32 )
                .deriveAlgoritm( confGetter.getFilePbkdfAlgo() )
                .build()
                .derivKey();

        System.arraycopy( derivedData, 0, encodedKey, 0, 16  );
        System.arraycopy( derivedData, 16, iv, 0, 16 );

        SecretKey key = new SecretKeySpec( encodedKey, "AES" );
        AlgorithmParameterSpec encIv = new IvParameterSpec( iv );

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );

        cipher.init( Cipher.DECRYPT_MODE, key, encIv );
        byte[] plainText = cipher.doFinal( DataTypeConverter.parseHexBinary( kekVerifyingVal ) );

        if ( new String( plainText ).equals( kekVerifyingStr ) ) {
            logger.info( "success init kek password" );
        } else {
            throw new KmsIllegalArgumentException( EnumErrorCode.INIT_ERROR, "kek is not verified." );
        }

        return derivedData;
    }

    private SqlSessionFactory initMybatis( DataSource dataSource ) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment( "EmbeddedKms", transactionFactory, dataSource );
        Configuration configuration = new Configuration( environment );

        configuration.getTypeAliasRegistry().registerAliases( "net.glaso.kms.embeded.common.domain" );
        configuration.getMapperRegistry().addMapper( KeyMapper.class );
        configuration.getTypeHandlerRegistry().register( SqliteBlobTypeHandler.class );
        configuration.setJdbcTypeForNull( JdbcType.NULL );

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build( configuration );

        logger.info( "succes to initialize EmbededKMS DB." );

        return sqlSessionFactory;
    }

    private ConfGetter initProperties() throws IOException {
        Properties prop = new Properties();

        URL resource = this.getClass().getClassLoader().getResource( propertiesPath );

        if ( resource == null ) {
            throw new KmsNullPointerException( EnumErrorCode.INIT_ERROR, "Initializing Properties file is not found."  );
        }

        File propFile = new File( resource.getFile() );
        try ( InputStream input = new BufferedInputStream( new FileInputStream( propFile ) ) ) {
            prop.load( input );
        }

        ConfGetter confGetter = new ConfGetter( prop );

        logger.info( "success to initialize EmbededKMS app Conf." );

        return confGetter;
    }


}
