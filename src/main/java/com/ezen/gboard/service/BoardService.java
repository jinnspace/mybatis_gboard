package com.ezen.gboard.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.gboard.dao.IBoardDao;
import com.ezen.gboard.dto.BoardVO;
import com.ezen.gboard.dto.Paging;
import com.ezen.gboard.dto.ReplyVO;

@Service
public class BoardService {

	@Autowired
	IBoardDao bdao;

	public HashMap<String, Object> selectBoardAll(HttpServletRequest request) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		Paging paging=new Paging();

		HttpSession session=request.getSession();
		int page=1;
		if(request.getParameter("page")!=null) {
			page=Integer.parseInt(request.getParameter("page"));
			session.setAttribute("page", page);
		}else if(session.getAttribute("page")!=null) {
			page=(Integer)session.getAttribute("page");
		}else { 
			session.removeAttribute("page");
		}
		
		paging.setPage(page);
		int count=bdao.getAllcount();
		paging.setTotalCount(count);
		paging.paging();
		List<BoardVO>list=bdao.selectBoardAll(paging);
		for(BoardVO bdto:list) {
			int cnt=bdao.replyCount(bdto.getNum());
			bdto.setReplycnt(cnt);
		}
		result.put("boardList", list);
		result.put("paging",paging);
		return result;
	}

	public void insertBoard(BoardVO boardvo) {
		bdao.insertBoard(boardvo);
		
	}

	public HashMap<String, Object> boardView(int num) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		
		//조회수 증가
		bdao.plusOneReadCount(num);
		//게시물 조회
		BoardVO bvo=bdao.getBoard(num);
		//댓글 리스트 조회
		List<ReplyVO>list=bdao.selectReply(num);
		//위 동작 중 필요한 결과들을 모두 result에 put한다.
		result.put("board", bvo);
		result.put("replyList", list);
		
		return result;
	}

	public void insertReply(ReplyVO replyvo) {
		bdao.insertReply(replyvo);
		
	}

	public HashMap<String, Object> boardViewWithoutCount(int num) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		BoardVO bvo=bdao.getBoard(num);
		//댓글 리스트 조회
		List<ReplyVO>list=bdao.selectReply(num);
		//위 동작 중 필요한 결과들을 모두 result에 put한다.
		result.put("board", bvo);
		result.put("replyList", list);
		
		return result;
	}

	public void deleteReply(int num) {
		bdao.deleteReply(num);
		
	}

	public BoardVO getBoard(int num) {
		return bdao.getBoard(num);
	}

	public void updateBoard(BoardVO boardvo) {
		bdao.updateBoard(boardvo);
		
	}
	
	public void removeBoard(int num) {
		bdao.removeBoard(num);
	}
}
