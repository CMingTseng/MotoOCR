package me.image.service;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;

import me.image.assembler.AndroidFormAssembler;
import me.image.domain.EPAAirBean;
import me.image.util.ImageMEConstants;

/**
 * APP與雲端主機連線
 */
public class ImageService {
    private static final String LOGGER = ImageService.class.getName();

    static final String LOGON_SITE = "camerame.zapto.org"; //android 預設10.0.2.2 192.168.1.109
    static final int LOGON_PORT = 8080;
    static final String IMAGE_UPLOAD_SERVICE = "cameraME/service/rest/imageService";
    static final int CONNECTION_TIMEOUT = 50000;//六秒
    static final int SOCKET_TIMEOUT = 50000;

    /**
     * 呼叫雲端Webservice
     *
     * @data 圖片
     * @userId 機器識別碼
     */
    public EPAAirBean doEPAService(byte[] data, String userId) throws Exception {
        Log.i(LOGGER, " Enter Method doEPAService aa :");
        BasicHttpParams httpParams = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);

        HttpClient client = new DefaultHttpClient(httpParams);

        //client.getHostConfiguration().setHost(LOGON_SITE, LOGON_PORT);

//		  File file=new File("E:/ocrImage/digits.jpg");
//		  byte[] imageBytes=null;
//		  if(file!=null){
//		   	 FileInputStream fis=new FileInputStream (file);
//		     if(fis!=null){
//		    	  int len=fis.available();
//		    	  imageBytes=new byte[len];
//		    	  fis.read(imageBytes);
//		     }
//		   }	  
        //String url="http://"+LOGON_SITE+":"+LOGON_PORT+"/"+IMAGE_UPLOAD_SERVICE+"/imageUpload";
        String url = "http://" + LOGON_SITE + ":" + LOGON_PORT + "/" + IMAGE_UPLOAD_SERVICE + "/imageUpload";
        Log.i(LOGGER, "url [" + url + "]");
        // 模擬登錄頁面
        //PostMethod post = new PostMethod(IMAGE_UPLOAD_SERVICE);
        HttpPost post = new HttpPost(url);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ByteArrayBody imgBody = new ByteArrayBody(data, "image/jpeg", "image");
        entity.addPart("userId", new StringBody(userId, "text/plain", Charset.forName("UTF-8")));
        //entity.addPart("type",new StringBody( "EPA_AIR", "text/plain", Charset.forName( "UTF-8" )));
        entity.addPart("image", imgBody);
        entity.addPart("type", new StringBody(ImageMEConstants.QUERY_TYPE_EPA_AIR, "text/plain", Charset.forName("UTF-8")));
        //entity.addPart("carNo",new StringBody( "CRN-588", "text/plain", Charset.forName( "UTF-8" )));
        // For File parameters
        // entity.addPart( paramName, new FileBody((( File ) paramValue ), "application/zip" ));

        // For usual String parameters
        // entity.addPart( paramName, new StringBody( paramValue.toString(), "text/plain",
        Charset.forName("UTF-8");

        post.setEntity(entity);
        try {
            Log.i(LOGGER, "----execute start----");
            HttpResponse response = client.execute(post);
            Log.i(LOGGER, " response status [" + response + "]");
            // Here we go!
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            AndroidFormAssembler assebler = new AndroidFormAssembler();
            EPAAirBean epaAirBean = assebler.assemblerXMLToEPAAirBean(result);
            Log.i(LOGGER, "----reponse start----");
            Log.i(LOGGER, result);
            Log.i(LOGGER, "----reponse start----");
            return epaAirBean;

        } catch (Exception e) {
            Log.i(LOGGER, "error connection");
            e.printStackTrace();

        } finally {

            client.getConnectionManager().shutdown();
        }
        return null;

    }
}
