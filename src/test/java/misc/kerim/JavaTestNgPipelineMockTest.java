package misc.kerim;

import com.DC.testcases.BaseClass;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;


public class JavaTestNgPipelineMockTest extends BaseClass {

    @BeforeMethod(groups = {"smoke"})
    public void setUp(ITestContext testContext) throws Exception {
//        driver = initializeBrowser(testContext, false);
//        DCLoginPage loginPage = new DCLoginPage(driver);
//        loginPage.openLoginPage(driver, READ_CONFIG.getDcAppUrl());
//        System.out.println("");
        LOGGER.info("************* BEFORE METHOD IN TEST CLASS ***************");
    }

    @AfterMethod(groups = {"smoke"})
    public void killDriver() {
        quitBrowser();
        LOGGER.info("************* AFTER METHOD IN TEST CLASS ***************");
    }

    @Test(groups = {"smoke"})
    @Severity(SeverityLevel.BLOCKER)
    @Owner("Kerim Dogan")
    public void Api_Search_Service_Create_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
        LOGGER.info("************* WAITED 3 SECONDS ***************");
    }

    @Test(groups = {"smoke"})
    @Severity(SeverityLevel.MINOR)
    @Owner("Kerim Dogan")
    public void Api_Search_Service_Create_Duplicate_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
        LOGGER.info("************* WAITED 3 SECONDS ***************");
    }

    @Test(groups = {"smoke"})
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Kerim Dogan")
    public void Api_Search_Service_Update_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
        LOGGER.info("************* WAITED 3 SECONDS ***************");
    }





    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Kerim Dogan")
    public void Api_Search_Service_No_Group_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
        LOGGER.info("************* WAITED 3 SECONDS ***************");
    }


    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Kerim Dogan")
    public void Api_Search_Service_Both_Group_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
        LOGGER.info("************* WAITED 3 SECONDS ***************");
    }





    @Test(groups = {"regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Owner("Kerim Dogan")
    public void Api_Search_Create_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.MINOR)
    @Owner("Kerim Dogan")
    public void Api_Search_Create_Duplicate_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(1 > 2);
        Thread.sleep(3000);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Kerim Dogan")
    public void Api_Search_Update_Search_Term_Test() throws InterruptedException {
        Assert.assertTrue(2 > 1);
        Thread.sleep(3000);
    }

}