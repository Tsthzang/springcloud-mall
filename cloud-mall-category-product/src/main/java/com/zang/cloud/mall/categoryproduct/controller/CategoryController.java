package com.zang.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.categoryproduct.model.pojo.Category;
import com.zang.cloud.mall.categoryproduct.request.AddCategoryReq;
import com.zang.cloud.mall.categoryproduct.request.UpdateCategoryReq;
import com.zang.cloud.mall.categoryproduct.service.CategoryService;
import com.zang.cloud.mall.categoryproduct.vo.CategoryVo;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {

    @Resource
    CategoryService categoryService;

    /**
     * 添加分类目录接口
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("添加分类目录接口")
    @PostMapping("admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(@Valid @RequestBody AddCategoryReq addCategoryReq) {
        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();
    }

    /**
     * 更新分类目录接口
     * @param updateCategoryReq
     * @return
     */
    @ApiOperation("更新分类目录接口")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    /**
     * 删除分类目录接口
     * @param id
     * @return
     */
    @ApiOperation("删除分类目录接口")
    @PostMapping("admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    /**
     * 后台列表分页显示接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation("后台目录列表")
    @GetMapping("admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /**
     * 递归显示所有分类(用户看)接口
     * @return
     */
    @ApiOperation("前台目录列表")
    @GetMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVo> categoryVos = categoryService.listForCustomer(0);
        return ApiRestResponse.success(categoryVos);
    }
}
