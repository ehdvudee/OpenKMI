package net.glaso.kms.embeded;

import net.glaso.kms.embeded.common.init.ProgramInitializer;
import net.glaso.kms.embeded.common.mybatis.mapper.KeyMapper;
import net.glaso.kms.embeded.common.utils.app.DataTypeConverter;
import net.glaso.kms.embeded.common.utils.pbkdf2.Pbkdf2Util;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramInitializerTest {

    private final String algo = "HmacSHA256";
    private final String salt = "0e9e808300edd7ae043bf4a07bc7b748fda89ed6eb6e7a60c22d038208ea5a7a6b1b882c5dd42a5738057d2f3bf5edb9b49fdcfd766de03694d24e3cc7a774d5";
    private final int itr = 10000;

    private final String kekVerifyingStr = "Welcome to EmbededKMS";

    private final Logger logger = Logger.getGlobal();

    @Test
    public void run() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // GIVEN
        String password = "3Q_bFQ$J,a,L(n8Gg[J,";
        DataSource testDataSource = getTestDataSource();
        byte[] kekVerifyingVal = generateKekPw( password );

        // WHEN
        ProgramInitializer initializer = new ProgramInitializer(
                DataTypeConverter.printHexBinary( kekVerifyingVal ),
                password,
                testDataSource );

        // THEN
        assertThat( initializer.getKek() )
                .isNotNull();
        assertThat( initializer.getConfGetter() )
                .isNotNull();
        assertThat( initializer.getSqlSessionfactory().openSession() )
                .isNotNull();
    }

    private DataSource getTestDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        String filePath = this.getClass().getClassLoader().getResource( "./test.db" ).getFile();

        dataSource.setUrl( "jdbc:sqlite:" + filePath + "?foreign_keys=ON&busy_timeout=60000&journal_mode=WAL&date_string_format=yyyy-MM-dd HH:mm:ss" );
        dataSource.setDriverClassName( "org.sqlite.JDBC" );

        return dataSource;
    }

    private byte[] generateKekPw( String password ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encodedKey = new byte[16];
        byte[] iv = new byte[16];

        byte[] derivedData = new Pbkdf2Util.Builder().password( password.getBytes() )
                .salt( salt )
                .iteration( itr )
                .derivedKeyLength( 32 )
                .deriveAlgoritm( algo )
                .build()
                .derivKey();

        System.arraycopy( derivedData, 0, encodedKey, 0, 16  );
        System.arraycopy( derivedData, 16, iv, 0, 16 );

        logger.info( "real key : " + DataTypeConverter.printHexBinary( encodedKey ) );
        logger.info( "real iv : " + DataTypeConverter.printHexBinary( iv ) );

        SecretKey key = new SecretKeySpec( encodedKey, "AES" );
        AlgorithmParameterSpec encIv = new IvParameterSpec( iv );

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );

        cipher.init( Cipher.ENCRYPT_MODE, key, encIv );
        byte[] cipherText = cipher.doFinal( kekVerifyingStr.getBytes() );

        logger.info( "kekVerifyingVal : " + DataTypeConverter.printHexBinary( cipherText ) );

        return cipherText;
    }

}
