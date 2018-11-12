package com.samsfotx.demopdfreader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.math.BigDecimal;

@SpringBootApplication
@Slf4j
public class DemoPdfReaderApplication {

    @Value("${document.name}")
    private String documentName;

    public static void main(String[] args) {
        SpringApplication.run(DemoPdfReaderApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(){
        return args -> {
            try (PDDocument document = PDDocument.load(new File(documentName))) {
                if (!document.isEncrypted()) {
                    final PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);
                    final PDFTextStripper pdfTextStripper = new PDFTextStripper();
                    final String pdfFileInText = pdfTextStripper.getText(document);
                    // split by whitespace
                    final String lines[] = pdfFileInText.split("\\r?\\n");
                    BigDecimal subTotal = BigDecimal.ZERO;
                    BigDecimal salesTax = BigDecimal.ZERO;
                    BigDecimal totalDue = BigDecimal.ZERO;
                    
                    for (String line : lines) {
                        line = line.trim();
                        log.info("{}",line);

                        if(line.startsWith("SUBTOTAL")){
                            subTotal = new BigDecimal(line.substring(line.indexOf('$')+1));
                        }

                        if(line.startsWith("SALES TAX")){
                            salesTax = new BigDecimal(line.substring(line.indexOf('$')+1));
                        }

                        if(line.startsWith("TOTAL DUE")){
                            totalDue = new BigDecimal(line.substring(line.indexOf('$')+1));
                        }
                    }

                    log.info("Sub Total {}", subTotal);
                    log.info("Sales Tax {}", salesTax);
                    log.info("Total Due {}", totalDue);


                }

            }
        };
    }
}
