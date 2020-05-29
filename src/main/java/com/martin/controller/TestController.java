package com.martin.controller;

import com.martin.entity.User;
import com.martin.util.JxlsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/5/29 0029
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("listExport")
    public void listExport(HttpServletResponse response, HttpServletRequest request) throws Exception {

        String filename = "用户模版.xls";
        List<User> list = new ArrayList<>();
        list.add(new User(1L,"张三","123456"));
        list.add(new User(2L,"李四","123456"));
        list.add(new User(3L,"王五","123456"));
        list.add(new User(4L,"赵六","123456"));

        HashMap<String,Object> model = new HashMap<>(16);
        model.put("infos",list);

        JxlsUtils.exportExcel("user-export.xls", model, response, request, filename);
    }
}
