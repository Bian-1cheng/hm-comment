package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 商品信息查询
 * @param: id 商品id
 * @return:
 * @author BianCheng
 * @date: 2023/12/13 15:23
 */
public interface IShopService extends IService<Shop> {

    Result getShopById(Long id);
}
