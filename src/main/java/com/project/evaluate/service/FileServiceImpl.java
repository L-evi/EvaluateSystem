package com.project.evaluate.service;


import cn.hutool.core.bean.BeanUtil;
import com.project.evaluate.entity.file.FileChunk;
import com.project.evaluate.entity.file.FileChunkDto;
import com.project.evaluate.vo.checkResultVo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 17:55
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * 默认存储路径前缀
     */
    private static final String filePath = "./";
    /**
     * 默认存储大小
     */
    private static final Long defaultChunkSize = 20971520L;

    @Override
    @SneakyThrows
    public Boolean uploadFile(FileChunkDto fileChunkDto) {
        if (fileChunkDto.getFile() == null) {
            throw new RuntimeException("文件不能为空");
        }
//        获取文件名称
        String fullFileName = filePath + File.separator + fileChunkDto.getFilename();
        Boolean uploadFlag;
//        根据文件数量进行分类：1、有一个分块，则为小文件  2、多个分片：则为大文件
        if (fileChunkDto.getTotalChunks() == 1) {
            uploadFlag = this.uploadSingleFile(fullFileName, fileChunkDto);
        } else {
            uploadFlag = this.uploadSharding(fullFileName, fileChunkDto);
        }
//        如果上传成功，存入数据库中
        if (uploadFlag) {
//            TODO 文件信息存储到数据库中
            System.out.println("文件上传成功");
        }
        return uploadFlag;
    }

    @SneakyThrows // 参考链接：https://blog.csdn.net/qq_22162093/article/details/115486647
    private Boolean uploadSingleFile(String fileName, FileChunkDto fileChunkDto) {
        File file = new File(fileName);
        fileChunkDto.getFile().transferTo(file);
        return Boolean.TRUE;
    }

    private Boolean uploadSharding(String fileName, FileChunkDto fileChunkDto) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw")) {
//            分片大小必须和前端匹配，否则上传会将文件损坏
            long chunkSize = fileChunkDto.getChunkSize() == 0L ? defaultChunkSize : fileChunkDto.getChunkSize().longValue();
//            偏移量：从哪一个位置开始文件写入，其值为每一片大小 * 已经存的块数
            long offset = chunkSize * (fileChunkDto.getChunkNumber() - 1);
//            定位到当前块的偏移量
            randomAccessFile.seek(offset);
//            写入
            randomAccessFile.write(fileChunkDto.getFile().getBytes());
        } catch (Exception e) {
//            throw new RuntimeException("文件写入失败");
            System.out.println("文件写入失败");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void saveFile(String fileName, FileChunkDto fileChunkDto) {
//        复制Dto对象到FileChunk中
        FileChunk fileChunk = BeanUtil.copyProperties(fileChunkDto, FileChunk.class);
        fileChunk.setFileName(fileName);
        fileChunk.setTotalChunk(fileChunk.getTotalChunk());
//        TODO 将文件分块信息存储起来
//        fileChunkService.save(fileChunk);

//        如果所有块都存储完成，则在文件记录中存储一份数据
        if (fileChunk.getChunkNumber().equals(fileChunkDto.getTotalChunks())) {
            String name = fileChunkDto.getFilename();
            MultipartFile multipartFile = fileChunkDto.getFile();

//            TODO 将文件存储信息存储到数据库中
            /*FileStorage fileStorage = new FileStorage();
            fileStorage.setRealName(file.getOriginalFilename());
            fileStorage.setFileName(fileName);
            fileStorage.setSuffix(FileUtil.getSuffix(name));
            fileStorage.setFileType(file.getContentType());
            fileStorage.setSize(dto.getTotalSize());
            fileStorage.setIdentifier(dto.getIdentifier());
            fileStorage.setFilePath(dto.getRelativePath());
            this.save(fileStorage);*/
        }
    }

    public checkResultVo check(FileChunkDto fileChunkDto) {
        checkResultVo vo = new checkResultVo();
//        根据Identity查找数据是否存在
//        TODO 数据库中寻找
        List<FileChunk> list = new ArrayList<FileChunk>();
//        如果没有找到则说明数据不存在，直接返回
        if (list.size() == 0) {
            vo.setUploaded(true);
            return vo;
        }
//        如果找到则看第一个数据是不是分片，如果不是分片，则说明文件已经上传成功
        FileChunk firstFile = list.get(0);
        if (firstFile.getTotalChunk() == 1) {
            vo.setUploaded(true);
            return vo;
        }
//        处理分片
        ArrayList<Integer> uploadFiles = new ArrayList<>();
        for (FileChunk fileChunk : list) {
            uploadFiles.add(fileChunk.getChunkNumber());
        }
        vo.setUploadedChunks(uploadFiles);
        return vo;
    }
}
