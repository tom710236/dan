package com.example.motoapp;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class clsHttpPost {
	public static String Invoke(String pStrServerURL, String pStrPostData)
			throws Exception {

		Log.e("HttpPost,pStrServerURL", pStrServerURL);
		Log.e("HttpPost,pStrPostData", pStrPostData);
		String strResult = "";
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 15000);

		HttpClient client = new DefaultHttpClient(params);
		HttpPost post = new HttpPost(pStrServerURL);

		List<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>(1);

		namevaluepairs.add(new BasicNameValuePair("POST31DATA", pStrPostData));
		post.setEntity(new UrlEncodedFormEntity(namevaluepairs, HTTP.UTF_8));// 讀中文
		HttpResponse response = client.execute(post);

		// 接收傳回來的值
		strResult = EntityUtils.toString(response.getEntity());
		Log.e("strResult",strResult);
		return strResult;
	}

	/**
	 * Post資料和上傳檔案
	 *
	 * @param pStrServerURL
	 *            主機連線URL
	 * @param pStrPostData
	 *            Post資料
	 * @param pStrFile
	 *            Post檔案
	 * @return
	 * @throws Exception
	 */
	public static String PostDataAndFile(String pStrServerURL,
										 String[] pStrPostData, String pStrFile) throws Exception {

		String serverResponseMessage = "";
		int serverResponseCode = 0;
		String serverResult = "";
		String fileName = URLDecoder.decode(pStrFile, "UTF-8");  //解決中文編碼
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(fileName);

		try {

			// open a URL connection to the Servlet
			// FileInputStream fileInputStream = new
			// FileInputStream(sourceFile);
			URL url = new URL(pStrServerURL);

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// conn.setRequestProperty("ENCTYPE", "multipart/form-data");

			dos = new DataOutputStream(conn.getOutputStream());

			// Post資料


			for (int i = 0; i < pStrPostData.length; i++) {
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				String[] ay = pStrPostData[i].split("@");
				String body = String.format(
						"Content-Disposition: form-data;name=\"%s\"\r\n\r\n",
						ay[0]);
				dos.writeBytes(body);			// Post參數名稱
				dos.write(ay[1].getBytes());	// Post參數值

				dos.writeBytes(lineEnd);
			}


			String fmtHeader = "Content-Disposition: form-data; name=\"Data\"; filename=\"%s\"\r\n Content-Type: application/octet-stream\r\n\r\n";

			Uri u = Uri.parse(fileName);
			File f = new File("" + u);
			f.getName();

			String[] strSrcFile = new String[] {fileName };
			String[] strFile = new String[] { f.getName() };

			// 上傳檔案
			for (int idx = 0; idx < strSrcFile.length; idx++) {
				FileInputStream fileInputStream = new FileInputStream(
						strSrcFile[idx]);

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.write(String.format(fmtHeader,  strFile[idx]).getBytes()); //中文編碼解決
				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				// dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				fileInputStream.close();
			}
			// end symbol for multi-file upload
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			dos.flush();
			dos.close();

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			serverResponseMessage = conn.getResponseMessage();
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataInputStream dis = new DataInputStream(conn.getInputStream());
				bytesAvailable = dis.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				bytesRead = dis.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					baos.write(buffer, 0, bytesRead);
					bytesAvailable = dis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = dis.read(buffer, 0, bufferSize);
				}
				serverResult = new String(baos.toByteArray());
			}

			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
					+ ": " + serverResponseCode);

			if (serverResponseCode == 200) {
			}

		} catch (MalformedURLException ex) {

			ex.printStackTrace();

			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {

			e.printStackTrace();

		}
		return serverResult;
	}

	/**
	 * Post資料和上傳檔案
	 *
	 * @param pStrServerURL
	 *            主機連線URL
	 * @param pStrPostData
	 *            Post資料
	 * @param pStrFile
	 *            Post檔案
	 * @return
	 * @throws Exception
	 */
	public static String PostDataAndFile(String pStrServerURL,
										 String pStrPostData, String pStrFile) throws Exception {

		String serverResponseMessage = "";
		int serverResponseCode = 0;
		String serverResult = "";
		String fileName = URLDecoder.decode(pStrFile, "UTF-8");  //解決中文編碼
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(fileName);

		try {

			// open a URL connection to the Servlet
			//FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(pStrServerURL);

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// conn.setRequestProperty("ENCTYPE", "multipart/form-data");

			dos = new DataOutputStream(conn.getOutputStream());

			// Post資料
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			String body = String.format(
					"Content-Disposition: form-data;name=\"%s\"\r\n\r\n",
					"PostData");
			dos.writeBytes(body);			// Post參數名稱
			dos.write(pStrPostData.getBytes());	// Post參數值

			dos.writeBytes(lineEnd);
			String fmtHeader = "Content-Disposition: form-data; name=\"File%d\"; filename=\"%s\"\r\n Content-Type: application/octet-stream\r\n\r\n";

			Uri u = Uri.parse(fileName);
			File f = new File("" + u);
			f.getName();

			String[] strSrcFile = new String[] {fileName };
			String[] strFile = new String[] { f.getName() };

			// 上傳檔案
			for (int idx = 0; idx < strSrcFile.length; idx++) {
				FileInputStream fileInputStream = new FileInputStream(
						strSrcFile[idx]);

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.write(String.format(fmtHeader, idx + 1, strFile[idx]).getBytes()); //中文編碼解決
				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				// dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				fileInputStream.close();
			}
			// end symbol for multi-file upload
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			dos.flush();
			dos.close();

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			serverResponseMessage = conn.getResponseMessage();
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataInputStream dis = new DataInputStream(conn.getInputStream());
				bytesAvailable = dis.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				bytesRead = dis.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					baos.write(buffer, 0, bytesRead);
					bytesAvailable = dis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = dis.read(buffer, 0, bufferSize);
				}
				serverResult = new String(baos.toByteArray());
			}

			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
					+ ": " + serverResponseCode);

			if (serverResponseCode == 200) {
			}

		} catch (MalformedURLException ex) {

			ex.printStackTrace();

			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {

			e.printStackTrace();

		}
		return serverResult;
	}


}
