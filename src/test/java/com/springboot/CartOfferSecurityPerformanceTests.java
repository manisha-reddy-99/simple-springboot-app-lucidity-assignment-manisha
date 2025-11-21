package com.springboot;

import com.springboot.controller.OfferRequest;
import com.springboot.utils.CartOfferTestUtils;
import org.junit.Assert;
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
public class CartOfferSecurityPerformanceTests {

    // TC30: Unauthorized user
    @Test
    public void tc30_UnauthorizedUser() throws Exception {
        Optional<String> userSegment = CartOfferTestUtils.getUserSegment(1);
        Assert.assertTrue(userSegment.isPresent());

        List<String> segments = new ArrayList<>();
        segments.addAll(Arrays.asList(userSegment.get().split(",")));

        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);

        // Using utility method for unauthenticated request
        Assert.assertFalse(CartOfferTestUtils.addOfferWithoutAuth(offerRequest));
    }

    // TC36: High load (Ideally should be done in other tools like JMeter)
    @Test
    public void tc36_HighLoad() throws Exception {
        Optional<String> userSegment = CartOfferTestUtils.getUserSegment(1);
        Assert.assertTrue(userSegment.isPresent());

        List<String> segments = new ArrayList<>();
        segments.addAll(Arrays.asList(userSegment.get().split(",")));

        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);

        int success = 0;
        for (int i = 0; i < 500; i++) {
            if (CartOfferTestUtils.addOffer(offerRequest)) success++;
        }
        Assert.assertEquals(500, success);
    }
}
