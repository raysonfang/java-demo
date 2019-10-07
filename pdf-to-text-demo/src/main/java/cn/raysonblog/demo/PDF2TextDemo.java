package cn.raysonblog.demo;

import com.baidu.aip.ocr.AipOcr;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * PDF�е�ͼƬתText�ı������Ի�ȡͼƬ�е�����
 * @author raysonfang
 * @���ں� Java�����ɻ���ID:raysonfang��
 */
public class PDF2TextDemo {
    //����APPID/AK/SK
    public static final String APP_ID = "16844926";
    public static final String API_KEY = "BYs0TGxD6oWPWZhuU6Gg8aoZ";
    public static final String SECRET_KEY = "frGCGi0Bc09QNqgQGCO8ThZyawfkgDpO";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * ����pdf�ĵ���Ϣ
     *
     * @param pdfPath pdf�ĵ�·��
     * @throws Exception
     */
    public static void pdfParse(String pdfPath) throws Exception {
        InputStream input = null;
        File pdfFile = new File(pdfPath);
        PDDocument document = null;
        try {
            input = new FileInputStream(pdfFile);
            //���� pdf �ĵ�
            document = PDDocument.load(input);

            /** �ĵ�������Ϣ **/
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("����:" + info.getTitle());
            System.out.println("����:" + info.getSubject());
            System.out.println("����:" + info.getAuthor());
            System.out.println("�ؼ���:" + info.getKeywords());

            System.out.println("Ӧ�ó���:" + info.getCreator());
            System.out.println("pdf ��������:" + info.getProducer());

            System.out.println("����:" + info.getTrapped());

            System.out.println("����ʱ��:" + dateFormat(info.getCreationDate()));
            System.out.println("�޸�ʱ��:" + dateFormat(info.getModificationDate()));


            //��ȡ������Ϣ
            PDFTextStripper pts = new PDFTextStripper();
            String content = pts.getText(document);
            System.out.println("����:" + content);


            /** �ĵ�ҳ����Ϣ **/
            PDDocumentCatalog cata = document.getDocumentCatalog();
            PDPageTree pages = cata.getPages();
            System.out.println(pages.getCount());
            int count = 1;

            // ��ʼ��һ��AipOcr
            AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

            // ��ѡ�������������Ӳ���
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);

            for (int i = 0; i < pages.getCount(); i++) {
                PDPage page = (PDPage) pages.get(i);
                if (null != page) {
                    PDResources res = page.getResources();
                    Iterable xobjects = res.getXObjectNames();
                    if(xobjects != null){
                        Iterator imageIter = xobjects.iterator();
                        while(imageIter.hasNext()){
                            COSName key = (COSName) imageIter.next();
                            if (res.isImageXObject(key)) {
                                try {
                                    PDImageXObject image = (PDImageXObject) res.getXObject(key);
                                    BufferedImage bimage = image.getImage();
                                    // ��BufferImageת�����ֽ�����
                                    ByteArrayOutputStream out =new ByteArrayOutputStream();
                                    ImageIO.write(bimage,"png",out);//png ΪҪ�����ͼƬ��ʽ
                                    byte[] barray = out.toByteArray();
                                    out.close();
                                    // ����ͼƬʶ������
                                    JSONObject json = client.basicGeneral(barray, new HashMap<String, String>());
                                    System.out.println(json.toString(2));
                                    count++;
                                    System.out.println(count);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != input)
                input.close();
            if (null != document)
                document.close();
        }
    }

    /**
     * ��ȡ��ʽ�����ʱ����Ϣ
     *
     * @param dar ʱ����Ϣ
     * @return
     * @throws Exception
     */
    public static String dateFormat(Calendar calendar) throws Exception {
        if (null == calendar)
            return null;
        String date = null;
        try {
            String pattern = DATE_FORMAT;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            date = format.format(calendar.getTime());
        } catch (Exception e) {
            throw e;
        }
        return date == null ? "" : date;
    }

    public static void main(String[] args) throws Exception {

        // ��ȡpdf�ļ�
        String path = "D:\\googleDownload\\���¶���Spring Cloudʵս .pdf";
        pdfParse(path);

    }

}
