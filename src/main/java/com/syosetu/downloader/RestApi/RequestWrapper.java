package com.syosetu.downloader.RestApi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestWrapper 
{
	private int currentProxyCnt = 0;
	
    public RestTemplate getCustomRestTemplate()
    {
    	//Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("160.202.146.50", 80));
    	
    	List<HttpHost> proxyList = new ArrayList<HttpHost>();
    	proxyList.add(new HttpHost("52.140.242.103", 3128));
    	proxyList.add(new HttpHost("51.158.165.18", 8811));
    	proxyList.add(new HttpHost("163.172.190.160", 8811));
    	
    	//HttpHost proxy = new HttpHost("52.140.242.103", 3128);
    	//HttpHost proxy = new HttpHost("51.158.165.18", 8811);
    	//HttpHost proxy = new HttpHost("163.172.190.160", 8811);
    	
    	currentProxyCnt++;
    	
        
    	HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(30000);
        factory.setConnectTimeout(30000);
        HttpClient httpClient;
        
        if(currentProxyCnt > proxyList.size()) 
    	{
    		currentProxyCnt = 0;
    		httpClient = HttpClientBuilder.create()
	            .setMaxConnTotal(100)
	            .setMaxConnPerRoute(25)
	            .setProxy(proxyList.get(currentProxyCnt))
	            .build();
    	}
        else if(currentProxyCnt == proxyList.size())
        {
        	httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(25)
                .build();
        }
        else
        {
        	httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(25)
                .setProxy(proxyList.get(currentProxyCnt))
                .build();
        }
        
        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
        
        //160.202.146.50 80
        //43.245.216.7 8080
        
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "_ga=GA1.2.128193112.1545719096; ks2=kbe4ex5xrkxn; lineheight=0; fontsize=0; novellayout=0; fix_menu_bar=1; _gid=GA1.2.1465465610.1546077386; _td=d8703db8-91ab-4ff5-bb4d-b09446d8f137;sasieno=0; over18=yes; nlist3=qo9e.c-pszu.1q-5d3j.1");
        headers.add("Content-Type", "text/html; charset=UTF-8");

        // 요청 Request에 Header 설정(intercept 방식)
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor()
        {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException 
            {
                request.getHeaders().set("Cookie", "_ga=GA1.2.128193112.1545719096; ks2=kbe4ex5xrkxn; lineheight=0; fontsize=0; novellayout=0; fix_menu_bar=1; _gid=GA1.2.1465465610.1546077386; _td=d8703db8-91ab-4ff5-bb4d-b09446d8f137;sasieno=0; over18=yes; nlist3=qo9e.c-pszu.1q-5d3j.1");
                return execution.execute(request, body);
            }
        });

        // 응답 본문 인코딩 설정
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(httpMessageConverter -> httpMessageConverter instanceof StringHttpMessageConverter);
        messageConverters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }
}