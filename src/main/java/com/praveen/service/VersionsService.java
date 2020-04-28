package com.praveen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.praveen.dao.VersionsRepository;
import com.praveen.model.Versions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VersionsService {

	@Autowired
	private VersionsRepository repository;

	public Map<String, String> saveVersion(Map<String, String> request) {
		Versions version = new Versions();
		version.setFilename(request.get("filename"));
		version.setVersion(request.get("version"));
		version.setEnabled("1");
		System.out.println(version.getFilename() + "###################");
		Versions resp = repository.save(version);
		Map<String, String> response = new HashMap<String, String>();
		response.put("id", String.valueOf(resp.getId()));
		response.put("status", "success");
		return response;
	}

	public String findMaxVersionByName(String filename) {
		return repository.findMaxVersion(filename);
	}

	public void deleteByVersionAndFilename(String version, String filename) {
		repository.deleteByVersionAndFilename(filename, version);
	}

	public ArrayList<Versions> fetchAllVersions() {
		return (ArrayList<Versions>) repository.findAll();
	}

	public ArrayList<Versions> fetchByName(String name) {

		return (ArrayList<Versions>) repository.findAllByName(name);
	}

	public List<Versions> fetchByFilenameAndVersions(String filename, String version) {
		return repository.findByFilenameAndVersion(filename, version);
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}
		System.out.println("removing file or directory : " + dir.getName());
		return dir.delete();
	}
}
