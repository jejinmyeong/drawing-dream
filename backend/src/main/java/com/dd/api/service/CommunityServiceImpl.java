package com.dd.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dd.api.dto.request.CommunityRegisterRequestDto;
import com.dd.api.dto.request.CommunityUpdateRequestDto;
import com.dd.api.dto.response.CommunityGetListResponseDto;
import com.dd.api.dto.response.CommunityGetListWrapperResponseDto;
import com.dd.api.dto.response.CommunityGetResponseDto;
import com.dd.api.dto.response.TotalCommunityGetResponseDto;
import com.dd.db.entity.board.Community;
import com.dd.db.entity.school.School;
import com.dd.db.entity.user.Auth;
import com.dd.db.entity.user.User;
import com.dd.db.entity.user.UserDepartment;
import com.dd.db.repository.AuthRepository;
import com.dd.db.repository.CommentRepository;
import com.dd.db.repository.CommunityRepository;
import com.dd.db.repository.UserDepartmentRepository;
import com.dd.security.util.JwtAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

	private final AuthRepository authRepository;
	
	private final UserDepartmentRepository userDepartmentRepository;
	
	private final CommunityRepository communityRepository;
	
	private final JwtAuthenticationProvider jwtAuthenticationProvider;
	
	@Override
	public void registerCommunity(String accessToken, CommunityRegisterRequestDto communityRegisterRequestDto) {
		String token = accessToken.split(" ")[1];
		String loginId = jwtAuthenticationProvider.getUsername(token);
		// 게시글 등록하는 유저 정보 가져오기
		User user = authRepository.findByLoginId(loginId).get().getUser();
		// 유저 소속 정보 가져오기
		UserDepartment userDepartment = userDepartmentRepository.findByUser(user).get();
		// 학교 가져오기
		School school = userDepartment.getSchool();
		// 현재 시간 가져오기
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		Community community = Community.builder()
				.title(communityRegisterRequestDto.getTitle())
				.content(communityRegisterRequestDto.getContent())
				.regTime(currentDateTime)
				.school(school)
				.user(user)
				.build();
		
		communityRepository.save(community);
	}
	
	@Override
	public boolean updateCommunity(String accessToken, CommunityUpdateRequestDto communityUpdateRequestDto) {
		String token = accessToken.split(" ")[1];
		String loginId = jwtAuthenticationProvider.getUsername(token);
		
		UUID userId = authRepository.findByLoginId(loginId).get().getUser().getId();
		// 수정할 Comment
		Community community = communityRepository.findById(communityUpdateRequestDto.getCommunityId()).get();
		
		if(userId != community.getUser().getId()) return false;
		// 커뮤니티 글 제목, 내용 update
		community.update(communityUpdateRequestDto.getTitle(), communityUpdateRequestDto.getContent());
		
		communityRepository.save(community);
		
		return true;
	}
	
	@Override
	public CommunityGetListWrapperResponseDto getCommunityList(String accessToken, int page) {
		String loginId = getLoginIdFromToken(accessToken);
		Auth auth = authRepository.findByLoginId(loginId).get();
		User user = auth.getUser();
		UserDepartment userDepartment = userDepartmentRepository.findByUser(user).get();
		School school = userDepartment.getSchool();
		
		List<CommunityGetListResponseDto> list = new ArrayList<>();
		
		for(Community c : communityRepository.findBySchoolAndDelYnOrderByRegTimeDesc(school, false, PageRequest.of(page-1, 10, Sort.Direction.DESC, "regTime")).get()) {
			list.add(
				new CommunityGetListResponseDto(c.getUser().getId(), c.getUser().getUserName(),
				c.getTitle(), c.getHit(), c.getRegTime(), c.getId())
			);
		}
		
		return new CommunityGetListWrapperResponseDto(list);
	}
	
	@Override
	public CommunityGetResponseDto getCommunity(UUID communityId) {
		Community community = communityRepository.findById(communityId).get();
		if(community.isDelYn()) return null;
		
		plusCommunityHit(community);
		
		CommunityGetResponseDto communityGetResponseDto = 
				new CommunityGetResponseDto(community.getUser().getId(), 
						community.getUser().getUserName(),
						community.getTitle(),
						community.getContent(),
						community.getHit(),
						community.getRegTime());
		
		System.out.println(community.getUser().getUserName());
		return communityGetResponseDto;
	}
	
	@Override
	public TotalCommunityGetResponseDto getTotalCommunity(String accessToken) {
		String loginId = getLoginIdFromToken(accessToken);
		Auth auth = authRepository.findByLoginId(loginId).get();
		User user = auth.getUser();
		UserDepartment userDepartment = userDepartmentRepository.findByUser(user).get();
		School school = userDepartment.getSchool();
		
		List<Community> list = communityRepository.findBySchoolAndDelYn(school, false).get();
		
		return new TotalCommunityGetResponseDto(list.size());
	}
	
	@Override
	public void plusCommunityHit(Community community) {
		community.updateHit();
		communityRepository.save(community);
	}
	
	@Override
	public boolean deleteCommunity(String accessToken, UUID communityId) {
		String loginId = getLoginIdFromToken(accessToken);
		// 삭제 요청자 userId
		UUID userId = authRepository.findByLoginId(loginId).get().getUser().getId();
		// 삭제할 Community 객체
		Community community = communityRepository.findById(communityId).get();
		
		if(userId != community.getUser().getId()) return false;
		
		// community 삭제처리 - delYn=true
		community.update(true);
		communityRepository.save(community);
		
		return true;
	}
	
	@Override
	public String getLoginIdFromToken(String accessToken) {
		String token = accessToken.split(" ")[1];
		return jwtAuthenticationProvider.getUsername(token);
	}
	
}
