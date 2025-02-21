package com.archisemtle.semtlewebserverspring.application.activity;

import com.archisemtle.semtlewebserverspring.dto.activity.ActivityRequestDto;
import com.archisemtle.semtlewebserverspring.dto.activity.ActivityResponseDto;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ActivityService {

    public void createActivityBoard(ActivityRequestDto requestDto) throws IOException;
    public ActivityResponseDto readActivityBoard(Long id);
    public void updateActivityBoard(Long id, ActivityRequestDto requestDto);
    public void deleteActivityBoard(Long id);
}
