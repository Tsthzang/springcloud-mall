package com.zang.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.categoryproduct.common.ProductConstant;
import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.categoryproduct.request.AddProductReq;
import com.zang.cloud.mall.categoryproduct.request.UpdateProductReq;
import com.zang.cloud.mall.categoryproduct.service.ProductService;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 后台商品管理接口
 */
@RestController
public class ProductAdminController {

    @Resource
    ProductService productService;

    @Value("${file.upload.ip}")
    String ip;

    @Value("${file.upload.port}")
    Integer port;

    /**
     * 添加商品接口
     */
    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    /**
     * 上传图片接口
     */
    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成新的UUID类型文件名
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        //创建文件
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);
        if(!fileDirectory.exists()){
            if(!fileDirectory.mkdir()){
                throw new ImoocException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        //将上传的文件存放到本地目录
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回给前端数据库中的路径
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL() + "")) +"/category-product/images/"+ newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    /**
     * uri转换成想返回给前端的方法
     * @param uri
     * @return
     */
    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), ip, port,
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    /**
     * 更新商品接口
     */
    @PostMapping("admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    /**
     * 删除商品接口
     */
    @PostMapping("admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    /**
     * 批量上下架商品接口
     */
    @PostMapping("admin/product/batchUpdateSellStauts")
    public ApiRestResponse batchUpdateSellStauts(@RequestParam Integer[] ids, @RequestParam Integer sellStauts) {
        productService.batchUpdateSellStauts(ids, sellStauts);
        return ApiRestResponse.success();
    }

    /**
     * 后台商品列表接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

}
