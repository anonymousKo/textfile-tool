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
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        String srcPath = file.getPath();
        File htmlFile = new File(srcPath.substring(0, srcPath.lastIndexOf("\\")) +
                "\\0html\\" + file.getName().replace(".md", ".html"));
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(md);
        String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        FileUtils.writeStringToFile(htmlFile, htmlText, "utf-8");
        log.info("convert file {} success", htmlFile);
    }

    public void extractHighlight(File file)  {
        String documentName;
        try {
            PDDocument pddDocument = PDDocument.load(file);
            documentName = file.getName();
            if(pddDocument.isEncrypted()){
                pddDocument.close();
                throw new Exception("the file " + documentName + " is encrypted");
            }
            for (int i = 0; i < pddDocument.getNumberOfPages(); i++) {
//               PDPage page =pddDocument.getPage(i);
//               List<PDAnnotation> la = page.getAnnotations();
//               for (PDAnnotation anot : la) {
//                   if (anot instanceof PDAnnotationTextMarkup && ((
//                            anot.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT))
//                           || (anot.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE))))
//                       processHighlight((PDAnnotationTextMarkup) anot, page);
//               }
//               pddDocument.save("C:\\Users\\DELL\\OneDrive\\Textbook\\JavaScript DOM编程艺术.pdf.txt");
                int pageNum = i + 1;
                PDPage page = pddDocument.getPage(i);
                List<PDAnnotation> pdAnnotationList = page.getAnnotations();

                if (pdAnnotationList.isEmpty()) {
                    continue;
                }
                boolean hasWirtePage = false;
                for (PDAnnotation pdAnnotation : pdAnnotationList) {
                    if (pdAnnotation instanceof PDAnnotationTextMarkup && ((
                            pdAnnotation.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT))
                            || (pdAnnotation.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE)))) {
                        if (! hasWirtePage) {
                            write(pageNum + " ",documentName);
                            hasWirtePage = true;
                        }
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
                        stripper.addRegion(Integer.toString(i), awtRect);
                        stripper.extractRegions(page);
                        Optional<String> content = Optional.ofNullable(pdAnnotation.getContents());
                        for (String region : stripper.getRegions()) {
                            String highLight = stripper.getTextForRegion(region) + content.orElse("");
                            write(highLight + "\n",documentName);
                        }
                    }

                }
            }
            pddDocument.close();
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }

    public static void write(String message,String documentName) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(documentName+".txt",true), true);
        out.write(message);
        out.close();
    }
}
