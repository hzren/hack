package com.hzren.hack.buy_room;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String string = null;
        try {
            PDFParser pdfParser = new PDFParser(new RandomAccessFile(new File("E:\\hzren\\Documents\\xyt.pdf"), "r"));
            pdfParser.parse();
            PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
            PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
            string = pdfTextStripper.getText(pdDocument);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        };
        System.out.println(string);

        string = string.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
        string = string.replace("XYT", "ABSXYT");
        String[] lines = string.split("ABS");

        ArrayList<String> tabs = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isBlank(line)){
                continue;
            }
            if (!line.startsWith("XYT")){
                continue;
            }
            String[] t1s = line.split(" ");
            for (String s : t1s) {
                if (StringUtils.isBlank(s)){
                    continue;
                }
                String[] s2s = s.split("，");
                for (String ss : s2s) {
                    if (StringUtils.isBlank(ss)){
                        continue;
                    }
                    tabs.add(ss);
                }
            }
        }

        ArrayList<String> idcards = new ArrayList<>();


        ArrayList<String> processedTabs = new ArrayList<>();
        for (String tab : tabs) {
            tab = tab.trim();
            if (StringUtils.isNotEmpty(tab)){
                processedTabs.add(tab);
            }
        }

        Object[] backs = processedTabs.toArray();
        for (int i = 0; i < backs.length; i++) {
            String stab = (String) backs[i];
            stab = stab.trim();
            if (stab.startsWith("XYT")){
                if (stab.length() != 8){
                    String[] subs = null;
                    if (stab.contains("是")){
                        subs = stab.split("是");
                    }else {
                        subs = stab.split("否");
                    }
                    subs = subs[0].split("\\*", 2);
                    String bianhao = stab.substring(0, 8);
                    String idcard = subs[1];
                    char c = idcard.charAt(0);
                    if (c < '0' || c > '9'){
                        idcard = idcard.substring(1);
                    }
                    idcards.add(idcard);
                    System.out.println(bianhao + " --- " + idcard);
                }else {
                    String idcard = (String) backs[i+2];
                    System.out.println(stab + " --- " + idcard);
                    idcards.add(idcard);
                }
            }
        }
        System.out.println(idcards.size());
    }
}