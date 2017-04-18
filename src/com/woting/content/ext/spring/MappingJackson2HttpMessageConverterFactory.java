package com.woting.content.ext.spring;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MappingJackson2HttpMessageConverterFactory extends MappingJackson2HttpMessageConverter {

	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        //加密
        System.out.println("些数据");
        String result = json.replace("##img##", "http://ac.wotingfm.com/contentimg");
        //输出
        outputMessage.getBody().write(result.getBytes());
	}
}
