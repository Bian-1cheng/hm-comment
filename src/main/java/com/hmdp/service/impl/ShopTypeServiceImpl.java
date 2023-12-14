package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private IShopTypeService typeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result shopList() {
        String shopTypeJson = stringRedisTemplate.opsForValue().get("shopType:");
        if(!StrUtil.isBlank(shopTypeJson)){
            JSONArray shopType = JSONUtil.parseArray(shopTypeJson);
            return Result.ok(shopType);
        }
        List<ShopType> shopList = typeService.query().orderByAsc("sort").list();
        stringRedisTemplate.opsForValue().set("shopType:",JSONUtil.toJsonStr(shopList));
        return Result.ok(shopList);
    }
}
