package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * @description: 商品信息查询类
 * @param: null
 * @return:
 * @author BianCheng
 * @date: 2023/12/13 15:25
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    public IShopService shopService;

    /**
     * @description: 根据商品id查询商品
     * {
     *     考虑了缓存击穿的情况，当需要查询的数据在redis和数据库中都不存在时。
     * }
     * @param: id 商品id
     * @return: 商品信息
     */
    @Override
    public Result getShopById(Long id) {
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        if(!StrUtil.isBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        if(shopJson != null){
            return Result.fail("店铺信息不存在");
        }
        Shop shop = shopService.getById(id);
        if(shop==null){
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("商品信息不存在");
        }
        shopJson = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,shopJson,CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok(shop);
    }

    @Override
    public Result updateShopInfo(Shop shop) {
        Long id = shop.getId();
        if(id == null){
            return Result.fail("商铺id为空");
        }
        shopService.updateById(shop);
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }
}
