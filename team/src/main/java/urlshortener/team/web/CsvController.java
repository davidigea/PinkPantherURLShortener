package urlshortener.team.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.CsvRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@RestController
public class CsvController {

	@Autowired
	protected CsvRepository csvRepository;

	@Autowired
	protected ClickRepository clickRepository;


    @RequestMapping(value = "/uploadCSV", method = RequestMethod.POST)
    public void downloadCSV(HttpServletResponse response, @RequestParam("file") MultipartFile file,
                            HttpServletRequest request) throws IOException {

        String csvFileName = "mock.csv";

        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        List<String> uris = csvRepository.parserCsv(file);
        List<String> urisShorted = new ArrayList<>();
        for(String uri : uris){
            // Shortening uri
            // ...
            urisShorted.add("http://localhostMock:8080/123");
        }

        List<CsvFormat> csvList = csvRepository.createCsv(uris, urisShorted);

        // uses the Super CSV API to generate CSV data from the model data
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {
                "URIOriginal",
                "URIAcortada"
        };

        csvWriter.writeHeader(header);

        for (CsvFormat aFile: csvList) {
            csvWriter.write(aFile, header);
        }

        csvWriter.close();
    }
}