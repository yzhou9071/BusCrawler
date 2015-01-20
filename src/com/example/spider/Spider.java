package com.example.spider;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.buscrawler.MainActivity;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;

public class Spider implements Runnable {

	ArrayList<String> errorList = new ArrayList<String>();// error info
	ArrayList<String> result = new ArrayList<String>(); // search result 
	String startUrl;
	int maxUrl;
	String searchString;
	String city;
	boolean caseSensitive = false;// whether ignore case
	boolean limitHost = false;// whether limit host
	String[] matUrl = {"_1","_2","_3","_4","_5","_6","_7","_8","_9",
						"_A","_B","_C","_D","_E","_F","_G","_H","_I","_J","_K","_L","_M","_N"
						,"_O","_P","_Q","_R","_S","_T","_U","_V","_W","_X","_Y","_Z"};
	public static ArrayList<String> pos = new ArrayList<String>();
	public static ArrayList<String> routes = new ArrayList<String>();
	public static ArrayList<Integer> index = new ArrayList<Integer>();
	
	public Spider(String startUrl, int maxUrl, String searchString,String city) {
		this.startUrl = startUrl;
		this.maxUrl = maxUrl;
		this.searchString = searchString;
		this.city = city;
	}

	public ArrayList<String> getResult() {
		return result;
	}

	public void run() {
		crawl(startUrl, maxUrl, searchString, limitHost, caseSensitive,city);
	}
	
	private URL verifyUrl(String url) {
		if (!url.toLowerCase().startsWith("http://"))
			return null;
		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(url);
		} 
		catch (Exception e) {
			return null;
		}
		return verifiedUrl;
	}

	private String downloadPage(URL pageUrl) {
		try {
			HttpURLConnection conn = (HttpURLConnection) pageUrl.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.connect();
			InputStream stream = conn.getInputStream();
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = stream.read(buffer)) != -1) {
				data.write(buffer, 0, len);
			}
			stream.close();
			String str = new String(data.toByteArray(), "utf-8");
			return str;
		} catch (Exception ee) {
			System.out.print("ee:" + ee.getMessage());
		}
		return null;
	}

	private String removeWwwFromUrl(String url) {
		int index = url.indexOf("://www.");
		if (index != -1) {
			return url.substring(0, index + 3) + url.substring(index + 7);
		}
		return (url);
	}

	private ArrayList<String> searchStringMatches(String pageContents,String matchString,String startUrl) {
		ArrayList<String> linklist = new ArrayList<String>();
		Pattern pat = Pattern.compile(matchString);
		Matcher mat = pat.matcher(pageContents);
		boolean ret = mat.find();
		while (ret){
			for (int i=1;i<=mat.groupCount();i++) {
				linklist.add(startUrl+"/"+mat.group(i));
				linklist.add(startUrl+"/"+mat.group(i)+"?back=true");
			}
			ret = mat.find();
		}
		return linklist;
	}

	private String getPageContents(String url,boolean caseSensitive){
		URL verifiedUrl = verifyUrl(url);
		String pageContents = downloadPage(verifiedUrl);
		
		return pageContents;
	}

	public ArrayList<String> getRoutes(){return routes;}
	public ArrayList<Integer> getIndex(){return index;}
	public ArrayList<String> getPos(){return pos;}
	
	private ArrayList<String> getSpecificUrl(String startUrl,boolean caseSensitive,String city) {
		ArrayList<String> finalurls = new ArrayList<String>();
		String matchstr = "<a href=\"/"+city+"/xianlu/([\\s\\S]*?)\">";
		for(int i=0;i<matUrl.length;i++){
			String pageContents = getPageContents(startUrl+matUrl[i],caseSensitive);
			ArrayList<String> tmp = searchStringMatches(pageContents,matchstr,startUrl);
			finalurls.addAll(tmp);
		}
		return finalurls;
	}

	public ArrayList<String> crawl(String startUrl, int maxUrls,
			String searchString, boolean limithost, boolean caseSensitive,String city){
		LinkedHashSet<String> toCrawlList = new LinkedHashSet<String>();
		if (maxUrls < 1) {
			errorList.add("Invalid Max URLs value.");
			System.out.println("Invalid Max URLs value.");
		}

		if (searchString.length() < 1) {
			errorList.add("Missing Search String.");
			System.out.println("Missing search String");
		}

		if (errorList.size() > 0) {
			System.out.println("err!!!");
			return errorList;
		}
		startUrl = removeWwwFromUrl(startUrl);

		toCrawlList.addAll(getSpecificUrl(startUrl,caseSensitive,city));
		
		int count = 0;
		while (toCrawlList.size() > 0) {			
			String specificUrl = removeWwwFromUrl(toCrawlList.iterator().next());
			toCrawlList.remove(specificUrl);
			//System.out.println("***********"+specificUrl+"&&&&"+toCrawlList.size());
			String pageContents = getPageContents(specificUrl,caseSensitive);
			if(pageContents == null){continue;}
			
			Pattern pat = Pattern.compile("<span>([\\s\\S]*?)</span>");
			Matcher mat = pat.matcher(pageContents);
			boolean ret = mat.find();
			if(ret == false){continue;}
			String route_tmp = mat.group(1);
			
			pat = Pattern.compile("<i>([\\s\\S]*?)</i>");
			mat = pat.matcher(pageContents);
			ret=mat.find();
			int i = 1;
			//System.out.println("**Routes:"+route_tmp);
			while(ret){
				index.add(i);
				routes.add(route_tmp);
				pos.add(mat.group(1));
				//System.out.println("**Count:"+count+"**Index:"+index.get(count)+"**Routes:"+routes.get(count)+"**Pos:"+pos.get(count));
				i++;count++;
				ret=mat.find();
			}
		}
		return result;
	}

	public void buscrawler(String city){
		Spider crawler = new Spider("http://mbus.mapbar.com/"+city+"/xianlu",20,"公交线路",city);
		Thread search = new Thread(crawler);
		search.start();
		try {
			search.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {}
}
