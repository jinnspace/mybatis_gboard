package com.ezen.gboard.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ezen.gboard.dto.MemberVO;

@Mapper
public interface IMemberDao {

	MemberVO getMember(String userid);

	void memberJoin( MemberVO membervo);

	void memberUpdate(MemberVO membervo);

}
