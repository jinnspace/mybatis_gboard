package com.ezen.gboard.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ezen.gboard.dto.BoardVO;
import com.ezen.gboard.dto.Paging;
import com.ezen.gboard.dto.ReplyVO;

@Mapper
public interface IBoardDao {

	int getAllcount();

	List<BoardVO> selectBoardAll(Paging paging);

	int replyCount(int num);

	void insertBoard(BoardVO boardvo);

	void plusOneReadCount(int num);

	BoardVO getBoard(int num);

	List<ReplyVO> selectReply(int num);

	void insertReply(ReplyVO replyvo);

	void deleteReply(int num);

	void updateBoard(BoardVO boardvo);

	void removeBoard(int num);


	
}
