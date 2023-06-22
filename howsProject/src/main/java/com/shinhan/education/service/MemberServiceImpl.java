package com.shinhan.education.service;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.shinhan.education.dto.MemberDTO;
import com.shinhan.education.dto.MemberSignUpRequest;
import com.shinhan.education.dto.MemberUpdateRequest;
import com.shinhan.education.entity.Members;
import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.Payload;
import com.shinhan.education.vo.RequestVO;

@Transactional
@Service
public class MemberServiceImpl implements MemberService {
	
	
	String getMemberId(String token) {
		System.out.println("token: " + token);
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();

		String payloadJson = new String(decoder.decode(chunks[1]));

		Gson gson = new Gson();

		Payload payload = gson.fromJson(payloadJson, Payload.class);
		String memberid = payload.getSub();
		System.out.println("memberid : " + memberid);
		return memberid;
	}
	

	private final MemberRepository memberRepo;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

	public MemberServiceImpl(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider,
			PasswordEncoder passwordEncoder) {
		this.memberRepo = memberRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
	}

	// 회원가입
	@Override
	@Transactional
	public void signUp(MemberSignUpRequest requestDto, MemberLevel memberLevel) throws Exception {
		// 비밀번호 암호화
		String encryptedPassword = passwordEncoder.encode(requestDto.getPswd());

		Members member = requestDto.toEntity();
		member.setPswd(encryptedPassword);
		member.setMemberLevel(memberLevel);
		member = memberRepo.save(member);
	}

	// 로그인
	@Override
	public String login(String memberid, String password) {
		// 회원 가입 여부 확인
		boolean isMemberRegistered = checkMemberRegistration(memberid);
		if (!isMemberRegistered) {
			throw new IllegalArgumentException("가입되지 않은 ID입니다.");
		}

		// 주어진 회원 ID와 비밀번호를 사용하여 사용자의 인증을 진행
		boolean isAuthenticated = authenticate(memberid, password);
		if (!isAuthenticated) {
			throw new IllegalArgumentException("잘못된 비밀번호입니다.");
		}

		// 토큰 발행을 위한 회원 정보 가져오기
		Members member = memberRepo.findByMemberid(memberid)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 가져올 수 없습니다."));

		// 권한 리스트 생성
		List<String> roles = new ArrayList<>();
		roles.add(member.getRoles().name());

		// 토큰 발행
		return jwtTokenProvider.createToken(member.getMemberid(), roles);
	}

	// 메서드는 주로 로그인 기능에서 사용되며, 주어진 회원 ID와 비밀번호를 사용하여 사용자의 인증함
	@Override
	public boolean authenticate(String memberid, String password) {
		Members member = memberRepo.findByMemberid(memberid)
				.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));

		return passwordEncoder.matches(password, member.getPswd());
	}

	// 메서드는 주어진 회원 ID를 사용하여 해당 회원의 역할(Role) 목록을 가져온다.
	@Override
	public List<String> getRoles(String memberid) {
		Members member = memberRepo.findByMemberid(memberid)
				.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));

		return member.getRoleNames();
	}

	// 회원 가입 전에 중복된 회원 ID가 있는지 확인하거나, 특정 회원의 가입 여부를 확인할 때 사용
	@Override
	public boolean checkMemberRegistration(String memberid) {
		Optional<Members> memberOptional = memberRepo.findByMemberid(memberid);
		return memberOptional.isPresent();
	}

	// 회원탈퇴
	@Override
	public boolean delete(String memberid) {
		Optional<Members> memberOptional = memberRepo.findByMemberid(memberid);

		if (memberOptional.isPresent()) {
			Members member = memberOptional.get();
			memberRepo.delete(member);
			return true;
		}

		return false;
	}

	//회원정보수정
	@Override
	@Transactional
	public boolean update(HttpServletRequest request, MemberUpdateRequest updaterequest) throws Exception {
		
	    // 토큰에서 회원 ID 추출
		String token = request.getHeader("token");
		String memberid = getMemberId(token);

	    // 회원 ID를 기반으로 회원 정보를 조회합니다.
	    Members member = memberRepo.findByMemberid(memberid)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    // 비밀번호를 암호화합니다.
	    String encryptedPassword = member.getPswd(); // 비밀번호가 제공되지 않으면 기존 비밀번호를 유지합니다.

	    if (updaterequest.getPswd() != null) {
	        encryptedPassword = passwordEncoder.encode(updaterequest.getPswd());
	    }
	    // 필드를 업데이트합니다.
	    if (updaterequest.getMembername() != null) {
	        member.setMembername(updaterequest.getMembername());
	    }
	    if (updaterequest.getBday() != null) {
	        member.setBday(updaterequest.getBday());
	    }
	    if (updaterequest.getPhone() != null) {
	        member.setPhone(updaterequest.getPhone());
	    }
	    if (updaterequest.getAccBank() != null) {
	        member.setAccBank(updaterequest.getAccBank());
	    }
	    if (updaterequest.getAccno() != null) {
	        member.setAccno(updaterequest.getAccno());
	    }
	    if (updaterequest.getJobname() != null) {
	        member.setJobname(updaterequest.getJobname());
	    }
	    if (updaterequest.getHasjob() != null) {
	        member.setHasjob(updaterequest.getHasjob());
	    }
	    if (updaterequest.getHiredate() != null) {
	        member.sethiredate(updaterequest.getHiredate()); 
	    }
	    if (updaterequest.getMarry() != null) {
	        member.setMarry(updaterequest.getMarry());
	    }
	    if (updaterequest.getHaschild() != null) {
	        member.setHaschild(updaterequest.getHaschild());
	    }

	    // 업데이트된 비밀번호를 설정합니다.
	    member.setPswd(encryptedPassword);

	    // 업데이트된 회원 정보를 저장합니다.
	    memberRepo.save(member);

	    return true;
	}

	// 클라이언트에게 회원정보목록 보냄(total 포함)
	@Override
	public RequestVO<List<MemberDTO>> getMembers(int page, int size) {
	    // 페이지 요청 객체 생성
	    PageRequest pageable = PageRequest.of(page, size, Sort.by("joindate").descending());
	    // 페이지 단위로 회원 목록 조회
	    Page<Members> membersPage = memberRepo.findAll(pageable);
	    // 전체 회원 수
	    Long total = membersPage.getTotalElements();
	    // 현재 페이지의 회원 목록
	    List<Members> members = membersPage.getContent();

	    // 가공된 회원 목록을 담을 리스트 생성
	    List<MemberDTO> filteredMembers = new ArrayList<>();

	    // 각 회원 정보를 필요한 필드로 변환하여 filteredMembers에 추가
	    for (Members member : members) {
	        MemberDTO filteredMember = new MemberDTO();
	        filteredMember.setMemberid(member.getMemberid());
	        filteredMember.setMembername(member.getMembername());
	        filteredMember.setBday(member.getBday());
	        filteredMember.setRoles(member.getRoles());
	        filteredMember.setEmail(member.getEmail());
//	        filteredMember.setEmploydocument(employdocument);
	        filteredMembers.add(filteredMember);
	    }

	    // 결과를 담을 RequestVO 객체 생성
	    RequestVO<List<MemberDTO>> r = new RequestVO<>();
	    r.setObj(filteredMembers);
	    r.setTotal(Integer.parseInt(total.toString()));
	    return r;
	}

	// 회원아이디 조회
	@Override
	public Optional<Members> getMemberByid(String memberid) {
		return memberRepo.findByMemberid(memberid);
	}

	// 관리자가 사용자 권한 수정
	@Override
	public void updateMemberRoles(String memberid, List<String> roles) {
		Optional<Members> optionalMember = getMemberByid(memberid);
		if (optionalMember.isPresent()) {
			Members member = optionalMember.get();
			System.out.println(member);
			member.setRoles(roles);
			memberRepo.save(member); // 멤버에 저장
		} else {
			throw new RuntimeException("회원을 찾을 수 없습니다.");
		}
	}
	
	
}