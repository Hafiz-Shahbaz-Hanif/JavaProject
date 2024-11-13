package com.DC.tests.sharedAssertions;

import com.DC.db.productVersioning.ExportsCollection;
import com.DC.objects.productVersioning.ExportRecord;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ExportResponse;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.awaitility.core.ConditionTimeoutException;
import org.testng.Assert;

import java.util.concurrent.TimeUnit;

import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;
import static org.awaitility.Awaitility.await;

public class ExportCoreAssertions {

    private static final ExportsCollection EXPORT_CORE_COLLECTION_NEW = new ExportsCollection();

    public static ExportRecord waitForExportToBeInDB(String expectedExportRecordId) {
        long timeout = 3;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            ExportRecord[] latestExportContainer = new ExportRecord[1];
            await().atMost(timeout, timeUnit).until(() -> {
                ExportRecord latestRecord = EXPORT_CORE_COLLECTION_NEW.getExportRecord(expectedExportRecordId);
                if (latestRecord != null) {
                    latestExportContainer[0] = latestRecord;
                    return true;
                } else {
                    return false;
                }
            });
            return latestExportContainer[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail("Export file was not generated within " + timeout + " " + timeUnit);
        }
        return null;
    }

    public static ExportRecord verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus expectedStatus, String exportId) {
        long timeout = 3;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            ExportRecord[] latestExportContainer = new ExportRecord[1];
            await().atMost(timeout, timeUnit).until(() -> {
                ExportRecord latestRecord = EXPORT_CORE_COLLECTION_NEW.getExportRecord(exportId);
                latestExportContainer[0] = latestRecord;
                return latestRecord.status.equals(expectedStatus);
            });
            return latestExportContainer[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail("Export status didn't change to " + expectedStatus + " within " + timeout + " " + timeUnit);
        }
        return null;
    }

    public static String verifyResponseReturnsAnExportId(String testCaseID, Response response) {
        ExportResponse responseBody = verifyEndpointReturnsCorrectObject(response, testCaseID, ExportResponse.class);
        Assert.assertNotNull(responseBody.exportId, "Export id was null");
        Assert.assertFalse(responseBody.exportId.isEmpty(), "Export id was empty");
        Assert.assertTrue(responseBody.success, "Success was false");
        Assert.assertEquals(responseBody.message, "OK", "Incorrect message returned");
        return responseBody.exportId;
    }

    public static void verifyResponseBodyHasExpectedError(ExportResponse response, String expectedError, String expectedResponseMessage) {
        Assert.assertFalse(response.success, "Success property was set to true");
        Assert.assertEquals(response.message, expectedResponseMessage, "Response message didn't match with expected msg");
        //Assert.assertEquals(response.data.error, expectedError, "Response error didn't match with expected error");
    }

    public static void verifyBasicExportCoreData(ExportRecord exportCoreRecord, Enums.ExportType expectedExportType, Enums.ExportSubType expectedExportSubType, String expectedCompanyId) {
        Assert.assertNotEquals(exportCoreRecord._version, 0, "Latest export version was 0");
        Assert.assertNotNull(exportCoreRecord.startedOn, "StartedOn value was null");
        Assert.assertNotNull(exportCoreRecord.completedOn, "CompletedOn value was null");
        Assert.assertEquals(exportCoreRecord.companyId, expectedCompanyId, "Company id didn't match with the expected company id");
        Assert.assertEquals(exportCoreRecord.trackingType, expectedExportType, "ExportRecord type didn't match with the expected type");
        Assert.assertEquals(exportCoreRecord.trackingSubType, expectedExportSubType, "ExportRecord subtype didn't match with the expected subtype");
        Assert.assertNotNull(exportCoreRecord.exportWorkbook, "Export workbook was null");
        Assert.assertNotNull(exportCoreRecord.exportWorkbook.link, "Download link was null");
        Assert.assertTrue(exportCoreRecord.errors.isEmpty(), "Error object was not empty");
    }

    public static void verifyErrorInExportRecordContainsExpectedMessage(ExportRecord exportRecord, String expectedErrorMsg) {
        Assert.assertFalse(exportRecord.errors.isEmpty(), "Error list was empty");
        Assert.assertTrue(exportRecord.errors.get(0).contains(expectedErrorMsg), "Error didn't have the expected message");
    }
}
