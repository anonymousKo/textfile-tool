package com.ke.textfiletool.util;

import cn.hutool.core.io.FileUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FileOperationUtil {
    public List<File> getFiles(String path, FileFilter fileFilter) {
        if (fileCheck(path)) {
            return new ArrayList<>(FileUtil.loopFiles(path, fileFilter));
        } else {
            return null;
        }
    }

    private boolean fileCheck(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return Objects.requireNonNull(file.listFiles()).length > 0;
        }
        return false;
    }

    public void replaceByLine(File file, String oldstr, String newstr) {
        List<String> list;
        boolean isChange = false;
        try {
            list = FileUtils.readLines(file, "UTF-8");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(oldstr)) {
                    isChange = true;
                    String temp = list.get(i).replaceAll(oldstr, newstr);
                    log.info("File: {}, line: {}, before replace：{}，after replace：{}", file, i + 1, list.get(i), temp);
                    list.remove(i);
                    list.add(i, temp);
                }
            }
            if (isChange) {
                backup(file);
            }
            FileUtils.writeLines(file, "UTF-8", list, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backup(File srcFile) throws IOException {
        String srcPath = srcFile.getPath();
        String destName, destPath;
        String fileName = srcFile.getName();
        int extension = fileName.lastIndexOf(".");
        if (extension != -1) {
            destName = "bak\\" + fileName.substring(0, extension) + "_bak" + fileName.substring(extension);
        } else {
            destName = "bak\\" + srcPath + "_bak";
        }
        destPath = srcPath.replace(fileName, destName);
        FileUtils.copyFile(srcFile, new File(destPath));
        log.info("Change the file: {}, create the bakFile : {}", srcPath, destPath);
    }

    public void mdToHtml(File file) throws IOException {
        String md = FileUtils.readFileToString(file, "utf-8");
        File htmlFile = new File(parseParentPath(file) +
                "0html\\" + file.getName().replace(".md", ".html"));
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(md);
        String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        FileUtils.writeStringToFile(htmlFile, htmlText, "utf-8");
        log.info("convert file {} success", htmlFile);
    }

    public String parseParentPath(File file){
        String srcPath = file.getPath();
        return srcPath.substring(0, srcPath.lastIndexOf("\\")) + "\\";
    }


    public void extractHighlight(File file)  {
        String documentName;
        StringBuilder wholeText = new StringBuilder("+++\n");
        Map<Integer,String > indexToBookmarkMap = new LinkedHashMap<>();
        boolean hasAnnotation = false;
        try {
            PDDocument pddDocument = PDDocument.load(file);
            documentName = file.getName();
            if(pddDocument.isEncrypted()){
                pddDocument.close();
                throw new SecurityException("the file " + documentName + " is encrypted");
            }
            mapBookmarks(pddDocument,indexToBookmarkMap);
            for (int i = 0; i < pddDocument.getNumberOfPages(); i++) {
                int pageNum = i + 1;
                PDPage page = pddDocument.getPage(i);
                List<PDAnnotation> pdAnnotationList = page.getAnnotations();
                if (pdAnnotationList.isEmpty()) {
                    continue;
                }
                for (PDAnnotation pdAnnotation : pdAnnotationList) {
                    String bookmarkName = "";
                    if (pdAnnotation instanceof PDAnnotationTextMarkup && ((
                            pdAnnotation.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT))
                            || (pdAnnotation.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE)))) {
                        hasAnnotation = true;
                        if (! indexToBookmarkMap.isEmpty()){
                            for(Map.Entry<Integer,String> entry:indexToBookmarkMap.entrySet()){
                                if (pageNum > entry.getKey()){
                                    bookmarkName = entry.getValue();
                                }else {
                                    break;
                                }
                            }
                        }
                        String highAndComment = renderHighlight(pdAnnotation,page);
                        String formattedDate = "";
                        if(pdAnnotation.getModifiedDate() != null){
                            formattedDate=formatDate(pdAnnotation.getModifiedDate().substring(2,15));
                        }

                        String exportIndex = "<div style=\"float:left\">" + pageNum +" " + bookmarkName + "</div>" +
                                "<div style=\"float:right\">"+ formattedDate + "</div>";
                        wholeText.append(exportIndex).append("\n").append("> ").append(highAndComment).append("+++").append("\n");
                    }
                }
            }
            File desFile = new File(parseParentPath(file) + file.getName().replace(".pdf","_note.md"));
            if (hasAnnotation){
                FileUtils.writeStringToFile(desFile, wholeText.toString(),"utf-8");
                log.info("extract highlight success, write file to->{}",desFile);
            }else {
                log.info("no highlight find in file -> {}",documentName);
            }
            pddDocument.close();
        }catch (SecurityException e){
            log.info(String.valueOf(e));
        }
        catch (Exception e) {
            log.info("extract highlight error -> {}", e);
        }
    }

    private String renderHighlight(PDAnnotation pdAnnotation,PDPage page) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDRectangle rect = pdAnnotation.getRectangle();
        float x = rect.getLowerLeftX() - 1;
        float y = rect.getUpperRightY() - 1;
        float width = rect.getWidth() + 2;
        float height = rect.getHeight() + rect.getHeight() / 4;
        int rotation = page.getRotation();
        if (rotation == 0) {
            PDRectangle pageSize = page.getMediaBox();
            y = pageSize.getHeight() - y;
        }
        Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
        stripper.addRegion(Integer.toString(1), awtRect);
        stripper.extractRegions(page);
        Optional<String> comment = Optional.ofNullable(pdAnnotation.getContents());
        return stripper.getTextForRegion(String.valueOf(1)) +"\n"+ comment.orElse("")
                + (comment.orElse("").isEmpty() ?"":"\n");
    }
    private void mapBookmarks(PDDocument pddDocument,Map<Integer,String> indexToBookmarkMap){
        try{

        PDOutlineItem currentBookmark = pddDocument.getDocumentCatalog().getDocumentOutline().getFirstChild();
        while (currentBookmark != null) {
            PDPage currentPage = currentBookmark.findDestinationPage(pddDocument);
            Integer currentPageIndex = pddDocument.getPages().indexOf(currentPage);
            indexToBookmarkMap.put(currentPageIndex, currentBookmark.getTitle());
            currentBookmark = currentBookmark.getNextSibling();
        }
        }catch (NullPointerException e){
            log.error("cant resolve the catalog of pdf ->{}",pddDocument.getDocumentInformation().getTitle());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public String formatDate(String originDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(originDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
}
