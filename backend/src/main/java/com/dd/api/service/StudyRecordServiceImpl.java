package com.dd.api.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.dd.api.dto.request.StudyRecordRegistRequestDto;
import com.dd.api.dto.response.StudyRecordFinishResponseDto;
import com.dd.db.entity.addon.StudyRecord;
import com.dd.db.entity.user.User;
import com.dd.db.repository.StudyRecordRepository;

import lombok.RequiredArgsConstructor;

@Service("studyRecordService")
@RequiredArgsConstructor
public class StudyRecordServiceImpl implements StudyRecordService {

	private final JwtTokenService jwtTokenService;
	
	private final StudyRecordRepository studyRecordRepository;
	
	@Transactional
	@Override
	public StudyRecord createStudyRecord(String accessToken, StudyRecordRegistRequestDto studyRecordRegistRequestDto) {
		User user = jwtTokenService.convertTokenToUser(accessToken);
		LocalDate studyDate = LocalDate.now();
		LocalDateTime startTime =  LocalDateTime.now();
		
		StudyRecord studyRecord = StudyRecord.builder()
				.studyDate(studyDate)
				.startTime(startTime)
				.title(studyRecordRegistRequestDto.getTitle())
				.user(user)
				.build();
		
		return studyRecordRepository.save(studyRecord);
	}

	@Override
	public StudyRecordFinishResponseDto finishStudyRecord(UUID studyRecordId) {
		StudyRecord studyRecord = studyRecordRepository.findById(studyRecordId).get();
		LocalDateTime endTime = LocalDateTime.now();
		
		studyRecord.finishStudyRecord(endTime);
		studyRecordRepository.save(studyRecord);
		
		Duration duration = Duration.between(studyRecord.getStartTime(), studyRecord.getEndTime());
		long hours = duration.toHours();
		long minutes = duration.toMinutes() - hours * 60;
		StudyRecordFinishResponseDto studyRecordFinishResponseDto = StudyRecordFinishResponseDto.builder()
				.title(studyRecord.getTitle())
				.durationTime(String.format("%02d", hours) + ":" + String.format("%02d", minutes))
				.build();
		
		return studyRecordFinishResponseDto;
	}

}
