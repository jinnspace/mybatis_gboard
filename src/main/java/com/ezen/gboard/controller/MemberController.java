package com.ezen.gboard.controller;

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

import com.ezen.gboard.dto.MemberVO;
import com.ezen.gboard.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService ms;
	
	@RequestMapping("/")
	public String index() {
		return "member/loginForm";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@ModelAttribute("dto")@Valid MemberVO membervo,
			BindingResult result, HttpServletRequest request, Model model) {
		
		if(result.getFieldError("userid")!=null) {
			model.addAttribute("message",result.getFieldError("userid").getDefaultMessage());
			return "member/loginForm";
		}else if(result.getFieldError("pwd")!=null) {
			model.addAttribute("message",result.getFieldError("pwd").getDefaultMessage());
			return "member/loginForm";
		}
		
		MemberVO mvo=ms.getMember(membervo.getUserid());
		
		if(mvo==null) {
			model.addAttribute("message","아이디가 없습니다.");
			return "member/loginForm";
		}else if(mvo.getPwd()==null) {
			model.addAttribute("message","비밀번호 오류. 관리자에게 문의하세요");
			return "member/loginForm";
		}else if(!mvo.getPwd().equals(membervo.getPwd())) {
			model.addAttribute("message","비밀번호가 맞지 않습니다.");
			return "member/loginForm";
		}else if(mvo.getPwd().equals(membervo.getPwd())) {
			HttpSession session=request.getSession();
			session.setAttribute("loginUser", mvo);
			return "redirect:/main";
		}else {
			return "member/loginForm";
		}
		
	}
	
	@RequestMapping("/memberJoinForm")
	public String join_form() {
		return "member/memberJoinForm";
	}
	
	@RequestMapping("/idcheck")
	public ModelAndView idcheck(@RequestParam("userid")String id) {
		
		ModelAndView mav=new ModelAndView();
		
		MemberVO mvo=ms.getMember(id);
		if(mvo==null)mav.addObject("result",-1);
		else mav.addObject("result",1);
		
		mav.addObject("id",id);
		mav.setViewName("member/idcheck");
		return mav;
	}
	
	@RequestMapping(value="/memberJoin",method=RequestMethod.POST)
	public ModelAndView memberJoin(
			@ModelAttribute("dto")@Valid MemberVO membervo,BindingResult result,
			@RequestParam(value="re_id",required=false)String reid,
			@RequestParam(value="pwd_check",required=false)String pwdchk) {
		ModelAndView mav=new ModelAndView();
		//밸리데이션으로 전송된 값들을 점검하고, 널이나 빈칸이 있으면 memberJoinForm으로 되돌아 가도록 코딩
		//MemberVO로 전달되지 않는 전달인수 - pwd_check, re_id들은 별도의 변수로 전달받고 별도로 이상유무를
		//체크하고 이상이 있을시 memberJoinForm.jsp로 되돌아간다
		//이때 re_id도 mav에 별도 저장하고 되돌아간다
		//모두 이상이 없다고 점검이 되면 회원가입하고, 회원가입 완료라는 메세지와 함게 loginForm.jsp로 되돌아간다.

		mav.setViewName("member/memberJoinForm"); //되돌아갈 페이지의 기본은 회원가입 페이지
		mav.addObject("re_id",reid); //membervo 변수에 담기지 못한 데이터들은 별도로 관리한다.
		
		if(result.getFieldError("userid")!=null) {
			mav.addObject("message",result.getFieldError("userid").getDefaultMessage());			
		}else if(result.getFieldError("pwd")!=null) {
			mav.addObject("message",result.getFieldError("pwd").getDefaultMessage());			
		}else if(result.getFieldError("name")!=null) {
			mav.addObject("message",result.getFieldError("name").getDefaultMessage());			
		}else if(result.getFieldError("email")!=null) {
			mav.addObject("message",result.getFieldError("email").getDefaultMessage());			
		}else if(result.getFieldError("phone")!=null) {
			mav.addObject("message",result.getFieldError("phone").getDefaultMessage());		
		}else if(!reid.equals(membervo.getUserid())){
			mav.addObject("message","아이디 중복확인을 하세요.");	
		}else if(!pwdchk.equals(membervo.getPwd())){
			mav.addObject("message","비밀번호가 일치하지 않습니다.");	
		}else {
			ms.memberJoin(membervo);
			mav.addObject("message","회원가입 완료.");	
			mav.setViewName("member/loginForm");
		}
		
		
		return mav;	
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session=request.getSession();
		session.invalidate();
		return "redirect:/";
	}
	
	@RequestMapping("/memberEditForm")
	public ModelAndView memberEditForm(Model model,HttpServletRequest request) {
		ModelAndView mav=new ModelAndView();
		HttpSession session=request.getSession();
		
		MemberVO mvo=(MemberVO)session.getAttribute("loginUser");
		mav.addObject("dto",mvo); //다시 돌아올때 계속바뀐다. 다시갖고오려면 필요
		
		mav.setViewName("member/memberEditForm");
		return mav;
	}
	
	@RequestMapping(value="/memberEdit", method=RequestMethod.POST)
	public String memberEdit(@ModelAttribute("dto")@Valid MemberVO membervo,BindingResult result,
			@RequestParam(value="pwd_check",required=false)String pwdchk,Model model,HttpServletRequest request) {
		//현재 메서드를 완성하고, updateMember메서드도 정상 작동하도록 제작
		String url="member/memberEditForm";
		if(result.getFieldError("pwd")!=null) {
			model.addAttribute("message",result.getFieldError("pwd").getDefaultMessage());			
		}else if(result.getFieldError("name")!=null) {
			model.addAttribute("message",result.getFieldError("name").getDefaultMessage());			
		}else if(result.getFieldError("email")!=null) {
			model.addAttribute("message",result.getFieldError("email").getDefaultMessage());			
		}else if(result.getFieldError("phone")!=null) {
			model.addAttribute("message",result.getFieldError("phone").getDefaultMessage());		
		}else if(!pwdchk.equals(membervo.getPwd())){
			model.addAttribute("message","비밀번호가 일치하지 않습니다.");	
		}else {
			ms.memberUpdate(membervo);
			url="redirect:/main";
			HttpSession session=request.getSession();
			session.setAttribute("loginUser", membervo);
		}
		
		return url;
	}
}
