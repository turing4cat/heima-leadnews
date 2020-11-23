package com.heima.article.controller.v1;

import com.heima.apis.article.ApArticleControllerApi;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ApArticleController implements ApArticleControllerApi {
    @Autowired
    ApArticleService apArticleService;

    /**
     * 保存app文章
     *
     * @param articleDto
     * @return
     */
    @PostMapping("/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto articleDto) {
        return apArticleService.saveArtilce(articleDto);
    }
}
