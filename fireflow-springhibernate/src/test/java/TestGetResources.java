import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TestGetResources {
	public static void main(String[] args){
//		try {
//			Enumeration<URL> urls = TestGetResources.class.getClassLoader().getResources("org/fireflow/model/resourcedef/Resource.class");
//			while (urls.hasMoreElements()){
//				URL url = urls.nextElement();
//				
//				System.out.println(url.toString());
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//		
//		
		System.out.println(System.getProperty("java.class.path"));
		
		System.out.println("=================================");
//		
//		try {
//			Resource[] resources = resolver.getResources("classpath*:**/overview.html");
//			
//			if (resources!=null){
//				for (Resource rsc : resources){
//					System.out.println("rsc.getURL().getRef()="+rsc.getURL().getRef());
//					System.out.println("rsc.getURL().getPath()="+rsc.getURL().getPath());
//					System.out.println("rsc.getURL().getFile()="+rsc.getURL().getFile());
//					System.out.println("rsc.getURL().getHost()="+rsc.getURL().getHost());
//					System.out.println("rsc.getURL().getProtocol()="+rsc.getURL().getProtocol());
//					System.out.println("rsc.getURL().getQuery()="+rsc.getURL().getQuery());
//					System.out.println("rsc.getFile().getName()="+rsc.getFile().getName());
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {

			Enumeration<URL> urls = TestGetResources.class.getClassLoader().getResources("/");
			while (urls.hasMoreElements()){
				URL url = urls.nextElement();
				System.out.println("url.getPath()+"+url.getPath());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
