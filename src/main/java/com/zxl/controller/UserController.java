package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zxl.Utils.ValidateCodeUtils;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import com.zxl.entity.User;
import com.zxl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码,并保存到session中
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("code:"+code);
            session.setAttribute(phone,code);
        return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    /**
     * 登录,由于前端返回的是json格式，且里面有个code值，这个值User类里没有，因此不能使用user接受，可以直接使用map，也可以使用UserDto来实现。
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){


        /*
         * 在Java中，map.get("phone") 返回的是一个泛型类型
         * Object。因为Map 的值可以是任意类型的对象，而在这种情况下，
         * phone 的具体类型可能是 String、Integer、Double 等等。
         */
        //1.获取手机号
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中过去验证码和输入的验证码比对
        Object codeInSession = session.getAttribute(phone);

        if(codeInSession!=null && codeInSession.equals(code)){
            //查看数据库表中是否有这个用户，
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(phone!=null,User::getPhone,phone);

            User one = userService.getOne(queryWrapper);
            if(one==null){
                //注册
                User user1 = new User();
                user1.setPhone(phone);
                userService.save(user1);
                session.setAttribute("user",user1.getId());
            }else{
                session.setAttribute("user",one.getId());
            }

            return R.success("登录成功");
        }

        return R.error("验证码错误！");
    }


}
