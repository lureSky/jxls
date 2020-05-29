package com.martin.util;

import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/5/29 0029
 */
public class JxlsUtils {

    private static final String TEMPLATE_PATH = "template";

    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {
        //初始化excel表，深入底层可以看到主要是使用了poiUtil
        Context context = PoiTransformer.createInitialContext();
        if (model != null) {
            for (String key : model.keySet()) {
                //model是什么？  model使我们使用的数据，excel title下面展示的数据
                context.putVar(key, model.get(key));
            }
        }

        //创建协助类，使用实例化
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        //传入模版的输入流，一个输出流，
        Transformer transformer  = jxlsHelper.createTransformer(is, os);
        //获得配置 ，从开始符号-结束符号  eg: ${info.name}
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
        /**
         设置静默模式，不报警告
         什么叫静默模式？
            静默模式是导出excel的时候，如果在map中没有对应的数值，会打印xxx标签没有赋值，如果开启静默则不会报告
         */
        //evaluator.getJexlEngine().setSilent(true);
        //函数强制，自定义功能
        Map<String, Object> funcs = new HashMap<String, Object>();
        //使用自定义的utils，与第一步的PoiTransformer.createInitialContext();内部使用默认的不同
        funcs.put("utils", new JxlsUtils());    //添加自定义功能
        //获取jxel引擎，并设置自定义参数
        evaluator.getJexlEngine().setFunctions(funcs);
        //必须要这个，否者表格函数统计会错乱 快速公式处理为false，使用context和transformer进行匹配
        //函数统计错乱，excel自带的 函数统计相加 会加错地方
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }

    public static void exportExcel(File xls, File out, Map<String, Object> model) throws FileNotFoundException, IOException {
        exportExcel(new FileInputStream(xls), new FileOutputStream(out), model);
    }

    public static void exportExcel(String templateName, Map<String, Object> model, HttpServletResponse response, HttpServletRequest request, String filename) throws FileNotFoundException, IOException {
        // 定义输出类型
        response.setContentType("application/vnd.ms-excel");
        //解决获得中文参数的乱码
        filename = new String(filename.getBytes("UTF-8"),"ISO-8859-1");

        //获得请求头中的User-Agent
        String agent = request.getHeader("User-Agent");
        //根据不同浏览器进行不同的编码
        String filenameEncoder = "";
        if (agent.contains("MSIE")) {
            // IE浏览器
            filenameEncoder = URLEncoder.encode(filename, "utf-8");
            filenameEncoder = filenameEncoder.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            BASE64Encoder base64Encoder = new BASE64Encoder();
            filenameEncoder = "=?utf-8?B?"
                    + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
        } else {
            // 其它浏览器
            filenameEncoder = URLEncoder.encode(filename, "utf-8");
        }
        // 设定输出头文件
        response.setHeader("Content-disposition", "attachment;filename=" + filename);
        OutputStream os = response.getOutputStream();
        //    File template = getTemplate(templateName);
        StringBuffer stringBuffer = new StringBuffer();
        InputStream stream = null;
        try {
            stream = JxlsUtils.class.getClassLoader().getResourceAsStream(TEMPLATE_PATH+ "/" + templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(stream!=null){
            exportExcel(stream, os, model);
        }
    }
    /*public static void exportExcel(String templatePath, OutputStream os, Map<String, Object> model) throws Exception {
        File template = getTemplate(templatePath);
        if(template != null){
            exportExcel(new FileInputStream(template), os, model);
        } else {
            throw new Exception("Excel 模板未找到。");
        }
    }*/

    //获取jxls模版文件
    public static File getTemplate(String path){
        File template = new File(path);
        if(template.exists()){
            return template;
        }
        return null;
    }

    // 日期格式化
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
            return dateFmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // if判断
    public Object ifelse(boolean b, Object o1, Object o2) {
        return b ? o1 : o2;
    }
}
