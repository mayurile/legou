package com.legou.controller.backend;

import com.google.common.collect.Maps;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.Product;
import com.legou.pojo.User;
import com.legou.service.IFileService;
import com.legou.service.IProductService;
import com.legou.service.IUserService;
import com.legou.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/6.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("/save.do")
    @ResponseBody
    public ServiceResponse productsave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //产品进行添加或更新的操作
            return iProductService.productsaveorupdate(product);
        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }

    @RequestMapping("/updatestatus.do")
    @ResponseBody
    public ServiceResponse productstatusupdate(HttpSession session, Integer productid, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //产品状态更新
            return iProductService.updateproductstatus(productid, status);
        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }

    @RequestMapping("/showdetail.do")
    @ResponseBody
    public ServiceResponse productdetail(HttpSession session, Integer productid) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //显示产品的信息
            return iProductService.manageProductdetail(productid);
        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }

    /**
     * 分页全显示
     *
     * @param session
     * @param pagenum  当前页数
     * @param pagesize 每页的个数
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServiceResponse productList(HttpSession session, @RequestParam(value = "pagenum", defaultValue = "1") int pagenum, @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //动态分页显示
            return iProductService.getproductlist(pagenum, pagesize);
        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }


    @RequestMapping("/search.do")
    @ResponseBody
    public ServiceResponse productSearch(HttpSession session, String name, Integer productid, @RequestParam(value = "pagenum", defaultValue = "1") int pagenum, @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //动态分页查询
            /// TODO: 2017/11/6 完成业务 
            return iProductService.productsearch(name, productid, pagenum, pagesize);
        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }

    @RequestMapping("/upload.do")
    @ResponseBody
    public ServiceResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        //从request的session中拿到serlet的上下文，放到上传文件夹upload中
        //创建到webapp的webinf同级
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), "用户未登录，请先登录再完成操作");
        }
        if (iUserService.checkadminrole(user).issuccess()) {
            //动态分页查询

            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            //与前端约定将url拼装出来
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServiceResponse.createbysuccess(fileMap);

        } else {
            return ServiceResponse.createbyerror("无操作权限");
        }
    }
    @RequestMapping("/richtext_img_upload.do")
    @ResponseBody
    public Map imgupload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Map resultmap=Maps.newHashMap();

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultmap.put("success",false);
            resultmap.put("msg","请登录管理员");
            return resultmap;
        }
        //按照simditor进行返回
        if (iUserService.checkadminrole(user).issuccess()) {
            //动态分页查询

            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isNotBlank(targetFileName)){
                resultmap.put("success",false);
                resultmap.put("msg","上传失败");
                return resultmap;
            }
            //与前端约定将url拼装出来
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultmap.put("success",false);
            resultmap.put("msg","上传成功");
            resultmap.put("fileMap",url);
            httpServletResponse.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultmap;

        } else {
            resultmap.put("success",false);
            resultmap.put("msg","无权限操作");
            return resultmap;
        }
    }
}

