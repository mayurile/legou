package com.legou.service.Impl;

import com.google.common.collect.Lists;
import com.legou.service.IFileService;
import com.legou.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/11/7.
 */
@Service("iFileService")
public class IFileServiceimpl implements IFileService {
    Logger logger= LoggerFactory.getLogger(IFileService.class);


    /**
     * 文件上传,返回上传的文件名
     * @param file
     * @param path 文件路径
     * @return
     */
    public String upload(MultipartFile file,String path){
        //获取上传文件的文件名
        String filename=file.getOriginalFilename();
        //获取扩展名，从尾部开始找以一个.，往右移一位
        String fileExtension=filename.substring(filename.lastIndexOf(".")+1);
        //建立上传的文件名
        String uploadfilename= UUID.randomUUID()+"."+fileExtension;
        logger.info("开始上传文件，上传文件名:{},上传路径:{},新文件名:{}",filename,path,uploadfilename);

        File filedir=new File(path);
        //如果路径不存在，创建该路径
        if(!filedir.exists()){
            //设置权限为可写
            filedir.setWritable(true);
            filedir.mkdir();
        }
        File targetfile=new File(path,uploadfilename);
        try {
            file.transferTo(targetfile);
            //将target文件上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetfile));
            //上传完成后删除upload中的文件
            targetfile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return  targetfile.getName();


    }
}
