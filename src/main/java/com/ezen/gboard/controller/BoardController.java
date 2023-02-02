package com.ezen.gboard.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.gboard.dto.BoardVO;
import com.ezen.gboard.dto.Paging;
import com.ezen.gboard.dto.ReplyVO;
import com.ezen.gboard.service.BoardService;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@Controller
public class BoardController {

	@Autowired
	BoardService bs;

	@Autowired
	ServletContext context;
	
	@RequestMapping("/main")
	public ModelAndView goMain(HttpServletRequest request) {
		ModelAndView mav=new ModelAndView();
		
		HttpSession session=request.getSession();
		if(session.getAttribute("loginUser")==null)
			mav.setViewName("loginform");
		else {
			
			HashMap<String, Object> result=bs.selectBoardAll(request);
			mav.addObject("boardList",(List<BoardVO>)result.get("boardList"));
			mav.addObject("paging",(Paging)result.get("paging"));
			mav.setViewName("board/main");
		}
		return mav;
	}
	
	@RequestMapping("/boardWriteForm")
	public String write_form(HttpServletRequest request) {
		String url="board/boardWriteForm";
		
		HttpSession session=request.getSession();
		if(session.getAttribute("loginUser")==null)url="member/loginForm";
		
		return url;
	}
	
	@RequestMapping(value="/boardWrite",method=RequestMethod.POST)
	public String boardWrite(HttpServletRequest request,@ModelAttribute("dto")@Valid BoardVO boardvo,
			BindingResult result,Model model) {
		//String title=request.getParameter("title");
		//System.out.println("title : "+title);
		// 방법1) javascript valid 사용    /  방법2)파일입력과 기타의 데이터들을 분리한다.
		String url="board/boardWriteForm";
		if(result.getFieldError("pass")!=null) {
			model.addAttribute("message",result.getFieldError("pass").getDefaultMessage());		
		}else if(result.getFieldError("title")!=null) {
			model.addAttribute("message",result.getFieldError("title").getDefaultMessage());			
		}else if(result.getFieldError("content")!=null) {
			model.addAttribute("message",result.getFieldError("content").getDefaultMessage());			
		}else {
			bs.insertBoard(boardvo);
			//Service와 Dao에 insertBoard를 제작
			url="redirect:/main";
			
		}
		
		return url;
	}
	
	@RequestMapping("/selectimg")
	public String selectimg() {
		return "board/selectimg";
	}
	
	
	@RequestMapping(value="/fileupload" , method=RequestMethod.POST)
	public String fileupload(Model model,HttpServletRequest request) {
		
		String path=context.getRealPath("/upload");
		try {
			MultipartRequest multi=new MultipartRequest(
					request, path,5*1024*1024,"UTF-8",new DefaultFileRenamePolicy()
			);
			//전송된 파일은 업로드 되고, 파일이름은 모델에 저장
			model.addAttribute("image",multi.getFilesystemName("image"));
		} catch (IOException e) {	e.printStackTrace();
		}
		return "board/completupload";
	}
	
	@RequestMapping("/boardView")
	public ModelAndView boardView(@RequestParam("num")int num,
			HttpServletRequest request) {
		ModelAndView mav=new ModelAndView();
		
		HashMap<String,Object>result=bs.boardView(num);
		
		mav.addObject("board",(BoardVO)result.get("board"));
		mav.addObject("replyList",(List<ReplyVO>)result.get("replyList"));
		mav.setViewName("board/boardView");
		
		return mav;
	}
	
	@RequestMapping("/addReply")
	public String addReply(ReplyVO replyvo,HttpServletRequest request) {
		bs.insertReply(replyvo);
		
		return "redirect:/boardViewWithoutCount?num="+replyvo.getBoardnum();
	}
	
	@RequestMapping("/boardViewWithoutCount")
	public ModelAndView boardViewWithoutCount(@RequestParam("num")int num,
			HttpServletRequest request,Model model) {
		
		ModelAndView mav=new ModelAndView();
		
		HashMap<String,Object>result=bs.boardViewWithoutCount(num);
		
		mav.addObject("board",(BoardVO)result.get("board"));
		mav.addObject("replyList",(List<ReplyVO>)result.get("replyList"));
		mav.setViewName("board/boardView");
		
		return mav;
	}
	
	@RequestMapping("/deleteReply")
	public String reply_delete(@RequestParam("num")int num,
			@RequestParam("boardnum")int boardnum,HttpServletRequest request) {
		bs.deleteReply(num);
		return "redirect:/boardViewWithoutCount?num=" + boardnum;
	}
	
	@RequestMapping("/boardEditDeleteForm")
	public String board_edit_form(Model model,HttpServletRequest request,@RequestParam("num")int num) {
		model.addAttribute("num",num);
		return "board/boardCheckPassForm";
	}
	
	@RequestMapping("/boardEditDelete")
	public String board_edit(@RequestParam("num")int num,@RequestParam("pass")String pass,Model model,
			HttpServletRequest request) {
		BoardVO bvo=bs.getBoard(num);
		model.addAttribute("num",num);
		if(pass.equals(bvo.getPass())) {
			return "board/boardCheckPass";
		}else {
			model.addAttribute("message","비밀번호가 맞지 않습니다 확인해주세요");
			return "board/boardCheckPassForm";
		}
	}
	@RequestMapping("/boardUpdateForm")
	public String board_update_form(@RequestParam("num")int num,
			Model model,HttpServletRequest request) {
		BoardVO bvo=bs.getBoard(num);
		model.addAttribute("num",num);
		model.addAttribute("dto",bvo);
		return "board/boardEditForm";
	}
	@RequestMapping(value="/boardUpdate",method=RequestMethod.POST)
	public String boardUpdate(@ModelAttribute("dto")@Valid BoardVO boardvo,BindingResult result,
			@RequestParam("oldfilename")String oldfilename, HttpServletRequest request,Model model) {
		String url="board/boardEditForm";
		model.addAttribute("oldfilename",oldfilename);
		if(boardvo.getImgfilename()==null)boardvo.setImgfilename(oldfilename);
		
		
		if(result.getFieldError("pass")!=null) {
			model.addAttribute("message",result.getFieldError("pass").getDefaultMessage());		
		}else if(result.getFieldError("title")!=null) {
			model.addAttribute("message",result.getFieldError("title").getDefaultMessage());			
		}else if(result.getFieldError("content")!=null) {
			model.addAttribute("message",result.getFieldError("content").getDefaultMessage());			
		}else {
			bs.updateBoard(boardvo);
			url="redirect:/boardViewWithoutCount?num="+boardvo.getNum();
			
		}
		
		
		return url;
	}
	
	
	
	
	
	@RequestMapping("boardDelete")
	public String board_delete_form(@RequestParam("num")int num,
			Model model,HttpServletRequest request) {
		bs.removeBoard(num);
		return "redirect:/main";
	}
	
	
}

