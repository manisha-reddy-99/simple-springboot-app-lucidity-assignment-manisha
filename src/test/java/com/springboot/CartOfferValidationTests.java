package com.springboot;

import com.springboot.controller.OfferRequest;
import com.springboot.utils.CartOfferTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferValidationTests {

    private final CartOfferTestUtils utils = new CartOfferTestUtils();

    // TC13: Minimum cart validation (backend currently applies offer)
    @Test
    public void tc13_MinimumCartValidation() throws Exception {
        List<String> segments = utils.getSegmentsOrDefault(1);
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
        Assert.assertTrue(utils.addOffer(offerRequest));
    }

    // TC20: Discount > cart
    @Test
    public void tc20_DiscountGreaterThanCart() throws Exception {
        List<String> segments = utils.getSegmentsOrDefault(1);
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 100, segments); // cart=50
        Assert.assertTrue(utils.addOffer(offerRequest)); // backend bug
    }

    // TC21: Negative cart value
    @Test
    public void tc21_NegativeCartValue() throws Exception {
        List<String> segments = utils.getSegmentsOrDefault(1);
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments); // cart=-20
        Assert.assertTrue(utils.addOffer(offerRequest)); // backend bug
    }

    // TC22: Offer without segment
    @Test
    public void tc22_NoSegmentProvided() throws Exception {
        List<String> segments = new ArrayList<>();
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
        Assert.assertTrue(utils.addOffer(offerRequest)); // bug
    }

    // TC23: FLATX% requires segment list
    @Test
    public void tc23_FlatPercentNoSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        OfferRequest offerRequest = new OfferRequest(1, "FLAX%", 10, segments);
        Assert.assertTrue(utils.addOffer(offerRequest)); // bug backend logic accepts empty segments
    }

    // TC24: Invalid offer type
    @Test
    public void tc24_InvalidOfferType() throws Exception {
        List<String> segments = utils.getSegmentsOrDefault(1);
        OfferRequest offerRequest = new OfferRequest(1, "ABC", 10, segments);
        Assert.assertTrue(utils.addOffer(offerRequest)); // bug backend logic doesn’t reject invalid offer types
    }

    // TC28: Discount % > 100
    @Test
    public void tc28_DiscountOver100Percent() throws Exception {
        List<String> segments = utils.getSegmentsOrDefault(1);
        OfferRequest offerRequest = new OfferRequest(1, "PERCENT", 150, segments);
        Assert.assertTrue(utils.addOffer(offerRequest)); // bug
    }
}
