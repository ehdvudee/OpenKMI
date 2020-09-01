package net.glaso.kms.embeded;

import net.glaso.kms.embeded.common.init.ProgramInitializer;
import net.glaso.kms.embeded.common.mapper.key.KeyMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

public class MyBatisTest {

    @Test
    public void run() {
        BasicDataSource dataSource = new BasicDataSource();

        String filePath = this.getClass().getClassLoader().getResource( "./test.db" ).getFile();

        dataSource.setUrl( "jdbc:sqlite:" + filePath + "?foreign_keys=ON&busy_timeout=60000&journal_mode=WAL&date_string_format=yyyy-MM-dd HH:mm:ss" );
        dataSource.setDriverClassName( "org.sqlite.JDBC" );

        ProgramInitializer initializer = new ProgramInitializer( "123" );
        SqlSessionFactory fac = initializer.initMybatis( dataSource );
        SqlSession session = fac.openSession();

        session.getMapper( KeyMapper.class ).getKey();


    }
}
