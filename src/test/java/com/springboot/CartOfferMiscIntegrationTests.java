package com.springboot;

import com.springboot.controller.OfferRequest;
import com.springboot.utils.CartOfferTestUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferMiscIntegrationTests {

    // TC38: Multiple FLAT offers for different restaurants
    @Ignore("Skipping until backend offers list is cleared between tests")
    @Test
    public void testMultipleRestaurantsOffer_FlatOfferPerRestaurant() throws Exception {

        // WORKAROUND: reset backend state with dummy offer
        OfferRequest dummy = new OfferRequest(9999, "FLATX", 1, Arrays.asList("p9"));
        CartOfferTestUtils.addOffer(dummy);  // flush previous offers

        Optional<String> userSegment = CartOfferTestUtils.getUserSegment(1);
        Assert.assertTrue(userSegment.isPresent());

        List<String> segments = new ArrayList<>();
        segments.add(userSegment.get());

        // NOW add fresh offers
        OfferRequest offerA = new OfferRequest(1, "FLATX", 10, segments);
        OfferRequest offerB = new OfferRequest(2, "FLATX", 20, segments);

        Assert.assertTrue(CartOfferTestUtils.addOffer(offerA));
        Assert.assertTrue(CartOfferTestUtils.addOffer(offerB));

        // restaurant 1 → must get 190 (but allOffers is not getting cleared after each testcase as I donot have control to controller, needs to be fixed.
        int responseA = CartOfferTestUtils.applyOffer(200, 1, 1);
        Assert.assertEquals(190, responseA);

        // restaurant 2 → must get 180
        int responseB = CartOfferTestUtils.applyOffer(200, 2, 1);
        Assert.assertEquals(180, responseB);
    }


    // TC42: Changing cart items after applying offer → discount must be re-evaluated
    @Test
    public void testCartChangeAfterOffer() throws Exception {

        Optional<String> userSegment = CartOfferTestUtils.getUserSegment(1);
        Assert.assertTrue(userSegment.isPresent());
        List<String> segments = Arrays.asList(userSegment.get());

        // STEP 1: Add offer
        OfferRequest offer = new OfferRequest(1, "FLATX", 20, segments);
        Assert.assertTrue(CartOfferTestUtils.addOffer(offer));

        // STEP 2: Apply offer with initial cart
        int finalAmountBefore = CartOfferTestUtils.applyOffer(200, 1, 1);

        // STEP 3: Cart changes ↓ now lower
        int finalAmountAfter = CartOfferTestUtils.applyOffer(80, 1, 1);

        // STEP 4: Offer must be re-evaluated
        Assert.assertNotEquals(finalAmountBefore, finalAmountAfter);
    }

}
