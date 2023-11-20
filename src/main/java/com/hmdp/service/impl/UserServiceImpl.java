package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.RegexUtils.isPhoneInvalid;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 发送验证码，并保存在session中
     * @param phone
     * @param session
     * @return
     */
    @Override
    public Result sendcode(String phone, HttpSession session) {
        if(isPhoneInvalid(phone)){
            return Result.fail("手机号码有误");
        }
        String code = RandomUtil.randomString(4);
        session.setAttribute("code",code);
        log.debug("code:"+ code);
        return Result.ok("验证码发送成功");
    }

    @Override
    public Result login(String code, String phone, HttpSession session) {
        String true_code = session.getAttribute("code").toString();
        if(true_code==null || !true_code.equals(code)){
            return Result.fail("验证码错误");
        }
        User user = query().eq("phone",phone).one();
        if(user == null){
            user = createUserWithPhone(phone);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickName(user.getNickName());
        userDTO.setIcon(user.getIcon());
        session.setAttribute("user",userDTO);
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(8));
        save(user);
        return user;
    }
}
