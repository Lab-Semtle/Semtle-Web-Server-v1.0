package com.archisemtle.semtlewebserverspring.presentation;


import com.archisemtle.semtlewebserverspring.application.PromotionService;
import com.archisemtle.semtlewebserverspring.common.CommonResponse;
import com.archisemtle.semtlewebserverspring.common.MessageConstants;
import com.archisemtle.semtlewebserverspring.common.utils.UserUtils;
import com.archisemtle.semtlewebserverspring.config.jwt.JwtAuthenticationFilter;
import com.archisemtle.semtlewebserverspring.dto.*;
import com.archisemtle.semtlewebserverspring.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.archisemtle.semtlewebserverspring.common.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    private final UserUtils userUtils;

    @GetMapping("")
    public ResponseEntity<CommonResponse<?>> getPromotions(
            @RequestParam(name = "keyword",  required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        if (page < 1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PAGE_NUM_MIN));
        }

        if (size < 1 || size > 100) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PAGE_NUM_MAX));
        }

        try{
            PromotionResponseDto responseDto = promotionService.getPromotions(keyword, page, size);
            //리턴문 Dto -> Vo 변경 0226
            PromotionVo responseVo = PromotionVo.dtoToVo(responseDto);
            return ResponseEntity
                    .ok(CommonResponse.success(responseVo));
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(WRONG_SERVER));
        }
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<CommonResponse<?>> getPromotionsById(
            @PathVariable("promotionId") Long id) {
        if(id == null || id<0){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PARAM));
        }
        try{
            ProjectPromotionResponseDto2 responseDto = promotionService.getPromotionsById(id);
            if(responseDto == null){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.fail(NONE_DATA));
            }else{
                return ResponseEntity
                        .ok(CommonResponse.success(responseDto));
            }
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(WRONG_SERVER));
        }
    }

    @PostMapping("")
    public ResponseEntity<CommonResponse<?>> createPromotion(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ProjectPromotionRequestDto reqDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PARAM,errorMessage));
        }
        try{
            String userUuid = userUtils.getUserUuid(authorizationHeader);
            reqDto.setUserUuid(userUuid);
            ProjectPromotionCUDResponseDto response = promotionService.mergePromotion(reqDto);
            PromotionCUDDtos.Create responseDto = new PromotionCUDDtos.Create(
                    "프로젝트 홍보 게시물이 성공적으로 등록되었습니다"
                    ,response.getBoardId()
                    ,response.getCreateDt());
            //리턴문 Dto -> Vo 변경 0226
            PromotionCreateVo responseVo = PromotionCreateVo.dtoToVo(responseDto);
            return ResponseEntity
                    .ok(CommonResponse.success(MessageConstants.PROMOTION_CREATE_SUCCESS, responseVo));
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(WRONG_SERVER));
        }
    }

    @PatchMapping("/{promotionId}")
    public ResponseEntity<CommonResponse<?>> updatePromotion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ProjectPromotionRequestDto reqDto,
            @PathVariable("promotionId") Long id,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PARAM,errorMessage));
        }
        if(id == null || id<0){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.fail(WRONG_PARAM));
        }
        try{
            String userUuid = userUtils.getUserUuid(authorizationHeader);
            reqDto.setUserUuid(userUuid);
            ProjectPromotionResponseDto2 checkPromotion = promotionService.getPromotionsById(id);
            Boolean checkPermission = promotionService.checkPermission(userUuid, id);
            if(Boolean.FALSE.equals(checkPermission)){
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(CommonResponse.fail(WRONG_USER_UPDATE));
            }
            else if(checkPromotion == null){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.fail(NOT_FOUND_DATA));
            }else{

                reqDto.setBoardId(id);
                ProjectPromotionCUDResponseDto response = promotionService.mergePromotion(reqDto);
                PromotionCUDDtos.Update responseDto = new PromotionCUDDtos.Update(
                        MessageConstants.PROMOTION_UPDATE_SUCCESS
                        ,response.getUpdateDt());
                //리턴문 Dto -> Vo 변경 0226
                PromotionUpdateVo responseVo = PromotionUpdateVo.dtoToVo(responseDto);
                return ResponseEntity
                        .ok(CommonResponse.success(MessageConstants.PROMOTION_UPDATE_SUCCESS, responseVo));
            }
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(WRONG_SERVER));
        }
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<CommonResponse<?>> deletePromotion(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("promotionId") Long id) {
        if(id == null || id<0){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.fail(WRONG_PARAM));
        }
        try{
            String userUuid = userUtils.getUserUuid(authorizationHeader);
            ProjectPromotionResponseDto2 checkPromotion = promotionService.getPromotionsById(id);
            Boolean checkPermission = promotionService.checkPermission(userUuid, id);
            if(Boolean.FALSE.equals(checkPermission)){
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(CommonResponse.fail(WRONG_USER_DELETE));
            } else if(checkPromotion == null){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.fail(NONE_DATA));
            }else{
                ProjectPromotionCUDResponseDto response = promotionService.deletePromotion(id);
                PromotionCUDDtos.Update responseDto = new PromotionCUDDtos.Update(
                        MessageConstants.PROMOTION_DELETE_SUCCESS
                        ,response.getUpdateDt());
                //리턴문 Dto -> Vo 변경 0226
                PromotionUpdateVo responseVo = PromotionUpdateVo.dtoToVo(responseDto);
                return ResponseEntity
                        .ok(CommonResponse.success(MessageConstants.PROMOTION_DELETE_SUCCESS, responseVo));
            }
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(WRONG_SERVER));
        }
    }
}
