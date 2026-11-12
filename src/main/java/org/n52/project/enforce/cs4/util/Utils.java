package org.n52.project.enforce.cs4.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.n52.project.enforce.cdm.CdmEvidenceTypeRepository;
import org.n52.project.enforce.cdm.CdmObservationTypeRepository;
import org.n52.project.enforce.cdm.CdmValidationStatusRepository;
import org.n52.project.enforce.cdm.CommonDataModel;
import org.n52.project.enforce.cdm.CommonDataModelRepository;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasData;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasDataRepository;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasObservedProperty;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasObservedPropertyRepository;
import org.n52.project.enforce.cs4.api.impl.minka.Cs4MinkaData;
import org.n52.project.enforce.cs4.api.impl.minka.Cs4MinkaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class Utils {

    private Cs4MinkaDataRepository dataRepository;

    private CS4PlayasDataRepository cs4PlayasDataRepository;

    private CS4PlayasObservedPropertyRepository cs4PlayasobservedPropertyRepository;
    
    private CdmValidationStatusRepository cdmValidationStatusRepository;
    
    private CdmObservationTypeRepository cdmObservationTypeRepository;
    
    private CdmEvidenceTypeRepository cdmEvidenceTypeRepository;
    
    private CommonDataModelRepository commonDataModelRepository;

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static Logger LOG = LoggerFactory.getLogger(Utils.class);

    public Utils(Cs4MinkaDataRepository dataRepository, CS4PlayasDataRepository cs4PlayasDataRepository,
            CS4PlayasObservedPropertyRepository cs4PlayasobservedPropertyRepository,
            CommonDataModelRepository commonDataModelRepository,
            CdmEvidenceTypeRepository cdmEvidenceTypeRepository) {
        this.dataRepository = dataRepository;
        this.cs4PlayasDataRepository = cs4PlayasDataRepository;
        this.cs4PlayasobservedPropertyRepository = cs4PlayasobservedPropertyRepository;
        this.cdmEvidenceTypeRepository = cdmEvidenceTypeRepository;
        this.commonDataModelRepository = commonDataModelRepository;
    }

    public boolean ckeckIdIsInDb(UUID id) {
        Optional<Cs4MinkaData> possibleExistingDbData = dataRepository.findById(id);
        if (possibleExistingDbData.isPresent()) {
            return true;
        }
        return false;
    }

    public void createNewData(JsonNode jsonNode) {
        JsonNode time = jsonNode.path("time_observed_at");
        JsonNode locationNode = jsonNode.path("location");
        JsonNode speciesGuess = jsonNode.path("species_guess");
        JsonNode updatedAt = jsonNode.path("updated_at");
        JsonNode userId = jsonNode.path("user").path("id");
        ArrayNode identifications = (ArrayNode) jsonNode.path("identifications");
        JsonNode photoUrl = identifications.get(0).path("taxon").path("default_photo").path("url");
        String[] locationStringArray = locationNode.asText().split(",");
        double lat = Double.parseDouble(locationStringArray[0]);
        double lon = Double.parseDouble(locationStringArray[1]);
        Point location = geometryFactory.createPoint(new Coordinate(lon, lat));
        Cs4MinkaData dbData = new Cs4MinkaData(getId(jsonNode));
        dbData.setUserId(userId.asInt());
        if ((photoUrl != null) && !photoUrl.isMissingNode()) {
            try {
                dbData.setMediaUrl(new URI(photoUrl.asText()).toURL());
            } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
                LOG.error(e.getMessage());
            }
        }
        dbData.setLocation(location);
        dbData.setSpeciesName(speciesGuess.asText());
        try {
            dbData.setObservedDatetime(OffsetDateTime.parse(time.asText()));
            dbData.setUpdatedDatetime(OffsetDateTime.parse(updatedAt.asText()));
        } catch (Exception e) {
            LOG.info("Error while parsing dates.", e.getMessage());
        }
        dataRepository.saveAndFlush(dbData);
    }

    public UUID getId(JsonNode node) {
        JsonNode uuid = node.path("uuid");
        return UUID.fromString(uuid.asText());
    }

    public void updateData(JsonNode jsonNode) {
        JsonNode updatedAt = jsonNode.path("updated_at");
        OffsetDateTime newUpdatedAt = OffsetDateTime.parse(updatedAt.asText());
        UUID id = getId(jsonNode);
        Optional<Cs4MinkaData> dbDataOpt = dataRepository.findById(id);
        if (dbDataOpt.isPresent()) {
            Cs4MinkaData dbData = dbDataOpt.get();
            OffsetDateTime dbUpdatedAt = dbData.getUpdatedDatetime();
            if (newUpdatedAt.isAfter(dbUpdatedAt)) {
                dbData.setUpdatedDatetime(newUpdatedAt);
                JsonNode speciesGuessNode = jsonNode.path("species_guess");
                if(speciesGuessNode != null) {
                    dbData.setSpeciesName(speciesGuessNode.asText());
                }
                dataRepository.save(dbData);
                LOG.info(String.format("Data was with id %s was updated.", id.toString()));
            }
        }
    }

    public void readExcelFile(String excelFileAsString) throws IOException {
        readExcelFile(new ByteArrayInputStream(excelFileAsString.getBytes()));
    }
    
    public void readExcelFile(InputStream inputstream) throws IOException {
        File tmpExcelFile = File.createTempFile("excel", ".xlsx");
        IOUtils.copy(inputstream, new FileOutputStream(tmpExcelFile));
        Workbook workbook = new XSSFWorkbook(new FileInputStream(tmpExcelFile));
        Sheet sheet = workbook.getSheetAt(0);
        Row firstRow = sheet.getRow(0);
        short columnCount = firstRow.getLastCellNum();
        Map<Integer, String> propertyTypeMap = new HashMap<>();
        for (int k = 15; k < 92; k++) {
            Row row = sheet.getRow(k);
            Cell cell = row.getCell(1);
            switch (cell.getStringCellValue()) {
            case "Plastic", "Paper / Cardboard", "Wood (machined)", "Metal", "Glass", "Sanitary Waste", "Medical Waste", "Others":
                break;
            default:
                String cellValue = cell.getStringCellValue();
                propertyTypeMap.put(k, cellValue);
                Optional<CS4PlayasObservedProperty> potentialObservedPeoperty =
                        cs4PlayasobservedPropertyRepository.findById(k);
                if (potentialObservedPeoperty.isPresent()) {
                    break;
                }
                CS4PlayasObservedProperty observedProperty = new CS4PlayasObservedProperty(k, cellValue);
                cs4PlayasobservedPropertyRepository.saveAndFlush(observedProperty);
            }
        }
        CS4PlayasData data = null;
        Row row = null;
        Cell cell = null;
        for (int j = 2; j < columnCount; j++) {
            data = new CS4PlayasData();
            for (int k = 0; k < 15; k++) {
                row = sheet.getRow(k);
                cell = row.getCell(j);
                switch (k) {
                case 0:
                    data.setLocationName(cell.getStringCellValue());
                    break;
                case 1:
                    data.setId((int) cell.getNumericCellValue());
                    break;
                case 2:
                    data.setEntity(cell.getStringCellValue());
                    break;
                case 3:
                    try {
                        data.setCleaningDate(dateFormat.parse(cell.getStringCellValue()));
                    } catch (ParseException e) {
                        LOG.error(e.getMessage());
                    }
                    break;
                case 4:
                    data.setCountry(cell.getStringCellValue());
                    break;
                case 5:
                    data.setCommunity(cell.getStringCellValue());
                    break;
                case 6:
                    data.setProvince(cell.getStringCellValue());
                    break;
                case 7:
                    data.setMunicipality(cell.getStringCellValue());
                    break;
                case 8:
                    data.setCollectionType(cell.getStringCellValue());
                    break;
                case 9:
                    data.setProject(cell.getStringCellValue());
                    break;
                case 10:
                    data.setMonitoringLengthMeters((int) cell.getNumericCellValue());
                    break;
                case 11:
                    data.setInitialCoordinates(this.createPoint(cell.getStringCellValue()));
                    break;
                case 12:
                    data.setFinalCoordinates(this.createPoint(cell.getStringCellValue()));
                    break;
                case 13:
                    data.setTotalItems((int) cell.getNumericCellValue());
                    break;
                default:
                    break;
                }
            }
            String propertyName = null;
            for (int k = 15; k < 91; k++) {
                row = sheet.getRow(k);
                cell = row.getCell(j);
                propertyName = propertyTypeMap.get(k);
                if (propertyName == null) {
                    continue;
                }

                Optional<CS4PlayasObservedProperty> potentialObservedPeoperty =
                        cs4PlayasobservedPropertyRepository.findById(k);
                data.putObservedPropertyCount(potentialObservedPeoperty.get(), (int)cell.getNumericCellValue());
            }
            row = sheet.getRow(91);
            cell = row.getCell(j);
            data.setRemarks(cell.getStringCellValue());
            cs4PlayasDataRepository.saveAndFlush(data);
            LOG.info(String.format("Data with id %d added to cs4 data.", data.getId()));
        }
        workbook.close();
    }

    private Point createPoint(String pointAsString) {
        if (pointAsString != null) {
            String[] coordinateArray = pointAsString.split(",");
            if (coordinateArray.length == 2) {
                String latStrg = coordinateArray[1];
                String lngStrg = coordinateArray[0];
                if ((latStrg != null && !latStrg.isEmpty()) && (lngStrg != null && !lngStrg.isEmpty())) {
                    double lat = Double.parseDouble(latStrg);
                    double lng = Double.parseDouble(lngStrg);
                    return geometryFactory.createPoint(new Coordinate(lat, lng));
                }
            }
        }
        return geometryFactory.createPoint(new Coordinate(0, 0));
    }
    
    public void convertToCdm(JsonNode jsonNode, File dataDirectory) {

        Boolean authorityNotified;
        String authorityReference;
        String campaignId = "biodiversitat-marina-de-les-platges-de-barcelona";
        int caseStudyId = 4;
        String chainOfCustodyReference;
        String contributorId = jsonNode.path("user").path("id").asText();
        OffsetDateTime createdAt = OffsetDateTime.now();
        String description;
        OffsetDateTime endTime;
        String evidenceId = null;
        String geometry;
        Double locationAccuracyM = jsonNode.get("positional_accuracy").asDouble();
        OffsetDateTime modifiedAt = null;
        String observationId = jsonNode.path("uuid").asText();
        OffsetDateTime observationTime = null;
        String originalEvidenceHash = null;
        String originalRecordId;
        Boolean personalDataFlag = false;
        String processingActivityRef;
        String protocolId;
        String protocolVersion;
        String provenanceRecord;
        Double qualityScore;
        String recordVersion = "1";
        OffsetDateTime submissionTime = OffsetDateTime.now();
        String validatorId;
        String value;
        //N/A
        int sensitiveContentFlagId = 1;
        int observationTypeId = 1;
        int observedPropertyId = 1;
        int unitId;
        int categoryId;
        int locationSourceId;
        int contributorRoleId = 1;
        //mobile_app
        int collectionMethodId = 2;
        int evidenceTypeId;
        int validationStatusId = 1;
        int validationMethodId;
        //restricted
        int accessLevelId = 1;
        int retentionClassId;
        int complianceRelevanceId;
        int suspectedViolationTypeId;
        int regulatoryDomainId = 5;
        int evidenceStatusId;
        int evidenceAccessRestrictionId;
        int deviceOrSensorId;
        //restricted
        int licenceId = 1;
        
        JsonNode time = jsonNode.path("time_observed_at");
        JsonNode locationNode = jsonNode.path("location");
        JsonNode speciesGuess = jsonNode.path("species_guess");
        JsonNode updatedAt = jsonNode.path("updated_at");
        if (jsonNode.path("identifications") instanceof ArrayNode) {
            ArrayNode identifications = (ArrayNode) jsonNode.path("identifications");
            JsonNode photoUrl = identifications.get(0).path("taxon").path("default_photo").path("medium_url");
            if ((photoUrl != null) && !photoUrl.isMissingNode()) {
                String contentType = "";
                File file = new File(dataDirectory.getAbsolutePath() + "/" + UUID.randomUUID().toString());
                try (BufferedInputStream in = new BufferedInputStream(new URL(photoUrl.asText()).openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                    contentType = findContentType(photoUrl.asText());
                } catch (IOException | URISyntaxException e) {
                    LOG.error(e.getMessage());
                }
                byte[] data;
                try {
                    data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    byte[] hash = MessageDigest.getInstance("MD5").digest(data);                    
                    String evidenceType = getEvidenceType(contentType);
                    Optional<Integer> evidenceTypeIdFromDb = cdmEvidenceTypeRepository.getIdForName(evidenceType);
                    if(evidenceTypeIdFromDb.isPresent()) {
                        evidenceTypeId = evidenceTypeIdFromDb.get();
                    }
                    if(evidenceType.equals("photo")) {
                        evidenceId = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(data);
                    } else if(evidenceType.equals("video")) {
                        evidenceId = photoUrl.asText();
                    }
                    String checksum = new BigInteger(1, hash).toString(16);
                    originalEvidenceHash = checksum;
                } catch (IOException | NoSuchAlgorithmException e) {
                    LOG.error(e.getMessage());
                }
//                try {
//                    dbData.setMediaUrl(new URI(photoUrl.asText()).toURL());
//                } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
//                    LOG.error(e.getMessage());
//                }
            }
        }
        String[] locationStringArray = locationNode.asText().split(",");
        double lat = Double.parseDouble(locationStringArray[0]);
        double lon = Double.parseDouble(locationStringArray[1]);
        Point location = geometryFactory.createPoint(new Coordinate(lon, lat));
        geometry = new GeoJsonWriter().write(location);
        value = speciesGuess.asText();
        try {
            observationTime = OffsetDateTime.parse(time.asText());
            modifiedAt = OffsetDateTime.parse(updatedAt.asText());
        } catch (Exception e) {
            LOG.info("Error while parsing dates.", e.getMessage());
        }
        
        JsonNode qualityGradeNode = jsonNode.get("quality_grade");
        
        if (!qualityGradeNode.isMissingNode()) {
            String qualityGrade = qualityGradeNode.asText();
            switch (qualityGrade) {
            case "research": {
                contributorRoleId = 5;
            }
            default:
                contributorRoleId = 1;
            }
        }
        
        CommonDataModel dataModel = new CommonDataModel(caseStudyId, createdAt, modifiedAt, observationId, observationTime, personalDataFlag, recordVersion, submissionTime, observationTypeId, observedPropertyId, collectionMethodId, validationStatusId, accessLevelId, licenceId);
        dataModel.setSensitiveContentFlagId(sensitiveContentFlagId);
        dataModel.setCampaignId(campaignId);
        dataModel.setContributorId(contributorId);
        dataModel.setGeometry(geometry);
        dataModel.setValue(value);
        dataModel.setRegulatoryDomainId(regulatoryDomainId);
        dataModel.setLocationAccuracyM(locationAccuracyM);
        dataModel.setContributorRoleId(contributorRoleId);
        dataModel.setEvidenceId(evidenceId);
        dataModel.setOriginalEvidenceHash(originalEvidenceHash);
        commonDataModelRepository.saveAndFlush(dataModel);
    }
    
    private String getEvidenceType(String contentType) {

        String evidenceType = "";

        switch (contentType) {
        case MimeTypeUtils.IMAGE_JPEG_VALUE:
        case MimeTypeUtils.IMAGE_PNG_VALUE:
        case MimeTypeUtils.IMAGE_GIF_VALUE: {
            evidenceType = "photo";
            break;
        }
        case "video/quicktime": {
            evidenceType = "video";
            break;
        }
        default:
            evidenceType = "other";
        }
        return evidenceType;
    }
    
    private String findContentType(String url) throws IOException, URISyntaxException {
        String contentType = "";
        URLConnection connection = new URI(url).toURL().openConnection();
        contentType = connection.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            if (contentType.equals(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)) {
                String suffix = url.substring(url.lastIndexOf(".") + 1);
                if(suffix.equals("mov")) {
                    contentType = "video/quicktime";
                } else {
                    contentType = "image/" + suffix;
                }
            }
        }
        return contentType;
    }
    
}
