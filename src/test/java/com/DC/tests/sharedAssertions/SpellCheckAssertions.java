package com.DC.tests.sharedAssertions;

import com.DC.objects.insightslegacy.Product;
import com.DC.objects.insightslegacy.ProductProperties;
import com.DC.utilities.apiEngine.apiServices.insights.ProductService;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.SpellCheckModal;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SpellCheckAssertions {

    public final String TITLE_FIELD = "Product Title";
    public final String DESCRIPTION_FIELD = "Product Description";
    public final String BULLET_ONE_FIELD = "Bullet 1";
    public final String MISSPELLED_WORD_ALL_PRODUCTS = "WROKING";
    public final String MISSPELLED_WORD_FIRST_PRODUCT = "AGRESSIVE";
    public final String MISSPELLED_WORD_SECOND_PRODUCT = "ACCROSS";
    public final String CORRECT_WORD_FIRST_PRODUCT = "WORKING";
    SpellCheckModal spellCheckModal;

    public SpellCheckAssertions(SpellCheckModal spellCheckModalDriver) {
        spellCheckModal = spellCheckModalDriver;
    }

    public void verifyReplacingOnlyInSingleOccurrenceWorksAsExpected( String titleInFirstProduct, String descriptionInFirstProduct, String bulletOneInFirstProduct ) throws InterruptedException {
        spellCheckModal.selectReplacement( CORRECT_WORD_FIRST_PRODUCT, Enums.SpellCheckOption.ThisOccurenceOnly );
        String fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        Assert.assertNotEquals( titleInFirstProduct, fieldValueInModal, TITLE_FIELD + " value doesn't match with the expected value after selecting replacement" );

        verifyFieldValueWasNotModifiedInOtherFields( descriptionInFirstProduct, DESCRIPTION_FIELD );
        verifyFieldValueWasNotModifiedInOtherFields( bulletOneInFirstProduct, BULLET_ONE_FIELD );

        ArrayList<String> statuses = spellCheckModal.getStatusOfAllWordOccurrences( MISSPELLED_WORD_ALL_PRODUCTS );
        int updatedItemsCount = statuses.stream().filter( status -> status.equals( "Updated" ) ).toArray().length;
        int expectedUpdatedItemsCount = 1;
        Assert.assertEquals(expectedUpdatedItemsCount, updatedItemsCount, "There were more than " + expectedUpdatedItemsCount +" updated items" );

        ArrayList<String> wordsInPanel = spellCheckModal.getWordsInSpellCheckPanel();
        boolean activeWordIsVisible = spellCheckModal.clickNextItemIcon().isThereAnActiveWord();
        Assert.assertTrue( activeWordIsVisible, "Next item wasn't activated after clicking on next item button" );
        String activeWord = spellCheckModal.getActiveWord();
        Assert.assertEquals(wordsInPanel.get(1), activeWord, "Expected next item was not activated" );

        spellCheckModal.clickUndoButton();
        fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        Assert.assertEquals( titleInFirstProduct, fieldValueInModal, TITLE_FIELD + "value didn't change back to original after undoing replacement" );

        statuses = spellCheckModal.getStatusOfAllWordOccurrences( MISSPELLED_WORD_ALL_PRODUCTS );
        updatedItemsCount = statuses.stream().filter( status -> status.equals( "Updated" ) ).toArray().length;
        Assert.assertEquals( updatedItemsCount, 0, "There were more than zero updated items" );

        activeWordIsVisible = spellCheckModal.clickNextItemIcon()
                .clickNextItemIcon()
                .clickPreviousItemIcon()
                .isThereAnActiveWord();
        Assert.assertTrue( activeWordIsVisible, "None of the words in panel were activated after clicking on previous item button" );
        activeWord = spellCheckModal.getActiveWord();
        Assert.assertEquals( wordsInPanel.stream().findFirst().orElseThrow(), activeWord, "Expected previous item was not activated" );
    }

    public void verifyReplacingAllOccurrencesInFieldWorksAsExpected( String titleInFirstProduct, String descriptionInFirstProduct, String bulletOneInFirstProduct, int occurrencesOfMisspelledWordInTitle ) throws InterruptedException {
        spellCheckModal.selectReplacement( CORRECT_WORD_FIRST_PRODUCT, Enums.SpellCheckOption.AllOccurrencesInField );
        String fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        String expectedTitleValue = titleInFirstProduct.replace( MISSPELLED_WORD_ALL_PRODUCTS, CORRECT_WORD_FIRST_PRODUCT );
        Assert.assertEquals( expectedTitleValue, fieldValueInModal, TITLE_FIELD + " value doesn't match with the expected value after selecting replacement" );

        verifyFieldValueWasNotModifiedInOtherFields( descriptionInFirstProduct, DESCRIPTION_FIELD );
        verifyFieldValueWasNotModifiedInOtherFields( bulletOneInFirstProduct, BULLET_ONE_FIELD );

        ArrayList<String> statuses = spellCheckModal.getStatusOfAllWordOccurrences( MISSPELLED_WORD_ALL_PRODUCTS );
        int updatedItemsCount = statuses.stream().filter( status -> status.equals( "Updated" ) ).toArray().length;
        Assert.assertEquals( occurrencesOfMisspelledWordInTitle, updatedItemsCount, "Updated items count doesn't equal with the expected count" );
    }

    public void verifyReplacingAllOccurrencesInProductWorksAsExpected( String titleInFirstProduct, String descriptionInFirstProduct, String bulletOneInFirstProduct, String titleInSecondProduct, int occurrencesOfMisspelledWordInFirstProduct ) throws InterruptedException {
        spellCheckModal.clickUndoButton()
                .clickNextItemIcon()
                .selectReplacement( CORRECT_WORD_FIRST_PRODUCT, Enums.SpellCheckOption.AllOccurrencesOnProduct );
        String expectedTitleValue = titleInFirstProduct.replace( MISSPELLED_WORD_ALL_PRODUCTS, CORRECT_WORD_FIRST_PRODUCT );
        String fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        Assert.assertEquals( expectedTitleValue, fieldValueInModal, TITLE_FIELD + " value doesn't match with the expected value after selecting replacement" );

        fieldValueInModal = spellCheckModal.getFieldValue( DESCRIPTION_FIELD );
        String expectedDescriptionValue = descriptionInFirstProduct.replace( MISSPELLED_WORD_ALL_PRODUCTS, CORRECT_WORD_FIRST_PRODUCT );
        Assert.assertEquals( expectedDescriptionValue, fieldValueInModal, DESCRIPTION_FIELD + " value doesn't match with the expected value after selecting replacement" );

        fieldValueInModal = spellCheckModal.getFieldValue( BULLET_ONE_FIELD );
        String expectedBulletOneValue = bulletOneInFirstProduct.replace( MISSPELLED_WORD_ALL_PRODUCTS, CORRECT_WORD_FIRST_PRODUCT );
        Assert.assertEquals( expectedBulletOneValue, fieldValueInModal, BULLET_ONE_FIELD + " value doesn't match with the expected value after selecting replacement" );

        ArrayList<String> statuses = spellCheckModal.getStatusOfAllWordOccurrences( MISSPELLED_WORD_ALL_PRODUCTS );
        int updatedItemsCount = statuses.stream().filter( status -> status.equals( "Updated" ) ).toArray().length;
        Assert.assertEquals( occurrencesOfMisspelledWordInFirstProduct, updatedItemsCount, "Updated items count doesn't equal with the expected count" );

        // Verify other product doesn't have changes
        spellCheckModal.clickNextProductButton();
        fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        Assert.assertEquals( titleInSecondProduct, fieldValueInModal, TITLE_FIELD + " value doesn't match with the expected value after selecting replacement" );
    }

    public void verifyReplacingAllOccurrencesOnAllProductsWorksAsExpected( int expectedUpdatedItemsCount ) throws InterruptedException {
        ArrayList<String> statuses = spellCheckModal.clickUndoButton()
                .clickNextItemIcon()
                .selectReplacement( CORRECT_WORD_FIRST_PRODUCT, Enums.SpellCheckOption.AllOccurrencesOnAllProducts )
                .getStatusOfAllWordOccurrences( MISSPELLED_WORD_ALL_PRODUCTS );
        int updatedItemsCount = statuses.stream().filter( status -> status.equals( "Updated" ) ).toArray().length;
        Assert.assertEquals( expectedUpdatedItemsCount, updatedItemsCount, "Wrong updated items count " + expectedUpdatedItemsCount+ " updated items" );
    }

    public void verifyTypingReplacementWorksAsExpected() throws InterruptedException {
        spellCheckModal.clickOnWord( MISSPELLED_WORD_SECOND_PRODUCT );
        String replacement = "AC";
        String descriptionValue = spellCheckModal.getFieldValue( DESCRIPTION_FIELD );
        String bulletOneValue = spellCheckModal.getFieldValue( BULLET_ONE_FIELD );
        spellCheckModal.typeReplacement( replacement, Enums.SpellCheckOption.ThisOccurenceOnly );
        String fieldValueInModal = spellCheckModal.getFieldValue( TITLE_FIELD );
        String expectedTitleValue = fieldValueInModal.replace( MISSPELLED_WORD_SECOND_PRODUCT, replacement );
        Assert.assertEquals( expectedTitleValue, fieldValueInModal, TITLE_FIELD + " value doesn't match with the expected value after inserting replacement" );
        verifyFieldValueWasNotModifiedInOtherFields( descriptionValue, DESCRIPTION_FIELD );
        verifyFieldValueWasNotModifiedInOtherFields( bulletOneValue, BULLET_ONE_FIELD );
    }

    public void verifyButtonStatesWorkAsExpected() throws InterruptedException {
        boolean activeWordIsVisible = spellCheckModal.clickLastWordInPanel().isThereAnActiveWord();
        Assert.assertTrue( activeWordIsVisible, "Word wasn't activated after clicking last word in panel" );
        verifyPreviousItemIconState( true );
        verifyNextItemIconState( false );
        verifyPreviousProductButtonState( true );
        verifyNextProductButtonState( false );

        activeWordIsVisible = spellCheckModal.clickFirstWordInPanel().isThereAnActiveWord();
        Assert.assertTrue( activeWordIsVisible, "Word wasn't activated after clicking first word in panel" );

        verifyPreviousItemIconState( false );
        verifyNextItemIconState( true );
        verifyPreviousProductButtonState( false );
        verifyNextProductButtonState( true );
    }

    public String getExpectedState( boolean shouldElementBeEnabled ) {
        return shouldElementBeEnabled ? "enabled" : "disabled";
    }

    public void verifyPreviousItemIconState( boolean iconShouldBeEnabled ) {
        boolean elementIsEnabled = spellCheckModal.isPreviousItemIconEnabled();
        String expectedStatus = getExpectedState( iconShouldBeEnabled );
        Assert.assertEquals(elementIsEnabled, iconShouldBeEnabled, "Icon was not " + expectedStatus);
    }

    public void verifyNextItemIconState( boolean iconShouldBeEnabled ) {
        boolean elementIsEnabled = spellCheckModal.isNextItemIconEnabled();
        String expectedStatus = getExpectedState( iconShouldBeEnabled );
        Assert.assertEquals(elementIsEnabled, iconShouldBeEnabled, "Icon was not " + expectedStatus);
    }

    public void verifyPreviousProductButtonState( boolean buttonShouldBeEnabled ) {
        boolean elementIsEnabled = spellCheckModal.isPreviousProductButtonEnabled();
        String expectedStatus = getExpectedState(buttonShouldBeEnabled);
        Assert.assertEquals(elementIsEnabled, buttonShouldBeEnabled, "Icon was not " + expectedStatus);
    }

    public void verifyNextProductButtonState( boolean buttonShouldBeEnabled ) {
        boolean elementIsEnabled = spellCheckModal.isNextProductButtonEnabled();
        String expectedStatus = getExpectedState( buttonShouldBeEnabled );
        Assert.assertEquals(elementIsEnabled, buttonShouldBeEnabled, "Icon was not " + expectedStatus);
    }

    public void verifyUserCanNavigateToNextAndPreviousProducts() throws InterruptedException {
        boolean elementIsEnabled;
        do {
            spellCheckModal.clickNextProductButton();
            Assert.fail("Something went wrong while navigating to next product");
            elementIsEnabled = spellCheckModal.isNextProductButtonEnabled();
        } while ( elementIsEnabled );

        elementIsEnabled = spellCheckModal.isPreviousProductButtonEnabled();
        do {
            spellCheckModal.clickPreviousProductButton();
            Assert.fail("Something went wrong while navigating to next product");
            elementIsEnabled = spellCheckModal.isPreviousProductButtonEnabled();
        } while ( elementIsEnabled );
    }

    public void verifyStagedChanges(HashMap<String, String> propertyIdAndExpectedValue, String productId, String jwt ) throws Exception {
        Product productDetails = ProductService.getProductPropertiesAndStagedChanges(jwt, productId);
        String productUniqueId = Arrays.stream(productDetails.product.properties).filter(prop -> prop.id.equals("unique_id")).findFirst().orElseThrow().values[0];        ProductProperties[] stagedChanges = productDetails.product.stagedChanges;
        for (String property : propertyIdAndExpectedValue.keySet()) {
            String stagedPropertyValue = Arrays.stream(stagedChanges).filter(prop -> prop.id.equals(property)).findFirst().orElseThrow().values[0];            Assert.assertNotNull(stagedPropertyValue, propertyIdAndExpectedValue.get(property) + " was not staged in product " + productUniqueId);
            Assert.assertEquals(propertyIdAndExpectedValue.get(property), stagedPropertyValue, "Value of property " + property + " doesn't match with the expected value");
        }
    }

    public void verifyFieldValueWasNotModifiedInOtherFields( String expectedValue, String fieldToTest ) {
        var fieldValueInModal = spellCheckModal.getFieldValue( fieldToTest );
        Assert.assertEquals( expectedValue, fieldValueInModal, fieldToTest + " value changed after replacing word only in a single occurrence" );
    }
}
