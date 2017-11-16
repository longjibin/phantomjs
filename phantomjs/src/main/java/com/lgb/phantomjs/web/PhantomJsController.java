package com.lgb.phantomjs.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lgb.webspider.script.Script;


@Controller
public class PhantomJsController {

	private static final Logger LOGGER = Logger.getLogger(PhantomJsController.class);

	static {
		System.getProperties().setProperty("phantomjs.binary.path", "D:/phantomjs/phantomjs.exe");
	}

	@Autowired
	private WebDriver webDriver;

	@RequestMapping("down")
	public void down(@RequestHeader(value = "url") String url, @RequestHeader(value = "scriptName") String scriptName,
			HttpServletResponse response) throws IOException {
		url=URLDecoder.decode(url, "utf-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter printWriter = response.getWriter();
		
		LOGGER.info("downloading page " + url);
		webDriver.get(url);

		if(StringUtils.isNoneBlank(scriptName)){
			// 脚本执行
			try {
				Script script = (Script) Class.forName(scriptName).newInstance();
				script.script(webDriver);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.info(scriptName + ":脚本执行错误");
				return;
			}
		}

		// 脚本执行

		WebElement webElement = webDriver.findElement(By.xpath("/html"));
		String content = webElement.getAttribute("outerHTML");
		printWriter.write(content);
	}
}
