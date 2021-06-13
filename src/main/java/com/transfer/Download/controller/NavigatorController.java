package com.transfer.Download.controller;

import com.transfer.Download.entity.NavData;
import com.transfer.Download.entity.NavFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
public class NavigatorController {

    @Value("${home.path}")
    private String homePath;

    @Autowired
    private FileOperationController fileOps;

    private final static String BLOB_PATH = "BLOB_PATH";

    @GetMapping("/navigate")
    public ResponseEntity<NavData> navigatesFiles(@RequestParam("path") String filePath, HttpServletResponse response) throws Exception {
        return new ResponseEntity<NavData>(getNavFiles(filePath), HttpStatus.ACCEPTED);
    }

    @GetMapping("/download")
    public InputStreamResource downloadFile(@RequestParam("path") String filePath, HttpServletResponse response) throws Exception {
        validatePath(filePath);
        filePath = filePath.replace(BLOB_PATH, homePath);
        return fileOps.getFileStream(filePath, response);
    }

    private NavData getNavFiles(String filePath) throws Exception {
        validatePath(filePath);
        String newParentPath = filePath.replace(BLOB_PATH, homePath);
        File parent = new File(newParentPath);
        List<NavFiles> navFiles = new ArrayList<>();
        if (parent.isDirectory()) {
            File[] files = parent.listFiles();
            for (File file : files) {
                navFiles.add(new NavFiles(file.getName(), file.getAbsolutePath().replace("\\","/").replace(homePath, BLOB_PATH), file.isDirectory()));
            }
        }
        String backDir = newParentPath;
        if (!backDir.equals(homePath)) backDir = parent.getParentFile().getAbsolutePath();
        backDir = backDir.replace("\\","/").replace(homePath, BLOB_PATH);
        return new NavData(backDir, navFiles);
    }

    private void validatePath(String filePath) throws Exception {
        if (!filePath.contains(BLOB_PATH)) {
            throw new Exception("Not valid dirs");
        }
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}

