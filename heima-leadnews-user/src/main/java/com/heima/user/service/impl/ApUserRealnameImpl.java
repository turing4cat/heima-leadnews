package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.user.UserConstants.AdminConstants;
import com.heima.common.exception.CostomException;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojo.ApUser;
import com.heima.model.user.pojo.ApUserRealname;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.feign.ApAuthorFeign.ArticleFeign;
import com.heima.user.feign.WmUserFeign.WemediaFeign;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import com.sun.xml.bind.v2.TODO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApUserRealnameImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {
    @Autowired
    ArticleFeign articleFeign;
    @Autowired
    WemediaFeign wemediaFeign;
    @Autowired
    ApUserMapper apUserMapper;
    @Override
    public ResponseResult loadListByStatus(AuthDto dto) {
        //判断参数
        if (dto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页检查
        dto.checkParam();
        //构造条件
        IPage objectPage = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<ApUserRealname> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus()!=null) {
            lambdaQueryWrapper.eq(ApUserRealname::getStatus, dto.getStatus());
        }
        //查询数据
        IPage page = page(objectPage, lambdaQueryWrapper);
        //数据封装
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 审核的具体业务
     * @param dto
     * @param status  2 审核失败   9 审核成功
     * @return
     */
    @GlobalTransactional
    @Override
    public ResponseResult updateStatusById(AuthDto dto, Short status) {
        //检查参数
        if (dto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //审核修改状态
        //封装数据
        ApUserRealname apUserRealname = new ApUserRealname();
        apUserRealname.setId(dto.getId());
        apUserRealname.setStatus(status);
        //此处判断如果包含信息则是点击的通过  如果带有信息 则代表的是驳回
        if (StringUtils.isNoneBlank(dto.getMsg())) {
            apUserRealname.setReason(dto.getMsg());
        }
        //执行修改的条件
        updateById(apUserRealname);
        //此处判断是否是审核通过 如果是 则创建自媒体人和作者
        if (status.equals(AdminConstants.PASS_AUTH)) {
//            int i=1/0;
           ResponseResult responseResult= createWmUserAndAuthor(dto);
           if (responseResult!=null){
               return responseResult;
           }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 创建自媒体人和作者的信息
     * @param dto
     * @return
     */
    private ResponseResult createWmUserAndAuthor(AuthDto dto) {
        //查询认证用户的所有信息 用于得到用户id
        ApUserRealname apUserRealname = getById(dto.getId());
        //查询对应的用户信息 通过认证的实名表中对应的用户id
        ApUser apUser = apUserMapper.selectById(apUserRealname.getUserId());
        if (apUser==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //修改用户的状态为自媒体人  flag ==1
        apUser.setFlag((short) 1);
        //构建自媒体人的信息
        WmUser wmUser = wemediaFeign.findByName(apUser.getName());
        if (wmUser==null) {
            //构建信息
            wmUser=new WmUser();
            wmUser.setName(apUser.getName());
            wmUser.setName(apUser.getName());
            wmUser.setSalt(apUser.getSalt());
            wmUser.setPassword(apUser.getPassword());
            wmUser.setPhone(apUser.getPhone());
            wmUser.setCreatedTime(new Date());
            wmUser.setApUserId(apUser.getId());
            wmUser.setStatus((int)AdminConstants.PASS_AUTH);
            ResponseResult save = wemediaFeign.save(wmUser);
            //保存自媒体人
            ResponseResult result = wemediaFeign.save(wmUser);
            if(!result.getCode().equals(0)){
                throw new CostomException(ResponseResult.errorResult(AppHttpCodeEnum.AUTH_FAIL));
            }
        }
        //构建作者信息
        createApAuthor(wmUser);
        apUserMapper.updateById(apUser);
        return null;
    }

    private void createApAuthor(WmUser wmUser) {
        //先查询作者是否存在
        ApAuthor apAuthor =articleFeign.findByUserId(wmUser.getApUserId());
        if (apAuthor==null) {
            apAuthor = new ApAuthor();
            apAuthor.setName(wmUser.getName());
            apAuthor.setUserId(wmUser.getApUserId());
            apAuthor.setType(2);
            apAuthor.setCreatedTime(new Date());
            ResponseResult save = articleFeign.save(apAuthor);
            //保存作者
            ResponseResult result = articleFeign.save(apAuthor);
            if(!result.getCode().equals(0)){
                throw new CostomException(ResponseResult.errorResult(AppHttpCodeEnum.AUTH_FAIL));
            }
        }
    }
}
