package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialControllerApi {

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);
    /**
     * 查询素材列表
     * @param dto
     * @return
     */
    public ResponseResult findList(WmMaterialDto dto);
    /**
     * 删除图片
     * @param id
     * @return
     */
    ResponseResult delPicture(Integer id);
    /**
     * 取消收藏
     * @param id
     * @return
     */
    public ResponseResult cancelCollectionMaterial(Integer id);

    /**
     * 收藏
     * @param id
     * @return
     */
    public ResponseResult collectionMaterial(Integer id);
}