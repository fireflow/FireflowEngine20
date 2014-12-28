/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.clientwidget.servlet.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.clientwidget.servlet.ActionHandler;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class LoadImageHandler implements ActionHandler {
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String imagePath = req.getParameter("path");

		if (imagePath == null || imagePath.trim().equals("")) {
			return;
		}
		String lowerCasePath = imagePath.toLowerCase();
		String contentType = "image/png";
		if (lowerCasePath.endsWith(".png")) {
			contentType = "image/png";
		} else if (lowerCasePath.endsWith("gif")) {
			contentType = "image/gif";
		} else if (lowerCasePath.endsWith("bmp")) {
			contentType = "image/bmp";
		} else if (lowerCasePath.endsWith("jpeg")
				|| lowerCasePath.endsWith("jpg")) {
			contentType = "image/jpeg";
		}

		// 把图片写给浏览器
		// response.setHeader("content-type","image/jpeg");
		resp.setContentType(contentType);

		// 告诉浏览器不要缓存
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setIntHeader("Expires", -1);

		InputStream inStream = LoadImageHandler.class
				.getResourceAsStream(imagePath);
		if (inStream==null){
			return;//没有图片，则直接返回
		}
		OutputStream outStream = resp.getOutputStream();

		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			outStream.write(buff, 0, rc);
		}
		inStream.close();

	}
}
