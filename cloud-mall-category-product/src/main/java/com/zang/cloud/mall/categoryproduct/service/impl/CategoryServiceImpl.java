package com.zang.cloud.mall.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.categoryproduct.model.dao.CategoryMapper;
import com.zang.cloud.mall.categoryproduct.model.pojo.Category;
import com.zang.cloud.mall.categoryproduct.request.AddCategoryReq;
import com.zang.cloud.mall.categoryproduct.service.CategoryService;
import com.zang.cloud.mall.categoryproduct.vo.CategoryVo;
import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：目标分类功能
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    CategoryMapper categoryMapper;

    /**
     * 后台添加分类目录
     * @param addCategoryReq
     */
    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selecteByName(addCategoryReq.getName());
        if (categoryOld != null) {
            throw new ImoocException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    /**
     * 后台更新分类目录
     * @param updateCategory
     */
    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selecteByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new ImoocException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 后台删除分类目录
     * @param id
     */
    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            throw new ImoocException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 后台列表分页显示
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    /**
     * 递归展示用户分类列表
     * @return
     */
    @Override
    @Cacheable(value = "listForCustomer")
    public List<CategoryVo> listForCustomer(Integer parentId) {
        List<CategoryVo> categoryVoList = new ArrayList<>();
        recursivelyFindCategories(categoryVoList, parentId);
        return categoryVoList;
    }
    private void recursivelyFindCategories(List<CategoryVo> categoryVoList, Integer parentId) {
        //递归获取所有子类别,并组合成一个"目录树"
        List<Category> categoryList = categoryMapper.selectByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(category, categoryVo);
                categoryVoList.add(categoryVo);
                recursivelyFindCategories(categoryVo.getChildCategory(), categoryVo.getId());
            }
        }
    }
}
