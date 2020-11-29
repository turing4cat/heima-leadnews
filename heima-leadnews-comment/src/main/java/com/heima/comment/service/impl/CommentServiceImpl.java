package com.heima.comment.service.impl;

import com.heima.comment.feign.UserClient;
import com.heima.comment.service.CommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.comment.pojos.ApComment;
import com.heima.model.comment.pojos.ApCommentLike;
import com.heima.model.comment.vo.ApCommentVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojo.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserClient userClient;
    /**
     * 保存评论
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        //用户的评论具体逻辑  参数判断
        if (dto.getArticleId()==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //评论内容不能超过140个字
        if (dto.getContent()!=null && dto.getContent().length()>140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论内容不能超过140个字");
        }
        //todo 此处调用阿里云的接口对评论内容进行校验
        //判断是否登录
        ApUser user = AppThreadLocalUtils.getUser();
        if (user==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"登录后评论");
        }
        //保存评论
        ApUser userById = userClient.findUserById(user.getId());
        ApComment apComment = new ApComment();
        apComment.setAuthorId(userById.getId());
        apComment.setAuthorName(userById.getName());
        apComment.setContent(dto.getContent());
        apComment.setCreatedTime(new Date());
        apComment.setEntryId(dto.getArticleId());
        apComment.setFlag((short)0);
        apComment.setImage(userById.getImage());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short)0);
        mongoTemplate.save(apComment);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 点赞某一条评论
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //参数判断
        if (dto==null || dto.getCommentId()==null) {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断用户是否登陆
        ApUser user = AppThreadLocalUtils.getUser();
        if (user==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"请登录后进行操作");
        }
        //查询当前评论是否存在
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        if (apComment==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"该条评论不存在，或者已经被作者删除");
        }
        if (apComment!=null && dto.getOperation()==0){
            //点赞操作  需要给评论表新增1 并且记录是谁点的
            apComment.setLikes(apComment.getLikes()+1);
            mongoTemplate.save(apComment);
            //记录当前用户
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setAuthorId(user.getId());
            apCommentLike.setCommentId(apComment.getId());
            mongoTemplate.save(apCommentLike);
        }else {
            //取消点赞
            //修改点赞的数量
            apComment.setLikes(apComment.getLikes() <0 ? 0 : apComment.getLikes()-1);
            mongoTemplate.save(apComment);
            //删除当前用户的点赞信息 根据 authorId  commentId
            mongoTemplate.remove(Query.query(Criteria.where("authorId").is(user.getId()).and("commentId").is(apComment.getId())));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("likes",apComment.getLikes());
        return ResponseResult.okResult(map);
    }

    /**
     * 查询评论
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findByArticleId(CommentDto dto) {
        //判断参数
        if (dto==null || dto.getArticleId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (dto.getSize()==null || dto.getSize()==0) {
            dto.setSize(20);
        }
        //查询评论
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        //分页条件构造 按着时间倒序排列  最新的放到最上面   //分页排序
        query.limit(dto.getSize()).with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<ApComment> apComments = mongoTemplate.find(query, ApComment.class);
        //查看用户  登录看到自己的点赞高亮  没有登录就展示所有的点赞
        ApUser user = AppThreadLocalUtils.getUser();
        if (user==null) {
            return ResponseResult.okResult(apComments);
        }
        //登录后的显示
        //所有的点赞id信息 用于查询当前用户的点赞列表 是否包含
        List<String> apCommentIds = apComments.stream().map(x -> x.getId()).collect(Collectors.toList());
        //构造点赞表的查询条件
        Query query1 = Query.query(Criteria.where("commentId").in(apCommentIds).and("authorId").is(user.getId()));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);
        //结果
        ArrayList<ApCommentVo> apCommentVos = new ArrayList<>();
        if (apComments!=null && apCommentLikes!=null) {
            //结果封装
            apComments.stream().forEach(x->{
                ApCommentVo apCommentVo = new ApCommentVo();
                //数据结果封装
                BeanUtils.copyProperties(x,apCommentVo);
                for (ApCommentLike apCommentLike : apCommentLikes) {
                    //判断当前的用户有没有点赞
                    if (x.getId().equals(apCommentLike.getCommentId())) {
                        apCommentVo.setOperation((short)0);
                    }
                }
               apCommentVos.add(apCommentVo);
            });
            return ResponseResult.okResult(apCommentVos);
        }else {
            //当前的用户没有点赞的信息  直接返回评论列表就可以了
            return ResponseResult.okResult(apComments);
        }

    }
}
