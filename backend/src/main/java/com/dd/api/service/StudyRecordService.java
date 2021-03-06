package com.dd.api.service;

import java.time.LocalDate;
import java.util.UUID;

import com.dd.api.dto.request.StudyRecordRegistRequestDto;
import com.dd.api.dto.request.StudyRecordUpdateRequestDto;
import com.dd.api.dto.response.StudyRecordFinishResponseDto;
import com.dd.api.dto.response.StudyRecordGetListWrapperResponseDto;
import com.dd.api.dto.response.StudyRecordResponseDto;
import com.dd.api.dto.response.StudyRecordStartResponseDto;
import com.dd.db.entity.addon.StudyRecord;

public interface StudyRecordService {
	StudyRecordStartResponseDto createStudyRecord(String accessToken, StudyRecordRegistRequestDto studyRecordRegistRequestDto);
	StudyRecordFinishResponseDto finishStudyRecord(UUID studyRecordId);
	StudyRecordGetListWrapperResponseDto getStudyRecordListByDate(String accessToken, LocalDate studyDate);
	StudyRecordResponseDto getStudyRecord(UUID studyRecordId);
	StudyRecord deleteStudyRecord(UUID studyRecordId);
	StudyRecord updateStudyRecord(StudyRecordUpdateRequestDto studyRecordUpdateRequestDto);
}
