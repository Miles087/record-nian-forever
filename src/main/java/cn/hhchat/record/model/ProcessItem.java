package cn.hhchat.record.model;

import cn.hhchat.record.Config;
import cn.hhchat.record.util.FileUtil;
import cn.hhchat.record.util.ImgUtil;
import cn.hhchat.record.util.MarkdownUtil;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created this one by huminghao on 2018/3/18.
 */
@Data
@Slf4j
public class ProcessItem {

    String id;
    List<String> text;
    List<String> imageList;
    List<String> imageLocalList = new ArrayList<>();
    List<Comment> comments;
    List<Cool> coolList;
    String coolCnt;
    String createTime;

    public Boolean saveImagesToLocal(String title) {
        if (imageList.size() == 0) {
            return true;
        }
        for (int i = 0; i < imageList.size(); i++) {
            log.info(" => 尝试下载进展 {} 的图片 {}/{}", this.id, i + 1, imageList.size());
            String pic = imageList.get(i);
            String localFileName = FileUtil.generateProcessImgPath(title, this.id, ImgUtil.getImageFileName(pic));
            int count = 10;
            while (!ImgUtil.FetchImage(pic + "!large", localFileName) && count-- > 0) {
                log.warn(" => 下载进展的图片失败，重试中");
            }
            if (count == -1) {
                continue;
            }
            imageLocalList.add(localFileName);
        }
        return true;
    }

    public String toMD() {

        List<String> imgList = this.imageList;
        if (imgList == null) {
            imgList = new ArrayList<>();
        }
        if (Config.DOWNLOAD_IMAGES || CollectionUtil.isNotEmpty(this.imageLocalList)) {
            imgList = this.imageLocalList;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(MarkdownUtil.oneLine("进展 " + id))
                .append(MarkdownUtil.emptyLine());

        for (int i = 0; i < imgList.size(); i++) {
            sb.append(MarkdownUtil.image(String.valueOf(i), imgList.get(i), true));
        }

        for (String textLine : this.text) {
            if (textLine.length() == 0) {
                sb.append(MarkdownUtil.emptyLine());
            } else {
                sb.append(MarkdownUtil.oneLine(textLine));
            }
        }
        sb.append(MarkdownUtil.emptyLine())
                .append(MarkdownUtil.emptyLine())
                .append(MarkdownUtil.quote(this.createTime + " " + this.coolCnt));
        return sb.toString();
    }
}
