package com.ezen.gboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.gboard.dao.IMemberDao;
import com.ezen.gboard.dto.MemberVO;

@Service
public class MemberService {

	@Autowired
	IMemberDao mdao;

	public MemberVO getMember(String userid) {
		
		return mdao.getMember(userid);
	}

	public void memberJoin(MemberVO membervo) {
		mdao.memberJoin(membervo);
		
	}

	public void memberUpdate( MemberVO membervo) {
		mdao.memberUpdate(membervo);
		
	}
}
