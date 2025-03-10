package com.archisemtle.semtlewebserverspring.dto.archive;

import com.archisemtle.semtlewebserverspring.domain.archive.Archive;
import com.archisemtle.semtlewebserverspring.vo.archive.ArchiveResponseVo;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArchiveResponseDto {

    private Long board_id;
    private String writer;
    private String content;
    private String title;
    private Date createdAt;
    private List<String> imageUrl;
    private List<String> fileUrl;

    @Builder
    public ArchiveResponseDto(Archive archive) {
        writer = archive.getWriter();
        content = archive.getContent();
        title = archive.getTitle();
        board_id = archive.getId();
        createdAt = archive.getCreatedAt();
        imageUrl = archive.getImageUrl();
        fileUrl = archive.getFileUrl();
    }

    public static ArchiveResponseVo dtoToVo(ArchiveResponseDto responseDto){
        return ArchiveResponseVo.builder()
            .writer(responseDto.getWriter())
            .content(responseDto.getContent())
            .title(responseDto.getTitle())
            .createdAt(responseDto.getCreatedAt())
            .board_id(responseDto.getBoard_id())
            .imageUrl(responseDto.getImageUrl())
            .fileUrl(responseDto.getFileUrl())
            .build();
    }



}
