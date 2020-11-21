package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.fastdfs.FastDFSClient;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper,WmMaterial> implements WmMaterialService {
    //图片上传
    @Autowired
    private FastDFSClient fastDFSClient;

    @Value("${fdfs.url}")
    private String fileServerUrl;
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //检查参数
        if (multipartFile==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //名称不能为空
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //上传图片到fastdfs
        String fileId=null;
        try {
            //返回存储的名字加路径
             fileId = fastDFSClient.uploadFile(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //保存到表中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUrl(fileId);
        wmMaterial.setType((short)0);
        wmMaterial.setUserId(WmThreadLocalUtils.getUser().getId());
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        //保存的url路径没有拼接数据  需要手动拼接ip地址
        wmMaterial.setUrl(fileServerUrl+fileId);
        return ResponseResult.okResult(wmMaterial);
    }
    //简单的分页条件查询
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        if (dto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //检查分页参数  没有就初始化
        dto.checkParam();
        //构建分页条件查询
        IPage page= new Page(dto.getPage(),dto.getSize());
        //构建条件
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //当前用户只能查询自己的素材
        lambdaQueryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtils.getUser().getId());
        //判断是否是点击的我的收藏
        if (dto.getIsCollection() != null && dto.getIsCollection().shortValue()==1) {
           lambdaQueryWrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }
        //对查询的结果进行到徐排列
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        IPage page1 = page(page, lambdaQueryWrapper);
        //在结果返回之前给路径添加前缀
        List<WmMaterial> records = page1.getRecords();
        records = records.stream().map(item -> {
            item.setUrl(fileServerUrl+item.getUrl());
            return item;
        }).collect(Collectors.toList());
//        for (WmMaterial record : records) {
//            record.setUrl(fileServerUrl+record.getUrl());
//        }
        //数据封装返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page1.getTotal());
        responseResult.setData(records);
        return responseResult;
    }
    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;
    @Override
    public ResponseResult delPicture(Integer id) {
        //在删除之前需要进行判断该素材有没有被使用 对应的表wm_news_material
        //判断参数
        if (id==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //先查询是否存在该素材
        WmMaterial wmMaterial = getById(id);
        if (wmMaterial==null) {
            //素材不存在
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //判断该素材有没有被引用
        Integer integer = wmNewsMaterialMapper.selectCount(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getMaterialId, id));
        if (integer>0) {
            //代表当前素材被使用不能删除
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"该素材已经被使用，不能删除");
        }
        //如果没有 则删除表中数据 和服务器端的数据
        removeById(id);
        //服务器端的数据
        fastDFSClient.delFile(wmMaterial.getUrl());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 收藏和取消收藏
     * @param id
     * @param type   0 取消收藏  1 收藏 对应的表中字段为is_collection
     * @return
     */
    @Override
    public ResponseResult updateCollection(Integer id, Short type) {
        //判断参数
        if (id==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //根据当前用户的id修改对应的是否收藏
        //修改状态构建条件 当前用户  所选图片
        update(Wrappers.<WmMaterial>lambdaUpdate().eq(WmMaterial::getUserId,WmThreadLocalUtils.getUser().getId()).eq(WmMaterial::getId,id).set(WmMaterial::getIsCollection,type));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
