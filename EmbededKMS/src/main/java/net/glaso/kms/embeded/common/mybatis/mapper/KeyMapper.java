package net.glaso.kms.embeded.common.mybatis.mapper;

import net.glaso.kms.embeded.common.domain.Key;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface KeyMapper {

    @Select( "select * from abc" )
    List<Key> getKey();


}
