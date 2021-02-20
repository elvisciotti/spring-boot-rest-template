package io.github.elvisciotti.controllers;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HealthCheckController {

    @Autowired
    private Environment environment;

    @GetMapping("health-check")
    public Map getStatus() throws IOException {

        Map map = new HashMap<String, String>();
        map.put("version", "30dec");
        map.put("activeProfile", environment.getActiveProfiles()[0]);

        return map;
    }
}
