package net.kk.chat.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Description
 * @Author sgl
 * @Date 2018-05-15 14:04
 */
@Controller
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("sendName") String sendName, HttpServletResponse response) {
        if(sendName.equals("") || sendName == null){
            return "请先注册账户";
        }
        //获取上传文件名,包含后缀
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(substring.equals(".jpg") || substring.equals(".png")){
            //保存的文件名
            String dFileName = sendName + ".jpg";
            //保存路径
            //springboot 默认情况下只能加载 resource文件夹下静态资源文件
            String path = "E:/Program Files/untitled/chat/uploadFiles/";
            //生成保存文件
            File uploadFile = new File(path+dFileName);
            //将上传文件保存到路径
            try {
                file.transferTo(uploadFile);
                response.sendRedirect("/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "上传"+dFileName+"成功";
        }
        return "上传失败只支持jpg || png格式图片";
    }


}