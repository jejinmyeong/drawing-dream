package com.dd.api.dto.request;

import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("CommunityUpdateRequestDto")
@NoArgsConstructor
@Getter
public class CommunityUpdateRequestDto {
	
	@ApiModelProperty(name="게시글 정보 - id")
	private UUID communityId;
	
	@ApiModelProperty(name="게시글 정보 - 제목")
	private String title;
	
	@ApiModelProperty(name="게시글 정보 - 내용")
	private String content;
}
