package com.transfer.Download.controller;

import java.io.*;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
public class FileOperationController {

	@Value("${save.path}")
	private String savePath;

	@GetMapping("/file")
	public InputStreamResource downloadFile(@RequestParam("path") String filePath, HttpServletResponse response) throws IOException{
		return getFileStream(filePath,response);
	}

	public InputStreamResource getFileStream(String filePath, HttpServletResponse httpServletResponse) throws IOException {
		File file = new File(filePath);
		InputStreamResource fileStream = new InputStreamResource(new FileInputStream(file));
		httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+file.getName());
		httpServletResponse.setHeader("Content-Length", Files.size(file.toPath())+"");
		return fileStream;
	}

	@PostMapping("/file")
	public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException{
		file.transferTo(new File(savePath+"/"+file.getOriginalFilename()));
		return "done";
	}

	@PostMapping("/file/v2")
	public String uploadFileV2(HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iterStream = upload.getItemIterator(request);
		while (iterStream.hasNext()) {
			FileItemStream item = iterStream.next();
			InputStream stream = item.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(savePath+"/"+item.getName()));

			System.out.println("Starting upload : " + savePath+"/"+item.getName());
			char[] bytes = new char[10000];
			if (!item.isFormField()) {
				int read;
				while((read = bufferedReader.read(bytes))!=-1){
					bufferedWriter.write(read);
				}

				System.out.println("Done : " + savePath+"/"+item.getName());
			} else {
				System.out.println("not valid input");
			}
		}
		return "done";
	}
	
}
