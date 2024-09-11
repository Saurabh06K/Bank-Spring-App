package com.aurionpro.bank.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
	private final Cloudinary cloudinary;

	public CloudinaryService() {
		cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dpwhmmpcu", "api_key", "768334557329669",
				"api_secret", "eY0rlI0BDUlgYU60atoZaCpdXv0"));
	}

	public String uploadFile(MultipartFile file) throws IOException {
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
		return uploadResult.get("url").toString();
	}
}
