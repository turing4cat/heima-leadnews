package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojo.ApUser;
import com.heima.model.user.pojo.ApUserFan;
import com.heima.model.user.pojo.ApUserFollow;
import com.heima.user.feign.ApAuthorFeign.ArticleFeign;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.service.ApUserRelationService;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApUserRelationServiceImpl implements ApUserRelationService {
    @Autowired
    ArticleFeign articleFeign;
    @Autowired
    ApUserFollowMapper apUserFollowMapper;
    @Autowired
    ApUserFanMapper apUserFanMapper;
    /**
     * 关注或取消关注
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult follow(UserRelationDto dto) {
        //判断参数
        if (dto==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //前端传入的参数  作者id 文章id 操作方式
        //根据作者id查询作者的信息
        ApAuthor  apAuthorDB= articleFeign.findById(dto.getAuthorId());
        if (apAuthorDB==null){
            ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"作者不存在");
        }
//        获取当前用户
        ApUser user = AppThreadLocalUtils.getUser();
        //关注  operation 为0
        if (dto.getOperation()==0){
            ResponseResult responseResult= followByUserId(dto, apAuthorDB, user);
            return responseResult;
        }else {
            //取消关注
            //删除表关系  追随表  和粉丝表
            ResponseResult responseResult= followCancelByUserId(dto.getAuthorId(),user.getId());
            return responseResult;
        }
        //构造表关系  作者表ap_user_fan 用户表ap_user_follow
    }

    /**
     * 取消关注
     * @param authorId
     * @param uid
     * @return
     */
    private ResponseResult followCancelByUserId(Integer authorId, Integer uid) {
        if (authorId==null||uid==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUserFollow apUserFollow = apUserFollowMapper.selectOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getFollowId, authorId).eq(ApUserFollow::getUserId, uid));
        if (apUserFollow==null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"未关注");
        //删除追随者表
        apUserFollowMapper.delete(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getFollowId,authorId).eq(ApUserFollow::getUserId, uid));
        //查询粉丝
        ApUserFan apUserFan = apUserFanMapper.selectOne(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getFansId, uid).eq(ApUserFan::getUserId, authorId));
        if (apUserFan==null) ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"粉丝不存在");

        //删除粉丝表
        apUserFanMapper.delete(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getFansId,uid).eq(ApUserFan::getUserId,authorId));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 构建关注的关系
     * @param dto
     * @param apAuthorDB
     * @param user
     * @return
     */
    private ResponseResult followByUserId(UserRelationDto dto, ApAuthor apAuthorDB, ApUser user) {
        //构建之前先查询如果有就不构建关系
        ApUserFollow apUserFollow1 = apUserFollowMapper.selectOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getFollowId, apAuthorDB.getId()).eq(ApUserFollow::getUserId, user.getId()));
        if (apUserFollow1!=null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"已经关注");
        }
        ApUserFollow apUserFollow = new ApUserFollow();
        apUserFollow.setCreatedTime(new Date());
        apUserFollow.setFollowId(dto.getAuthorId());
        apUserFollow.setFollowName(apAuthorDB.getName());
        apUserFollow.setLevel((short)1);
        apUserFollow.setIsNotice(true);
        apUserFollow.setUserId(user.getId());
//            修改追随表
        apUserFollowMapper.insert(apUserFollow);
        //修改粉丝表
        ApUserFan apUserFan = new ApUserFan();
        apUserFan.setCreatedTime(new Date());
        apUserFan.setFansId(user.getId().longValue());
        apUserFan.setFansName(user.getName());
        apUserFan.setIsDisplay(true);
        apUserFan.setIsShieldComment(false);
        apUserFan.setIsShieldLetter(false);
        apUserFan.setUserId(dto.getAuthorId());
        apUserFan.setLevel((short)1);
        apUserFanMapper.insert(apUserFan);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
