package com.mealkit.auth.mapper;

import com.mealkit.auth.model.MemberRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 회원 MyBatis 매퍼.
 * SQL은 {@code classpath:mapper/auth/Member.xml}
 */
@Mapper
public interface MemberMapper {

    Optional<MemberRow> findByLoginId(@Param("loginId") String loginId);

    long countAll();

    int insert(@Param("loginId") String loginId,
               @Param("password") String password,
               @Param("role") String role,
               @Param("name") String name);
}
