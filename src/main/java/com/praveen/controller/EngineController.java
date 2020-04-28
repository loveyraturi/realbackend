package com.praveen.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.praveen.model.Versions;
import com.praveen.service.VersionsService;

@RestController
@RequestMapping("/praveen")
public class EngineController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private VersionsService versionsService;

	@GetMapping("/")
	public String getEmployees() {
		return "hihihi";
	}
	
	@GetMapping("/fetchall")
	public ArrayList<Versions> fetchall() {
		return versionsService.fetchAllVersions();
	}
	@GetMapping("/fetchfilebyname/{name}")
	public ArrayList<Versions> fetchFileByName(@PathVariable("name") String name) {
		return versionsService.fetchByName(name);
	}
	@GetMapping("fetchfilebynameandversion/{version}/{name}")
	public List<Versions> fetchFileByName(@PathVariable("name") String name,@PathVariable("version") String version) {
		return versionsService.fetchByFilenameAndVersions(name,version);
	}

	@PostMapping("/uploadfile")
	@ResponseBody
	public Map<String, String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
		String fileResponse = null;
		File file;
		try {
			file = ResourceUtils.getFile("classpath:");
			System.out.println(file.getAbsolutePath());
			String filepath = file.getAbsolutePath() + "/" + multipartFile.getOriginalFilename() + "v1";
			File newFile = new File(filepath);
			// Creating the directory
			boolean bool = newFile.mkdir();
			if (bool) {
				byte[] bytes = multipartFile.getBytes();
				Path path = Paths.get(filepath + "/" + multipartFile.getOriginalFilename());
				Files.write(path, bytes);
				fileResponse = "File successfully uploaded";
				Map<String, String> daoResponse = new HashMap<>();
				daoResponse.put("filename", multipartFile.getOriginalFilename());
				daoResponse.put("version", "v1");
				versionsService.saveVersion(daoResponse);
			} else {
				fileResponse = "This file already have a version";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> response = new HashMap<String, String>();

		response.put("status", fileResponse);
		response.put("filename", multipartFile.getOriginalFilename());
		return response;

	}

	@PostMapping(path = "/editfile", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> editFile(@RequestBody(required = true) Map<String, String> resp) {
		Map<String, String> response = new HashMap<String, String>();
		StringBuilder newversion = new StringBuilder();
		newversion.append("v");
		int newVersionIndex = Integer.parseInt(resp.get("version").substring(1, resp.get("version").length())) + 1;
		newversion.append(newVersionIndex);

		File file;
		try {
			file = ResourceUtils.getFile("classpath:");
			System.out.println(file.getAbsolutePath());
			String filepath = file.getAbsolutePath() + "/" + resp.get("filename") + newversion.toString();
			File newFile = new File(filepath);
			// Creating the directory
			boolean bool = newFile.mkdir();
			if (bool) {
				byte[] bytes = resp.get("content").getBytes();
				Path path = Paths.get(filepath + "/" + resp.get("filename"));
				Files.write(path, bytes);
				Map<String, String> daoResponse = new HashMap<>();
				daoResponse.put("filename", resp.get("filename"));
				daoResponse.put("version", newversion.toString());
				versionsService.saveVersion(daoResponse);
				response.put("status", "File edited Successfully");
			} else {
				byte[] bytes = resp.get("content").getBytes();
				Path path = Paths.get(file.getAbsolutePath() + "/" + resp.get("filename")+resp.get("version")+"/" + resp.get("filename"));
				Files.write(path, bytes);
				String maxVersion=versionsService.findMaxVersionByName(resp.get("filename"));
				int latestversion=Integer.parseInt(maxVersion.substring(1,maxVersion.length()));
				int currentVersion=Integer.parseInt(resp.get("version").substring(1, resp.get("version").length()));
				System.out.println(latestversion);
				for(int i=currentVersion+1;i<=latestversion;i++) {
					System.out.println(file.getAbsolutePath() + "/" + resp.get("filename")+"v"+i);
					File currentFile = new File(file.getAbsolutePath() + "/" + resp.get("filename")+"v"+i);
					boolean status=VersionsService.deleteDirectory(currentFile);
				    System.out.println(status);
				    versionsService.deleteByVersionAndFilename("v"+i, resp.get("filename"));
				    System.out.println("############################");
				}
				
				response.put("status", "File version does not exist");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("status", e.getMessage());
		}
		return response;
	}

	@GetMapping(path = "/readfile/{version}/{filename}", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> readFile(@PathVariable("filename") String filename,
			@PathVariable("version") String version) {
		System.out.println("#################################");
		File file;
		StringBuilder content = new StringBuilder();
		try {
			file = ResourceUtils.getFile("classpath:");
			System.out.println(file.getAbsolutePath());
			String filepath = file.getAbsolutePath() + "/" + filename + version + "/" + filename;

			try (Stream<String> stream = Files.lines(Paths.get(filepath), StandardCharsets.UTF_8)) {
				stream.forEach(s -> content.append(s).append("\n"));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> response = new HashMap<String, String>();
		response.put("filename", filename);
		response.put("content", content.toString());

		return response;
	}

	@GetMapping(path = "/switchversion/{version}", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> readFile(@PathVariable("version") String version) {

		Map<String, String> response = new HashMap<String, String>();
		response.put("filename", "switched version");
		return response;
	}

}
