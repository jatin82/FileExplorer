package com.transfer.Download.controller;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
public class FileOperationController {

    @Value("${save.path}")
    private String savePath;

    @GetMapping("/file")
    public InputStreamResource downloadFile(@RequestParam("path") String filePath, HttpServletResponse response) throws IOException {
        return getFileStream(filePath, response);
    }

    public InputStreamResource getFileStream(String filePath, HttpServletResponse httpServletResponse) throws IOException {
        File file = new File(filePath);
        InputStreamResource fileStream = new InputStreamResource(new FileInputStream(file));
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        httpServletResponse.setHeader("Content-Length", Files.size(file.toPath()) + "");
        return fileStream;
    }

    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        file.transferTo(new File(savePath + "/" + file.getOriginalFilename()));
        return "done";
    }

    @PostMapping("/file/v2")
    public String uploadFileV2(HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException, ExecutionException, InterruptedException {
        int length = request.getContentLength();
        ServletFileUpload upload = new ServletFileUpload();
        int bytesTransferredTillNow = 0;
        FileItemIterator iterStream = upload.getItemIterator(request);
        while (iterStream.hasNext()) {
            FileItemStream item = iterStream.next();
            String name = item.getName();
            InputStream stream = item.openStream();
            System.out.println("Starting upload : " + savePath + "/" + name);
            bytesTransferredTillNow = copy(stream, new FileOutputStream(savePath + "/" + name), length, bytesTransferredTillNow);
//            FileCopyUtils.copy(stream, new FileOutputStream(savePath + "/" + name));
            System.out.println("Done : " + savePath + "/" + name);
        }
        return "done";
    }


    public int copy(InputStream in, OutputStream out, int totalLength, int bytesTransferredTillNow) throws IOException {
        int byteCount = 0;
        int bytesRead;
        for (byte[] buffer = new byte[4096]; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
            out.write(buffer, 0, bytesRead);
            bytesTransferredTillNow += bytesRead;
            double percentage = (double) bytesTransferredTillNow / totalLength;
            System.out.print("completed : " + roundOff(percentage, 4) + " % \r");
        }

        out.flush();
        return bytesTransferredTillNow;
    }

    private double roundOff(double value, int roundOff) {
        double pow = Math.pow(10, roundOff);
        int roundedOff = (int) (value * pow);
        return 100D * roundedOff / pow;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
